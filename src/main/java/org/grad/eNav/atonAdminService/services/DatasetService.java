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

package org.grad.eNav.atonAdminService.services;


import _int.iho.s201.s100.gml.base._5_2.MDTopicCategoryCode;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortedSetSortField;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.grad.eNav.atonAdminService.aspects.LogDataset;
import org.grad.eNav.atonAdminService.exceptions.DataNotFoundException;
import org.grad.eNav.atonAdminService.exceptions.DeletedAtoNsInDatasetContentGenerationException;
import org.grad.eNav.atonAdminService.exceptions.SavingFailedException;
import org.grad.eNav.atonAdminService.exceptions.ValidationException;
import org.grad.eNav.atonAdminService.models.domain.DatasetContent;
import org.grad.eNav.atonAdminService.models.domain.s201.S201Dataset;
import org.grad.eNav.atonAdminService.models.domain.s201.S201DatasetIdentification;
import org.grad.eNav.atonAdminService.models.dtos.datatables.DtPagingRequest;
import org.grad.eNav.atonAdminService.models.enums.DatasetOperation;
import org.grad.eNav.atonAdminService.repos.DatasetRepo;
import org.grad.secom.core.models.enums.SECOM_DataProductType;
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
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static java.util.function.Predicate.not;

/**
 * The S-201 Dataset Service.
 *
 * Service Implementation for managing the S-201 Dataset objects.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Service
@Slf4j
public class DatasetService {

    /**
     * The Application Content
     */
    @Autowired
    ApplicationContext applicationContext;

    /**
     * The Entity Manager.
     */
    @Autowired
    EntityManager entityManager;

    /**
     * The Task Executor.
     */
    @Autowired
    Executor taskExecutor;

    /**
     * The Dataset Content Service.
     */
    @Autowired
    DatasetContentService datasetContentService;

    /**
     * The Dataset Repo.
     */
    @Autowired
    DatasetRepo datasetRepo;

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

    // Service Variables
    private final String[] searchFields = new String[] {
            "datasetIdentificationInformation.datasetTitle",
            "datasetIdentificationInformation.encodingSpecification",
            "datasetIdentificationInformation.encodingSpecificationEdition",
            "datasetIdentificationInformation.productIdentifier",
            "datasetIdentificationInformation.productEdition",
            "datasetIdentificationInformation.applicationProfile",
            "datasetIdentificationInformation.datasetFileIdentifier",
            "datasetIdentificationInformation.datasetAbstract"
    };
    private final String[] searchFieldsWithSort = new String[] {

    };

    /**
     * Find one dataset by UUID.
     *
     * @param uuid the UUID of the dataset
     * @return the dataset
     */
    @Transactional(readOnly = true)
    public S201Dataset findOne(UUID uuid) {
        return this.datasetRepo.findById(uuid)
                .orElseThrow(() -> new DataNotFoundException(String.format("The requested dataset with UUID %s was not found", uuid)));
    }

    /**
     * Get all the datasets in a pageable search.
     * <p/>
     * Note that this operation by default does NOT show any cancelled datasets.
     *
     * @param uuid the dataset UUID
     * @param geometry the dataset geometry
     * @param fromTime the dataset validity starting time
     * @param toTime the dataset validity ending time
     * @param includeCancelled whether cancelled datasets should be included in the response
     * @param pageable the pageable result output
     * @return The matching datasets in a paged response
     */
    @Transactional(readOnly = true)
    public Page<S201Dataset> findAll(UUID uuid,
                                     Geometry geometry,
                                     LocalDateTime fromTime,
                                     LocalDateTime toTime,
                                     Boolean includeCancelled,
                                     Pageable pageable) {
        log.debug("Request to get S-201 Datasets in a pageable search");
        // Create the search query - always sort by name
        SearchQuery<S201Dataset> searchQuery = this.getDatasetSearchQuery(
                uuid,
                geometry,
                fromTime,
                toTime,
                includeCancelled,
                new Sort(new SortedSetSortField("uuid", false))
        );

        // Map the results to a paged response
        return Optional.of(searchQuery)
                .map(query -> pageable.isPaged() ? query.fetch(pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize()) : query.fetchAll())
                .map(searchResult -> new PageImpl<>(searchResult.hits(), pageable, searchResult.total().hitCount()))
                .orElseGet(() -> new PageImpl<>(Collections.emptyList(), pageable, 0));
    }

    /**
     * Handles a datatables pagination request and returns the dataset results
     * list in an appropriate format to be viewed by a datatables jQuery table.
     * <p/>
     * Note that this operation by default does show even cancelled datasets,
     * as datatables is used to manage all the available entries.
     *
     * @param dtPagingRequest the Datatables pagination request
     * @return the Datatables paged response
     */
    @Transactional(readOnly = true)
    public Page<S201Dataset> handleDatatablesPagingRequest(DtPagingRequest dtPagingRequest) {
        log.debug("Request to get S-201 Datasets in a Datatables pageable search");
        // Create the search query
        final SearchQuery<S201Dataset> searchQuery = this.getDatasetSearchQueryByText(
                dtPagingRequest.getSearch().getValue(),
                dtPagingRequest.getSearch().getIncludeCancelled(),
                dtPagingRequest.getLucenceSort(Arrays.asList(searchFieldsWithSort))
        );

        // Map the results to a paged response
        return Optional.of(searchQuery)
                .map(query -> query.fetch(dtPagingRequest.getStart(), dtPagingRequest.getLength()))
                .map(searchResult -> new PageImpl<>(searchResult.hits(), dtPagingRequest.toPageRequest(), searchResult.total().hitCount()))
                .orElseGet(() -> new PageImpl<>(Collections.emptyList(), dtPagingRequest.toPageRequest(), 0));
    }

    /**
     * The saving operation that persists the datasets in the database using
     * the correct repository based on the instance type.
     *
     * @param dataset the dataset entity to be saved
     * @return the saved dataset entity
     */
    @Transactional
    public S201Dataset save(@NotNull S201Dataset dataset) {
        log.debug("Request to save Dataset : {}", dataset);

        // Cancellation check
        this.cancellationCheck(dataset);

        // TODO: Check that this is the right topic to be set
        // If not defined, set a default dataset ISO 19115-1 topic category
        Optional.of(dataset.getDatasetIdentificationInformation())
                .map(S201DatasetIdentification::getDatasetTopicCategories)
                .filter(not(List::isEmpty))
                .ifPresentOrElse(
                        datasetTopicCategories -> { },
                        () -> dataset.getDatasetIdentificationInformation()
                                .setDatasetTopicCategories(Collections.singletonList(MDTopicCategoryCode.OCEANS))
                );

        // Never accept the content from the input, could be wrong. If defined,
        // copy the content from the previous entry or create a new one.
        dataset.setDatasetContent(Optional.of(dataset)
                .map(S201Dataset::getUuid)
                .flatMap(this.datasetRepo::findById)
                .map(S201Dataset::getDatasetContent)
                .orElse(null));

        // Now save the dataset - Merge to pick up all the latest changes
        final S201Dataset savedDataset = this.datasetRepo.saveAndFlush(dataset);

        // TODO: We need to improve this code not sure that a task executor and
        //       sleep is the best course of action here...
        // Request an update for the dataset content, but let someone else
        // deal with this. We have issues with the dataset being passed to
        // the database before the content update kicks in...
        this.taskExecutor.execute(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                this.requestDatasetContentUpdate(savedDataset.getUuid());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        // And return the saved dataset
        return savedDataset;
    }

    /**
     * Cancel the dataset specified by its UUID.
     *
     * @param uuid the UUID of the dataset
     */
    @LogDataset(operation = DatasetOperation.CANCELLED)
    @Transactional
    public S201Dataset cancel(@NotNull UUID uuid) {
        log.debug("Request to cancel Dataset with UUID : {}", uuid);

        // Make sure the dataset exists
        final S201Dataset result = this.datasetRepo.findById(uuid)
                .orElseThrow(() -> new DataNotFoundException(String.format("The requested dataset with UUID %s was not found", uuid)));

        // Cancellation check - if OK then cancel the dataset
        // TODO: Do we really need to check - maybe not fail after?
        Optional.of(uuid)
                .map(this::cancellationCheck)
                .map(validUuid -> true)
                .ifPresent(result::setCancelled);

        // Clear the content deltas for logging
        Optional.of(result)
                .map(S201Dataset::getDatasetContent)
                .ifPresent(DatasetContent::clearDelta);

        // Now save the dataset - Merge to pick up all the latest changes
        final S201Dataset cancelledDataset = this.datasetRepo.saveAndFlush(result);

        // Publish the cancelled dataset to the deleted channel
        this.s201RemovalChannel.send(MessageBuilder.withPayload(result)
                .setHeader(MessageHeaders.CONTENT_TYPE, SECOM_DataProductType.S201)
                .setHeader("operation", DatasetOperation.CANCELLED)
                .build());

        // And return the object for AOP
        return cancelledDataset;
    }

    /**
     * Delete the dataset specified by its UUID.
     *
     * @param uuid the UUID of the dataset
     */
    @LogDataset(operation = DatasetOperation.DELETED)
    @Transactional
    public S201Dataset delete(@NotNull UUID uuid) {
        log.debug("Request to delete Dataset with UUID : {}", uuid);

        // Make sure the dataset exists
        final S201Dataset result = this.datasetRepo.findById(uuid)
                .orElseThrow(() -> new DataNotFoundException(String.format("The requested dataset with UUID %s was not found", uuid)));

        // Clear the content deltas for logging
        Optional.of(result)
                .map(S201Dataset::getDatasetContent)
                .ifPresent(DatasetContent::clearDelta);

        // Now delete the dataset
        this.datasetRepo.delete(result);

        // Publish the updated dataset to the deleted channel
        this.s201RemovalChannel.send(MessageBuilder.withPayload(result)
                .setHeader(MessageHeaders.CONTENT_TYPE, SECOM_DataProductType.S201)
                .setHeader("operation", DatasetOperation.DELETED)
                .build());

        // And return the object for AOP
        return result;
    }

    /**
     * Replaces the dataset specified by its UUID with a new one of the
     * same specification, but with a whole new UUID. This operation
     * is a mixture of existing functionality and basically will cancel
     * the old dataset and create a brand new one with the same information.
     * This operation can be used in cases where an AtoN is deleted. In these
     * cases S-100 cannot be used to send delta information, hence a deletion
     * of the old dataset is required.
     * <p/>
     * Note that since we are using AOP for the dataset content logging, the
     * saving and update functionality will not generate any logs since we
     * are in the same component. Therefore, the new dataset content generation
     * will need to handle both the logs for the cancellation of the original
     * dataset and the creation of the replacement one.
     *
     * @param uuid the UUID of the dataset to be replaced
     */
    @Transactional
    public S201Dataset replace(@NotNull UUID uuid) {
        log.debug("Request to replace Dataset with UUID : {}", uuid);

        //====================================================================//
        //                            IMPORTANT POINT                         //
        //====================================================================//
        // To activate the AOP, use the application context to access the     //
        // service. Otherwise, the LogDataset and other annotations will not  //
        // work as expected.                                                  //
        //====================================================================//
        final DatasetService self = this.applicationContext.getBean(DatasetService.class);
        //====================================================================//

        // And perform the replacing operation
        return Optional.of(uuid)
                .map(self::cancel)
                .map(S201Dataset::replace)
                .map(self::save)
                .orElseThrow(() -> new SavingFailedException(String.format("An" +
                        "unknown error occurred while attempting to replace the " +
                        "dataset content of the dataset with UUID %s .", uuid))
                );
    }

    /**
     * Requests an update of the dataset content. This function is mainly for
     * internal use but can also be used externally to request a dataset
     * content update on AtoN entry changes. The content generation is run
     * asynchronously so the function will handle the result accordingly.
     * <p/>
     * In the event where the dataset contents include deletions, the generation
     * operation will end with an error, which will instruct the function to
     * cancel the previous dataset and create a new once (replacement).
     *
     * @param uuid the UUID of the dataset to update the content for
     */
    @Transactional
    public void requestDatasetContentUpdate(@NotNull UUID uuid) {
        // And request the dataset content generation asynchronously
        this.datasetContentService.generateDatasetContent(uuid)
                .whenCompleteAsync((result, ex) -> {
                    if(Objects.nonNull(ex)) {
                        if(ex.getCause() instanceof DeletedAtoNsInDatasetContentGenerationException) {
                            log.warn("Warning while generating the content of the dataset with UUID {}: {}",
                                    uuid, ex.getMessage());
                            // Now perform the replacement operation
                            this.replace(uuid);
                        } else {
                            log.error("Error while generating the content of the dataset with UUID {}: {}",
                                    uuid, ex.getMessage());
                        }
                    } else {
                        log.info("Successfully generated the content of the dataset with UUID {}",
                                result.getUuid());
                        // Publish the updated dataset to the publication channel
                        this.s201PublicationChannel.send(MessageBuilder.withPayload(result)
                                .setHeader(MessageHeaders.CONTENT_TYPE, SECOM_DataProductType.S201)
                                .setHeader("operation", result.isNew() ?
                                        DatasetOperation.CREATED : DatasetOperation.UPDATED)
                                .build());
                    }
                });
    }

    /**
     * Constructs a hibernate search query using Lucene based on the provided
     * search test. This query will be based on both the dataset and
     * datasetIdentificationInformation tables and will include the following
     * fields:
     * <ul>
     *  <li>datasetIdentificationInformation.datasetTitle</li>
     *  <li>datasetIdentificationInformation.encodingSpecification</li>
     *  <li>datasetIdentificationInformation.encodingSpecificationEdition</li>
     *  <li>datasetIdentificationInformation.productIdentifier</li>
     *  <li>datasetIdentificationInformation.productEdition</li>
     *  <li>datasetIdentificationInformation.applicationProfile</li>
     *  <li>datasetIdentificationInformation.datasetFileIdentifier</li>
     *  <li>datasetIdentificationInformation.datasetAbstract</li>
     * </ul>
     *
     * @param searchText the text to be searched
     * @param includeCancelled whether cancelled datasets should be included in the response
     * @param sort the sorting selection for the search query
     * @return the full text query
     */
    protected SearchQuery<S201Dataset> getDatasetSearchQueryByText(String searchText, Boolean includeCancelled, Sort sort) {
        SearchSession searchSession = Search.session( this.entityManager );
        SearchScope<S201Dataset> scope = searchSession.scope( S201Dataset.class );
        return searchSession.search( scope )
                .extension(LuceneExtension.get())
                .where(f -> {
                    BooleanPredicateClausesStep<?> step = f.bool()
                            .must(Optional.ofNullable(includeCancelled)
                                    .filter(Boolean.TRUE::equals)
                                    .map(c -> f.matchAll()
                                            .toPredicate())
                                    .orElseGet(() -> f.not(f.match()
                                            .field("cancelled")
                                            .matching(Boolean.TRUE))
                                            .toPredicate()));
                    if(Objects.nonNull(searchText)) {
                        step = step.must(f.wildcard()
                                .fields(this.searchFields)
                                .matching(Optional.ofNullable(searchText).map(st -> "*" + st).orElse("") + "*"));
                    }
                    return step;
                }
                )
                .sort(f -> f.fromLuceneSort(sort))
                .toQuery();
    }

    /**
     * Constructs a hibernate search query using Lucene based on the provided
     * AtoN UID and geometry. This query will be based solely on the datasets
     * table.
     * </p>
     * For any more elaborate search, the getSearchMessageQueryByText function
     * can be used.
     *
     * @param uuid the dataset UUID to be searched
     * @param geometry the geometry that the results should intersect with
     * @param fromTime the date-time the results should match from
     * @param toTime the date-time the results should match to
     * @param includeCancelled  whether cancelled datasets should be included in the response
     * @param sort the sorting selection for the search query
     * @return the full text query
     */
    protected SearchQuery<S201Dataset> getDatasetSearchQuery(UUID uuid,
                                                             Geometry geometry,
                                                             LocalDateTime fromTime,
                                                             LocalDateTime toTime,
                                                             Boolean includeCancelled,
                                                             Sort sort) {
        // Then build and return the hibernate-search query
        SearchSession searchSession = Search.session( this.entityManager );
        SearchScope<S201Dataset> scope = searchSession.scope( S201Dataset.class );
        return searchSession.search( scope )
                .where( f -> {
                    BooleanPredicateClausesStep<?> step = f.bool()
                            .must(Optional.ofNullable(includeCancelled)
                                    .filter(Boolean.TRUE::equals)
                                    .map(c -> f.matchAll()
                                            .toPredicate())
                                    .orElseGet(() -> f.not(f.match()
                                            .field("cancelled")
                                            .matching(Boolean.TRUE))
                                            .toPredicate()));
                    if(Objects.nonNull(uuid)) {
                        step = step.must(f.match()
                                .field("uuid")
                                .matching(uuid));
                    }
                    if(Objects.nonNull(geometry)) {
                        step = step.must(f.bool()
                                .must(f.extension(LuceneExtension.get())
                                .fromLuceneQuery(createGeoSpatialQuery(geometry))));
                    }
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
        RecursivePrefixTreeStrategy strategy = new RecursivePrefixTreeStrategy(grid,"geometry");

        // Create the Lucene GeoSpatial Query
        return Optional.ofNullable(geometry)
                .map(g -> new SpatialArgs(SpatialOperation.Intersects, new JtsGeometry(g, ctx, false , true)))
                .map(strategy::makeQuery)
                .orElse(null);
    }

    /**
     * A helper function to protect the rest of the code from editing canceled
     * datasets. This function will perform a database call and could become
     * expensive in iterative operations.
     *
     * @param s201Dataset the dataset to be examined for being cancelled
     */
    @Transactional(readOnly = true)
    protected S201Dataset cancellationCheck(S201Dataset s201Dataset) {
        // Try to find whether the provided dataset has been cancelled
        this.cancellationCheck(Optional.ofNullable(s201Dataset)
                .map(S201Dataset::getUuid)
                .orElse(null));

        // If everything is OK, return the input back
        return s201Dataset;
    }

    /**
     * A helper function to protect the rest of the code from editing canceled
     * datasets. This function will perform a database call and could become
     * expensive in iterative operations.
     *
     * @param uuid the UUID of the dataset to be examined for being cancelled
     */
    @Transactional(readOnly = true)
    protected UUID cancellationCheck(UUID uuid) {
        // Try to find whether the provided dataset has been cancelled
        final boolean cancellationDetected = Optional.ofNullable(uuid)
                .flatMap(u -> this.datasetRepo.findByUuidAndCancelled(u, Boolean.TRUE))
                .isPresent();

        // If so, then just throw a runtime exception with a generic message
        if(cancellationDetected) {
            throw new ValidationException(
                    String.format(
                            "The specified dataset with UUID %s has been cancelled. No modifications are allowed!",
                            uuid
                    ));
        }

        // If everything is OK, return the input back
        return uuid;
    }

}
