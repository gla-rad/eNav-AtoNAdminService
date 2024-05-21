/*
 * Copyright (c) 2024 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grad.eNav.atonAdminService.services.secom;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortedSetSortField;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.grad.eNav.atonAdminService.models.domain.s201.S201Dataset;
import org.grad.eNav.atonAdminService.models.domain.secom.SubscriptionRequest;
import org.grad.eNav.atonAdminService.models.dtos.datatables.DtPagingRequest;
import org.grad.eNav.atonAdminService.models.enums.DatasetOperation;
import org.grad.eNav.atonAdminService.repos.SecomSubscriptionRepo;
import org.grad.eNav.atonAdminService.services.S100ExchangeSetService;
import org.grad.eNav.atonAdminService.services.UnLoCodeService;
import org.grad.secom.core.exceptions.SecomNotFoundException;
import org.grad.secom.core.exceptions.SecomValidationException;
import org.grad.secom.core.models.EnvelopeUploadObject;
import org.grad.secom.core.models.UploadObject;
import org.grad.secom.core.models.enums.AckRequestEnum;
import org.grad.secom.core.models.enums.ContainerTypeEnum;
import org.grad.secom.core.models.enums.SECOM_DataProductType;
import org.grad.secom.core.models.enums.SubscriptionEventEnum;
import org.grad.secom.springboot3.components.SecomClient;
import org.hibernate.search.backend.lucene.LuceneExtension;
import org.hibernate.search.backend.lucene.search.sort.dsl.LuceneSearchSortFactory;
import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.query.SearchQuery;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.scope.SearchScope;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.spatial4j.context.jts.JtsSpatialContext;
import org.locationtech.spatial4j.shape.jts.JtsGeometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * The SECOM Subscription Service Class.
 * <p/>
 * A service to handle the incoming SECOM subscription requests. Each
 * subscription is persisted in the database and is handled appropriately
 * as specified by the SECOM standard.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Service
@Slf4j
public class SecomSubscriptionService implements MessageHandler {

    @Value("${gla.rad.service.secom.subscriptions.restrictDuplicates:false}")
    boolean restrictDuplicateSubscriptions;

    /**
     * The Entity Manager Factory.
     */
    @Autowired
    EntityManagerFactory entityManagerFactory;

    /**
     * The UN/LoCode Service.
     */
    @Autowired
    UnLoCodeService unLoCodeService;

    /**
     * The SECOM Service.
     */
    @Autowired
    SecomService secomService;

    /**
     * The SECOM Subscription Notification Service.
     */
    @Autowired
    SecomSubscriptionNotificationService secomSubscriptionNotificationService;

    /**
     * The S-100 Exchange Set Service.
     */
    @Autowired
    S100ExchangeSetService s100ExchangeSetService;

    /**
     * The SECOM Subscription Repo.
     */
    @Autowired
    SecomSubscriptionRepo secomSubscriptionRepo;

    /**
     * The S-201 Dataset Channel to publish the published data to.
     */
    @Autowired
    @Qualifier("s201PublicationChannel")
    PublishSubscribeChannel s201PublicationChannel;

    /**
     * The S-201 Dataset Channel to publish the deleted data to.
     */
    @Autowired
    @Qualifier("s201RemovalChannel")
    PublishSubscribeChannel s201RemovalChannel;

    // Class Variables
    EntityManager entityManager;

    /**
     * The service post-construct operations where the handler auto-registers
     * it-self to the S-201 publication channel.
     */
    @PostConstruct
    public void init() {
        log.info("SECOM Subscription Service is booting up...");
        this.entityManager = this.entityManagerFactory.createEntityManager();
        this.s201PublicationChannel.subscribe(this);
        this.s201RemovalChannel.subscribe(this);
    }

    /**
     * When shutting down the application we need to make sure that all
     * threads have been gracefully shutdown as well.
     */
    @PreDestroy
    public void destroy() {
        log.info("SECOM Subscription Service is shutting down...");
        if(this.entityManager != null) {
            this.entityManager.close();
        }
        if (this.s201PublicationChannel != null) {
            this.s201PublicationChannel.destroy();
        }
        if (this.s201RemovalChannel != null) {
            this.s201RemovalChannel.destroy();
        }
    }

    /**
     * This is a simple handler for the incoming messages. This is a generic
     * handler for any type of Spring Integration messages, but it should really
     * only be used for the ones containing S-201 message payloads.
     *
     * @param message               The message to be handled
     * @throws MessagingException   The Messaging exceptions that might occur
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        // Get the headers of the incoming message
        final SECOM_DataProductType contentType = Optional.of(message)
                .map(Message::getHeaders)
                .map(headers -> headers.get(MessageHeaders.CONTENT_TYPE, SECOM_DataProductType.class))
                .orElse(null);
        final DatasetOperation datasetOperation = Optional.of(message)
                .map(Message::getHeaders)
                .map(headers -> headers.get("operation", DatasetOperation.class))
                .orElse(DatasetOperation.OTHER);

        // Only listen to valid content types
        if(Objects.isNull(contentType)) {
            return;
        }

        // Handle only messages that seem valid
        if(SECOM_DataProductType.S201.equals(contentType) && message.getPayload() instanceof S201Dataset s201Dataset) {
            // Get the payload of the incoming message

            // A simple debug message
            log.debug(String.format("SECOM Subscription Service received an S-201 dataset %s with UUID: %s.",
                    datasetOperation.getOperation(),
                    s201Dataset.getUuid()));

            // Handle based on whether this is a deletion or not
            if(!datasetOperation.isWithdrawal()) {
                // Get the matching subscriptions and inform them of the update
                this.findAll(ContainerTypeEnum.S100_DataSet,
                                SECOM_DataProductType.S201,
                                s201Dataset.getDatasetIdentificationInformation().getProductEdition(),
                                s201Dataset.getUuid(),
                                s201Dataset.getGeometry(),
                                s201Dataset.getLastUpdatedAt())
                        .forEach(subscription -> {
                            try {
                                this.sendToSubscription(subscription, s201Dataset);
                            } catch (Exception ex) {
                                log.error("Error while handling subscription with UUID {} for {}: {}",
                                        subscription.getUuid(), subscription.getClientMrn(), ex.getMessage());
                            }
                        });
            } else {
                // Get the matching subscriptions and inform them of the deletion
                this.findAll(null,
                                null,
                                null,
                                s201Dataset.getUuid(),
                                null,
                                null)
                        .stream()
                        .map(SubscriptionRequest::getUuid)
                        .forEach(uuid -> {
                            try {
                                this.delete(uuid);
                            } catch (Exception ex) {
                                log.error("Error while removing subscription for with UUID {}: {}",
                                        uuid, ex.getMessage());
                            }
                        });
            }
        }
        else {
            log.warn("Aids to Navigation Service received a publish-subscribe message with erroneous format.");
        }
    }

    /**
     * Get all the Subscription Requests in a search list.
     *
     * @param containerType         the container type of the requested subscriptions
     * @param dataProductType       the SECOM data product type of the requested subscriptions
     * @param productVersion        the product version of the requested subscriptions
     * @param dataReference         the UUID data reference matched by the requested subscriptions
     * @param geometry              the geometry intersecting with the requested subscriptions
     * @param timestamp             the timestamp for which the requested subscriptions are valid
     * @return the list of Subscription Requests
     */
    @Transactional(readOnly = true)
    public List<SubscriptionRequest> findAll(ContainerTypeEnum containerType,
                                             SECOM_DataProductType dataProductType,
                                             String productVersion,
                                             UUID dataReference,
                                             Geometry geometry,
                                             LocalDateTime timestamp) {
        log.debug("Request to get Subscription Requests in a search");
        // Create the search query - always sort by name
        SearchQuery<SubscriptionRequest> searchQuery = this.getSubscriptionRequestSearchQuery(
                containerType,
                dataProductType,
                productVersion,
                dataReference,
                geometry,
                timestamp,
                new Sort(new SortedSetSortField("uuid", false))
        );

        // Map the results to a paged response
        return searchQuery.fetchAll()
                .hits();
    }

    /**
     * Handles a datatables pagination request and returns the SECOM
     * subscription results list in an appropriate format to be viewed by a
     * datatables jQuery table.
     * <p/>
     *
     * @param dtPagingRequest the Datatables pagination request
     * @return the Datatables paged response
     */
    @Transactional(readOnly = true)
    public Page<SubscriptionRequest> handleDatatablesPagingRequest(DtPagingRequest dtPagingRequest) {
        log.debug("Request to get SECOM Subscriptions in a Datatables pageable search");
        // Create the search query
        final SearchQuery<SubscriptionRequest> searchQuery = this.getDatasetSearchQueryByText(
                dtPagingRequest.getSearch().getValue(),
                dtPagingRequest.getLucenceSort(List.of())
        );

        // Map the results to a paged response
        return Optional.of(searchQuery)
                .map(query -> query.fetch(dtPagingRequest.getStart(), dtPagingRequest.getLength()))
                .map(searchResult -> new PageImpl<>(searchResult.hits(), dtPagingRequest.toPageRequest(), searchResult.total().hitCount()))
                .orElseGet(() -> new PageImpl<>(Collections.emptyList(), dtPagingRequest.toPageRequest(), 0));
    }


    /**
     * Creates a new SECOM subscription and persists its information in the
     * database.
     *
     * @param subscriptionRequest the subscription request
     * @return the subscription request generated
     */
    public SubscriptionRequest save(String mrn, SubscriptionRequest subscriptionRequest) {
        log.debug("Request from MRN {} to save SECOM subscription {}", mrn, subscriptionRequest.getUuid());

        // Sanity Check
        Optional.ofNullable(mrn)
                .orElseThrow(() -> new SecomValidationException("Cannot raise new subscription requests without a provided client MRN"));

        // Normally we should not mind having multiple subscriptions per client
        // but specifically for test cases, this could lead to complicated
        // management on the client side, so we should be able to only allow one
        // subscription per client automatically.
        if(this.restrictDuplicateSubscriptions) {
            this.secomSubscriptionRepo.findByClientMrn(mrn)
                    .map(SubscriptionRequest::getUuid)
                    .ifPresent(this::delete);
        }

        // Populate the subscription dataset and geometry
        subscriptionRequest.setClientMrn(mrn);
        subscriptionRequest.updateSubscriptionGeometry(this.unLoCodeService);

        // Now save the request
        final SubscriptionRequest result = this.secomSubscriptionRepo.save(subscriptionRequest);

        // Inform to the subscription client (identify through MRN) - asynchronous
        this.secomSubscriptionNotificationService.sendNotification(subscriptionRequest.getClientMrn(),
                result.getUuid(),
                SubscriptionEventEnum.SUBSCRIPTION_CREATED);

        // Now save for each type
        return result;
    }

    /**
     * Removes and existing SECOM subscription from the persisted entries in
     * the database if found and return an output message.
     *
     * @param uuid the UUID of the subscription to be removed
     * @return the subscription identifier UUID removed
     */
    public UUID delete(@NotNull UUID uuid) {
        log.debug("Request to delete SECOM subscription with UUID : {}", uuid);

        // Look for the subscription and delete it if found
        final SubscriptionRequest subscriptionRequest = Optional.of(uuid)
                .flatMap(this.secomSubscriptionRepo::findById)
                .orElseThrow(() -> new SecomNotFoundException(uuid.toString()));

        // Delete the subscription
        this.secomSubscriptionRepo.delete(subscriptionRequest);

        // Inform to the subscription client (identify through MRN) - asynchronous
        this.secomSubscriptionNotificationService.sendNotification(subscriptionRequest.getClientMrn(),
                subscriptionRequest.getUuid(),
                SubscriptionEventEnum.SUBSCRIPTION_REMOVED);

        // If all OK, then return the subscription UUID
        return subscriptionRequest.getUuid();
    }

    /**
     * This function handles the operation of sending the updated list of
     * the received S-201 Aids to Navigation entries to the provided
     * subscription. This involved contacting the SECOM service registry in
     * order to find our the correct endpoint and then the SECOM upload
     * interface of the discovered client (if a valid registration is returned)
     * will be utilised.
     *
     * @param subscriptionRequest   the subscription request
     * @param s201Dataset           the S-201 dataset to be sent to the subscription
     */
    protected void sendToSubscription(SubscriptionRequest subscriptionRequest, S201Dataset s201Dataset) {
        // Make sure we also have an MRN for the subscribed client
        if(Objects.isNull(subscriptionRequest.getClientMrn())) {
            log.warn("Subscription request found for S-201 dataset updates but no client MRN");
            return;
        }

        // Identify the subscription client if possible through the client MRN
        final SecomClient secomClient = this.secomService.getClient(subscriptionRequest.getClientMrn());

        // Build the data envelope
        EnvelopeUploadObject envelopeUploadObject = new EnvelopeUploadObject();
        envelopeUploadObject.setDataProductType(SECOM_DataProductType.S201);
        envelopeUploadObject.setFromSubscription(true);
        envelopeUploadObject.setAckRequest(AckRequestEnum.DELIVERED_ACK_REQUESTED);
        envelopeUploadObject.setTransactionIdentifier(UUID.randomUUID());

        // Package the data according to the container (exchange-set or dataset by default)
        if(ContainerTypeEnum.S100_ExchangeSet.equals(subscriptionRequest.getContainerType())) {
            envelopeUploadObject.setContainerType(ContainerTypeEnum.S100_DataSet);
            try {
                envelopeUploadObject.setData(this.s100ExchangeSetService.packageToExchangeSet(
                        Collections.singletonList(s201Dataset),
                        subscriptionRequest.getUpdatedAt(),
                        LocalDateTime.now()));
            } catch (IOException | JAXBException ex) {
                log.error(ex.getMessage());
                return;
            }
        } else {
            envelopeUploadObject.setContainerType(ContainerTypeEnum.S100_DataSet);
            envelopeUploadObject.setData(s201Dataset.getDatasetContent().getContent().getBytes());
        }

        // Set the envelope to the upload object
        UploadObject uploadObject = new UploadObject();
        uploadObject.setEnvelope(envelopeUploadObject);

        // Now upload the message to the subscription client
        secomClient.upload(uploadObject);

        // Update the subscription timestamp tp keep track of the updates
        this.updateSubscriptionTimestamp(subscriptionRequest);
    }

    /**
     * Constructs a hibernate search query using Lucene based on the provided
     * search test. This query will be based on the SECOM subscriptions fields.
     *
     * @param searchText the text to be searched
     * @param sort the sorting selection for the search query
     * @return the full text query
     */
    protected SearchQuery<SubscriptionRequest> getDatasetSearchQueryByText(String searchText, Sort sort) {
        SearchSession searchSession = Search.session( this.entityManager );
        SearchScope<SubscriptionRequest> scope = searchSession.scope( SubscriptionRequest.class );
        return searchSession.search( scope )
                .extension(LuceneExtension.get())
                .where(f -> f.wildcard()
                        .fields(
                                "uuid",
                                "containerType",
                                "dataProductType",
                                "productVersion",
                                "dataReference",
                                "clientMrn"
                        )
                        .matching(Optional.ofNullable(searchText).map(st -> "*" + st).orElse("") + "*")
                )
                .sort(f -> f.fromLuceneSort(sort))
                .toQuery();
    }

    /**
     * Constructs a hibernate search query using Lucene based on the provided
     * a list of subscription search parameters.
     * -
     * For any more elaborate search, the getSearchMessageQueryByText function
     * can be used.
     *
     * @param containerType         the container type of the requested subscriptions
     * @param dataProductType       the SECOM data product type of the requested subscriptions
     * @param productVersion        the product version of the requested subscriptions
     * @param dataReference         the UUID data reference matched by the requested subscriptions
     * @param geometry              the geometry intersecting with the requested subscriptions
     * @param timestamp             the timestamp for which the requested subscriptions are valid
     * @param sort the sorting selection for the search query
     * @return the full text query
     */
    protected SearchQuery<SubscriptionRequest> getSubscriptionRequestSearchQuery(ContainerTypeEnum containerType,
                                                                                 SECOM_DataProductType dataProductType,
                                                                                 String productVersion,
                                                                                 UUID dataReference,
                                                                                 Geometry geometry,
                                                                                 LocalDateTime timestamp,
                                                                                 Sort sort) {
        // Then build and return the hibernate-search query
        SearchSession searchSession = Search.session( this.entityManager );
        SearchScope<SubscriptionRequest> scope = searchSession.scope( SubscriptionRequest.class );
        return searchSession.search( scope )
                .where( f -> {
                    BooleanPredicateClausesStep<?> step = f.bool()
                        .must(f.matchAll());
                    Optional.ofNullable(containerType).ifPresent(v -> {
                        step.should(f.match()
                                .field("containerType")
                                .matching(v.name()));
                        step.should(f.match()
                                .field("containerType")
                                .matching("NULL"));
                    });
                    Optional.ofNullable(dataProductType).ifPresent(v -> {
                        step.should(f.match()
                                .field("dataProductType")
                                .matching(v.name()));
                        step.should(f.match()
                                .field("dataProductType")
                                .matching("NULL"));
                    });
                    Optional.ofNullable(productVersion).ifPresent(v -> {
                        step.should(f.match()
                                .field("productVersion")
                                .matching(v));
                        step.should(f.match()
                                .field("productVersion")
                                .matching("NULL"));
                    });
                    Optional.ofNullable(dataReference).ifPresent(v -> {
                        step.should(f.match()
                                .field("dataReference")
                                .matching(v.toString()));
                        step.should(f.match()
                                .field("dataReference")
                                .matching("NULL"));
                    });
                    Optional.ofNullable(geometry).ifPresent(g -> {
                        step.must(f.extension(LuceneExtension.get())
                                .fromLuceneQuery(createGeoSpatialQuery(g)));
                    });
                    Optional.ofNullable(timestamp).ifPresent(v -> {
                        step.must(f.range()
                                .field("subscriptionPeriodStart")
                                .atMost(timestamp));
                    });
                    Optional.ofNullable(timestamp).ifPresent(v -> {
                        step.must(f.range()
                                .field("subscriptionPeriodEnd")
                                .atLeast(timestamp));
                    });
                    return step;
                })
                .sort(f -> ((LuceneSearchSortFactory)f).fromLuceneSort(sort))
                .toQuery();
    }

    /**
     * Creates a Lucene geo-spatial query based on the provided geometry. The
     * query isa recursive one based on the maxLevels defined (in this case 12,
     * which result in a sub-meter precision).
     *
     * @param geometry      The geometry to generate the spatial query for
     * @return The Lucene geo-spatial query constructed
     */
    protected Query createGeoSpatialQuery(Geometry geometry) {
        // Initialise the spatial strategy
        JtsSpatialContext ctx = JtsSpatialContext.GEO;
        int maxLevels = 12; //results in sub-meter precision for geo-hash
        SpatialPrefixTree grid = new GeohashPrefixTree(ctx, maxLevels);
        RecursivePrefixTreeStrategy strategy = new RecursivePrefixTreeStrategy(grid,"subscriptionGeometry");

        // Create the Lucene GeoSpatial Query
        return Optional.ofNullable(geometry)
                .map(g -> new SpatialArgs(SpatialOperation.Intersects, new JtsGeometry(g, ctx, false , true)))
                .map(strategy::makeQuery)
                .orElse(null);
    }

    /**
     * This helper function will update the timestamp of a subscription request
     * to demonstrate when that subscription was last informed on any changes.
     * This will be used to only send the dataset delta in case the exchange
     * set container has been selected.
     *
     * @param subscriptionRequest The subscription request to be updated
     */
    protected void updateSubscriptionTimestamp(SubscriptionRequest subscriptionRequest) {
        // Update the timestamp of the subscription
        subscriptionRequest.setUpdatedAt(LocalDateTime.now());

        // And save it in the database
        this.secomSubscriptionRepo.save(subscriptionRequest);
    }

}
