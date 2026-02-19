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

package org.grad.eNav.atonAdminService.services.secom.v1;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.bind.JAXBException;
import org.grad.eNav.atonAdminService.models.domain.DatasetContent;
import org.grad.eNav.atonAdminService.models.domain.s201.S201Dataset;
import org.grad.eNav.atonAdminService.models.domain.secom.SubscriptionRequest;
import org.grad.eNav.atonAdminService.models.dtos.datatables.*;
import org.grad.eNav.atonAdminService.models.enums.DatasetOperation;
import org.grad.eNav.atonAdminService.repos.SecomSubscriptionRepo;
import org.grad.eNav.atonAdminService.services.S100ExchangeSetService;
import org.grad.eNav.atonAdminService.services.UnLoCodeService;
import org.grad.eNav.atonAdminService.utils.SecomUtils;
import org.grad.secom.core.exceptions.SecomNotFoundException;
import org.grad.secom.core.exceptions.SecomValidationException;
import org.grad.secom.core.models.UploadObject;
import org.grad.secom.core.models.enums.AckRequestEnum;
import org.grad.secom.core.models.enums.ContainerTypeEnum;
import org.grad.secom.core.models.enums.SECOM_DataProductType;
import org.grad.secom.core.models.enums.SubscriptionEventEnum;
import org.grad.secom.springboot4.components.SecomClient;
import org.hibernate.search.engine.search.query.SearchQuery;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.query.SearchResultTotal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecomV1SubscriptionServiceTest {

    /**
     * The Tested Service.
     */
    @InjectMocks
    @Spy
    SecomV1SubscriptionService secomV1SubscriptionService;

    /**
     * The Entity Manager Factory mock.
     */
    @Mock
    EntityManagerFactory entityManagerFactory;

    /**
     * The SECOM v1 Service mock.
     */
    @Mock
    SecomV1Service secomV1Service;

    /**
     * The UN/LoCode Service mock.
     */
    @Mock
    UnLoCodeService unLoCodeService;

    /**
     * The SECOM v1 Subscription Notification Service mock.
     */
    @Mock
    SecomV1SubscriptionNotificationService secomV1SubscriptionNotificationService;

    /**
     * The S-100 Exchange Set Service mock.
     */
    @Mock
    S100ExchangeSetService s100ExchangeSetService;

    /**
     * The SECOM Subscription Repo mock.
     */
    @Mock
    SecomSubscriptionRepo secomSubscriptionRepo;

    /**
     * TThe S-201 Dataset Channel to publish the published data to.
     */
    @Mock
    PublishSubscribeChannel s201PublicationChannel;

    /**
     * The S-201 Dataset Channel to publish the deleted data to.
     */
    @Mock
    PublishSubscribeChannel s201RemovalChannel;

    // Test Variables
    private List<SubscriptionRequest> subscriptionRequestList;
    private SubscriptionRequest newSubscriptionRequest;
    private SubscriptionRequest existingSubscriptionRequest;
    private S201Dataset s201Dataset;
    private DatasetContent datasetContent;
    private GeometryFactory factory;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setUp() {
        // Create a temp geometry factory to get a test geometries
        this.factory = new GeometryFactory(new PrecisionModel(), 4326);

        // Initialise the subscriptions nodes list
        this.subscriptionRequestList = new ArrayList<>();
        for(long i=0; i<10; i++) {
            SubscriptionRequest subscription = new SubscriptionRequest();
            subscription.setUuid(UUID.randomUUID());
            subscription.setContainerType(SecomUtils.translateSecomContainerTypeEnum(ContainerTypeEnum.S100_DataSet));
            subscription.setDataProductType(SecomUtils.translateSecomDataProductTypeEnum(SECOM_DataProductType.S125));
            subscription.setProductVersion("0.0.1");
            subscription.setDataReference(UUID.randomUUID());
            subscription.setGeometry(this.factory.createEmpty(0));
            subscription.setUnlocode("UKHAR");
            subscription.setSubscriptionPeriodStart(LocalDateTime.now());
            subscription.setSubscriptionPeriodEnd(LocalDateTime.now());
            subscription.setCreatedAt(LocalDateTime.now());
            subscription.setUpdatedAt(LocalDateTime.now());
            subscription.setSubscriptionGeometry(this.factory.createEmpty(0));
            subscription.setGeometry(this.factory.createPoint(new Coordinate(i, i)));
            subscription.setClientMrn(String.format("urn:mrn:org:test:%d", i));
            this.subscriptionRequestList.add(subscription);
        }

        // Create a new subscription request
        this.newSubscriptionRequest = new SubscriptionRequest();
        this.newSubscriptionRequest.setContainerType(SecomUtils.translateSecomContainerTypeEnum(ContainerTypeEnum.S100_DataSet));
        this.newSubscriptionRequest.setDataProductType(SecomUtils.translateSecomDataProductTypeEnum(SECOM_DataProductType.S125));
        this.newSubscriptionRequest.setSubscriptionPeriodStart(LocalDateTime.now());
        this.newSubscriptionRequest.setSubscriptionPeriodEnd(LocalDateTime.now());
        this.newSubscriptionRequest.setGeometry(this.factory.createPoint(new Coordinate(52.98, 28)));

        // Create a an existing subscription request with a UUID
        this.existingSubscriptionRequest = new SubscriptionRequest();
        this.existingSubscriptionRequest.setUuid(UUID.randomUUID());
        this.existingSubscriptionRequest.setContainerType(SecomUtils.translateSecomContainerTypeEnum(ContainerTypeEnum.S100_DataSet));
        this.existingSubscriptionRequest.setDataProductType(SecomUtils.translateSecomDataProductTypeEnum(SECOM_DataProductType.S201));
        this.existingSubscriptionRequest.setSubscriptionPeriodStart(LocalDateTime.now());
        this.existingSubscriptionRequest.setSubscriptionPeriodEnd(LocalDateTime.now());
        this.existingSubscriptionRequest.setGeometry(this.factory.createPoint(new Coordinate(52.98, 1.28)));
        this.existingSubscriptionRequest.setClientMrn("urn:mrn:org:test");

        // Create a new S-201 dataset
        this.s201Dataset = new S201Dataset("S-201 Dataset");
        this.s201Dataset.setUuid(UUID.randomUUID());
        this.s201Dataset.setGeometry(this.factory.createPoint(new Coordinate(52.98, 1.28)));
        this.s201Dataset.setCreatedAt(LocalDateTime.now());
        this.s201Dataset.setLastUpdatedAt(LocalDateTime.now());
        this.datasetContent = new DatasetContent();
        this.datasetContent.setId(BigInteger.ONE);
        this.datasetContent.setContent("S-201 dataset content");
        this.datasetContent.setContentLength(BigInteger.valueOf(this.datasetContent.getContent().length()));
        this.datasetContent.setGeneratedAt(LocalDateTime.now());
        this.s201Dataset.setDatasetContent(this.datasetContent);
    }

    /**
     * Test that the SECOM Subscription Service gets initialised correctly, and
     * it subscribes to the AtoN publish subscribe channels.
     */
    @Test
    void testInit()  {
        // Perform the service call
        this.secomV1SubscriptionService.init();

        verify(this.entityManagerFactory, times(1)).createEntityManager();
        verify(this.s201PublicationChannel, times(1)).subscribe(this.secomV1SubscriptionService);
        verify(this.s201RemovalChannel, times(1)).subscribe(this.secomV1SubscriptionService);
    }

    /**
     * Test that the SECOM Subscription Service gets destroyed correctly, and it
     * un-subscribes from the S-201 publish subscribe channels.
     */
    @Test
    void testDestroy() {
        // Setup a mock entity manager
        this.secomV1SubscriptionService.entityManager = mock(EntityManager.class);

        // Perform the service call
        this.secomV1SubscriptionService.destroy();

        verify(this.secomV1SubscriptionService.entityManager , times(1)).close();
        verify(this.s201PublicationChannel, times(1)).destroy();
        verify(this.s201RemovalChannel, times(1)).destroy();
    }

    /**
     * Test that the SECOM subscription service can process correctly the S-201
     * dataset publications received through the S-201 dataset publish-subscribe
     * publication channel. This operation should identify all matching
     * subscription requests and inform them of the new update available.
     */
    @Test
    void testHandleMessagePublication() {
        doReturn(Collections.singletonList(this.existingSubscriptionRequest)).when(this.secomV1SubscriptionService).findAll(any(), any(), any(), any(), any(), any());
        doNothing().when(this.secomV1SubscriptionService).sendToSubscription(any(), any());

        // Create a message to be handled
        Message<S201Dataset> message = Optional.of(this.s201Dataset).map(MessageBuilder::withPayload)
                .map(builder -> builder.setHeader(MessageHeaders.CONTENT_TYPE, SECOM_DataProductType.S201.name()))
                .map(builder -> builder.setHeader("operation", DatasetOperation.CREATED))
                .map(MessageBuilder::build)
                .orElse(null);

        // Perform the service call
        this.secomV1SubscriptionService.handleMessage(message);

        // Verify that we look up the subscriptions in the proper way
        ArgumentCaptor<ContainerTypeEnum> containerTypeArgument = ArgumentCaptor.forClass(ContainerTypeEnum.class);
        ArgumentCaptor<SECOM_DataProductType> dataProductTypeArgument = ArgumentCaptor.forClass(SECOM_DataProductType.class);
        ArgumentCaptor<String> productVersionArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> uuidArgument = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Geometry> geometryArgument = ArgumentCaptor.forClass(Geometry.class);
        ArgumentCaptor<LocalDateTime> timestampArgument = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(this.secomV1SubscriptionService, times(1)).findAll(
                containerTypeArgument.capture(),
                dataProductTypeArgument.capture(),
                productVersionArgument.capture(),
                uuidArgument.capture(),
                geometryArgument.capture(),
                timestampArgument.capture());

        // Verify the arguments
        assertNotNull(containerTypeArgument.getValue());
        assertNotNull(dataProductTypeArgument.getValue());
        assertNotNull(productVersionArgument.getValue());
        assertNotNull(uuidArgument.getValue());
        assertNotNull(geometryArgument.getValue());
        assertNotNull(timestampArgument.getValue());
        assertEquals(ContainerTypeEnum.S100_DataSet, containerTypeArgument.getValue());
        assertEquals(SECOM_DataProductType.S201, dataProductTypeArgument.getValue());
        assertEquals(this.s201Dataset.getDatasetIdentificationInformation().getProductEdition(), productVersionArgument.getValue());
        assertEquals(this.s201Dataset.getUuid(), uuidArgument.getValue());
        assertEquals(this.s201Dataset.getGeometry(), geometryArgument.getValue());
        assertEquals(this.s201Dataset.getLastUpdatedAt(), timestampArgument.getValue());

        // Verify that we try to update the registered clients
        ArgumentCaptor<SubscriptionRequest> subscriptionRequestArgument = ArgumentCaptor.forClass(SubscriptionRequest.class);
        ArgumentCaptor<S201Dataset> s201DatasetArgument = ArgumentCaptor.forClass(S201Dataset.class);
        verify(this.secomV1SubscriptionService, times(1)).sendToSubscription(subscriptionRequestArgument.capture(), s201DatasetArgument.capture());

        // Verify the arguments
        assertNotNull(subscriptionRequestArgument.getValue());
        assertNotNull(s201DatasetArgument.getValue());
        assertEquals(this.existingSubscriptionRequest.getUuid(), subscriptionRequestArgument.getValue().getUuid());
        assertEquals(this.s201Dataset.getUuid(), s201DatasetArgument.getValue().getUuid());
    }

    /**
     * Test that the SECOM subscription service can process correctly the S-201
     * dataset deletions received through the S-201 dataset publish-subscribe
     * deletion channel. This operation should identify the affected
     * subscription requests directly linked to the deleted dataset and notify
     * them of the deletion.
     */
    @Test
    void testHandleMessageDeletion() {
        doReturn(Collections.singletonList(this.existingSubscriptionRequest)).when(this.secomV1SubscriptionService).findAll(any(), any(), any(), any(), any(), any());
        doReturn(this.existingSubscriptionRequest.getUuid()).when(this.secomV1SubscriptionService).delete(any());

        // Create a message to be handled
        Message<S201Dataset> message = Optional.of(this.s201Dataset).map(MessageBuilder::withPayload)
                .map(builder -> builder.setHeader(MessageHeaders.CONTENT_TYPE, SECOM_DataProductType.S201.name()))
                .map(builder -> builder.setHeader("operation", DatasetOperation.DELETED))
                .map(MessageBuilder::build)
                .orElse(null);

        // Perform the service call
        this.secomV1SubscriptionService.handleMessage(message);

        // Verify that we look up the subscriptions in the proper way for the deletion
        ArgumentCaptor<ContainerTypeEnum> containerTypeArgument = ArgumentCaptor.forClass(ContainerTypeEnum.class);
        ArgumentCaptor<SECOM_DataProductType> dataProductTypeArgument = ArgumentCaptor.forClass(SECOM_DataProductType.class);
        ArgumentCaptor<String> productVersionArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> uuidArgument = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Geometry> geometryArgument = ArgumentCaptor.forClass(Geometry.class);
        ArgumentCaptor<LocalDateTime> timestampArgument = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(this.secomV1SubscriptionService, times(1)).findAll(
                containerTypeArgument.capture(),
                dataProductTypeArgument.capture(),
                productVersionArgument.capture(),
                uuidArgument.capture(),
                geometryArgument.capture(),
                timestampArgument.capture());

        // Verify the arguments
        assertNull(containerTypeArgument.getValue());
        assertNull(dataProductTypeArgument.getValue());
        assertNull(productVersionArgument.getValue());
        assertNotNull(uuidArgument.getValue());
        assertNull(geometryArgument.getValue());
        assertNull(timestampArgument.getValue());
        assertEquals(this.s201Dataset.getUuid(), uuidArgument.getValue());

        // Verify that we try to inform the registered clients for the deletion
        ArgumentCaptor<UUID> removeSubscriptionArgument = ArgumentCaptor.forClass(UUID.class);
        verify(this.secomV1SubscriptionService, times(1)).delete(removeSubscriptionArgument.capture());

        // Verify the arguments
        assertNotNull(removeSubscriptionArgument.getValue());
        assertEquals(this.existingSubscriptionRequest.getUuid(), removeSubscriptionArgument.getValue());
    }

    /**
     * Test that the SECOM subscription service will not process any messages
     * that are received through the publish-subscribe channels and are not in
     * the correct format.
     */
    @Test
    void testHandleMessageWrongFormat() {
        // Create a message to be handled
        Message message = Optional.of(this.s201Dataset).map(MessageBuilder::withPayload)
                .map(builder -> builder.setHeader(MessageHeaders.CONTENT_TYPE, SECOM_DataProductType.OTHER.name()))
                .map(builder -> builder.setHeader("operation", DatasetOperation.CREATED))
                .map(MessageBuilder::build)
                .orElse(null);

        // Perform the service call
        this.secomV1SubscriptionService.handleMessage(message);

        // Verify that we won't call any of the subscription functions
        verify(this.secomV1SubscriptionService, never()).findAll(any(), any(), any(), any(), any(), any());
        verify(this.secomV1SubscriptionService, never()).sendToSubscription(any(), any());
        verify(this.secomV1SubscriptionService, never()).delete(any());
    }

    /**
     * Test that we can search for all the subscription currently present in the
     * database and matching the provided criteria.
     */
    @Test
    void testFindAll() {
        // Mock the full text query
        SearchQuery<SubscriptionRequest> mockedQuery = mock(SearchQuery.class);
        SearchResult<SubscriptionRequest> searchResult = mock(SearchResult.class);
        SearchResultTotal searchResultTotal = mock(SearchResultTotal.class);
        doReturn(searchResult).when(mockedQuery).fetchAll();
        doReturn(Collections.singletonList(this.existingSubscriptionRequest)).when(searchResult).hits();
        doReturn(mockedQuery).when(this.secomV1SubscriptionService).getSubscriptionRequestSearchQuery(any(), any(), any(), any(), any(), any(), any());

        // Perform the service call
        List<SubscriptionRequest> result = this.secomV1SubscriptionService.findAll(
                SecomUtils.translateSecomContainerTypeEnum(this.existingSubscriptionRequest.getContainerType()),
                SecomUtils.translateSecomDataProductTypeEnum(this.existingSubscriptionRequest.getDataProductType()),
                this.existingSubscriptionRequest.getProductVersion(),
                this.existingSubscriptionRequest.getDataReference(),
                this.existingSubscriptionRequest.getGeometry(),
                LocalDateTime.now());

        // Test the result
        assertNotNull(result);
        assertEquals(1, result.size());

        // Test each of the result entries
        for(int i=0; i < result.size(); i++){
            assertNotNull(result.get(i));
            assertEquals(this.existingSubscriptionRequest.getUuid(), result.get(i).getUuid());
            assertEquals(this.existingSubscriptionRequest.getContainerType(), result.get(i).getContainerType());
            assertEquals(this.existingSubscriptionRequest.getDataProductType(), result.get(i).getDataProductType());
            assertEquals(this.existingSubscriptionRequest.getSubscriptionPeriodStart(), result.get(i).getSubscriptionPeriodStart());
            assertEquals(this.existingSubscriptionRequest.getSubscriptionPeriodEnd(), result.get(i).getSubscriptionPeriodEnd());
            assertEquals(this.existingSubscriptionRequest.getGeometry(), result.get(i).getGeometry());
            assertEquals(this.existingSubscriptionRequest.getClientMrn(), result.get(i).getClientMrn());
        }
    }

    /**
     * Test that we can retrieve the paged list of subscription request entries
     * for a Datatables pagination request (which by the way also includes
     * search and sorting definitions).
     */
    @Test
    void testHandleDatatablesPagingRequest() {
        // First create the pagination request
        DtPagingRequest dtPagingRequest = new DtPagingRequest();
        dtPagingRequest.setStart(0);
        dtPagingRequest.setLength(5);

        // Set the pagination request columns
        dtPagingRequest.setColumns(new ArrayList());
        Stream.of("uuid", "containerType", "dataProductType")
                .map(DtColumn::new)
                .forEach(dtPagingRequest.getColumns()::add);

        // Set the pagination request ordering
        DtOrder dtOrder = new DtOrder();
        dtOrder.setColumn(0);
        dtOrder.setDir(DtDirection.asc);
        dtPagingRequest.setOrder(Collections.singletonList(dtOrder));

        // Set the pagination search
        DtSearch dtSearch = new DtSearch();
        dtSearch.setValue("search-term");
        dtPagingRequest.setSearch(dtSearch);

        // Mock the full text query
        SearchQuery<?> mockedQuery = mock(SearchQuery.class);
        SearchResult<?> mockedResult = mock(SearchResult.class);
        SearchResultTotal mockedResultTotal = mock(SearchResultTotal.class);
        doReturn(5L).when(mockedResultTotal).hitCount();
        doReturn(mockedResultTotal).when(mockedResult).total();
        doReturn(this.subscriptionRequestList.subList(0, 5)).when(mockedResult).hits();
        doReturn(mockedResult).when(mockedQuery).fetch(any(), any());
        doReturn(mockedQuery).when(this.secomV1SubscriptionService).getDatasetSearchQueryByText(any(), any());

        // Perform the service call
        Page<SubscriptionRequest> result = this.secomV1SubscriptionService.handleDatatablesPagingRequest(dtPagingRequest);

        // Validate the result
        assertNotNull(result);
        assertEquals(5, result.getSize());

        // Test each of the result entries
        for(int i=0; i < result.getSize(); i++) {
            assertNotNull(result.getContent().get(i));
            assertEquals(this.subscriptionRequestList.get(i).getUuid(), result.getContent().get(i).getUuid());
            assertEquals(this.subscriptionRequestList.get(i).getGeometry(), result.getContent().get(i).getGeometry());
            assertEquals(this.subscriptionRequestList.get(i).getUuid(), result.getContent().get(i).getUuid());
            assertEquals(this.subscriptionRequestList.get(i).getContainerType(), result.getContent().get(i).getContainerType());
            assertEquals(this.subscriptionRequestList.get(i).getDataProductType(), result.getContent().get(i).getDataProductType());
            assertEquals(this.subscriptionRequestList.get(i).getProductVersion(), result.getContent().get(i).getProductVersion());
            assertEquals(this.subscriptionRequestList.get(i).getDataReference(), result.getContent().get(i).getDataReference());
            assertEquals(this.subscriptionRequestList.get(i).getGeometry(), result.getContent().get(i).getGeometry());
            assertEquals(this.subscriptionRequestList.get(i).getUnlocode(), result.getContent().get(i).getUnlocode());
            assertEquals(this.subscriptionRequestList.get(i).getSubscriptionPeriodStart(), result.getContent().get(i).getSubscriptionPeriodStart());
            assertEquals(this.subscriptionRequestList.get(i).getSubscriptionPeriodEnd(), result.getContent().get(i).getSubscriptionPeriodEnd());
            assertEquals(this.subscriptionRequestList.get(i).getCreatedAt(), result.getContent().get(i).getCreatedAt());
            assertEquals(this.subscriptionRequestList.get(i).getUpdatedAt(), result.getContent().get(i).getUpdatedAt());
            assertEquals(this.subscriptionRequestList.get(i).getSubscriptionGeometry(), result.getContent().get(i).getSubscriptionGeometry());
            assertEquals(this.subscriptionRequestList.get(i).getClientMrn(), result.getContent().get(i).getClientMrn());
        }
    }

    /**
     * Test that we can successfully create a new subscription request.
     */
    @Test
    void testSave() {
        // Mock the HTTP servlet request
        final HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
        doReturn(this.existingSubscriptionRequest).when(this.secomSubscriptionRepo).save(any());
        doReturn(mock(CompletableFuture.class)).when(this.secomV1SubscriptionNotificationService).sendNotification(any(String.class), any(), any());

        // Perform the service call
        SubscriptionRequest result = this.secomV1SubscriptionService.save("urn:mrn:org:test", this.newSubscriptionRequest);

        // Make sure everything seems OK
        assertNotNull(result);
        assertEquals(this.existingSubscriptionRequest.getUuid(), result.getUuid());
        assertEquals(this.existingSubscriptionRequest.getContainerType(), result.getContainerType());
        assertEquals(this.existingSubscriptionRequest.getDataProductType(), result.getDataProductType());
        assertEquals(this.existingSubscriptionRequest.getSubscriptionPeriodStart(), result.getSubscriptionPeriodStart());
        assertEquals(this.existingSubscriptionRequest.getSubscriptionPeriodEnd(), result.getSubscriptionPeriodEnd());
        assertEquals(this.existingSubscriptionRequest.getGeometry(), result.getGeometry());
        assertEquals(this.existingSubscriptionRequest.getClientMrn(), result.getClientMrn());

        // Make sure the subscription notification was also sent
        verify(this.secomV1SubscriptionNotificationService, times(1)).sendNotification(
                eq(this.existingSubscriptionRequest.getClientMrn()),
                eq(this.existingSubscriptionRequest.getUuid()),
                eq(SubscriptionEventEnum.SUBSCRIPTION_CREATED));
    }

    /**
     * Test that if no MRN is provided in the HTTP request header, then the
     * subscription request will be declined with a SECOMValidationException.
     */
    @Test
    void testSaveBlankMrn() {
        // Perform the service call
        assertThrows(SecomValidationException.class, () -> this.secomV1SubscriptionService.save(null, this.newSubscriptionRequest));

        // Make sure no subscription notifications were sent
        verify(this.secomV1SubscriptionNotificationService, never()).sendNotification(any(String.class), any(), any());
    }

    /**
     * Test that we can successfully delete an existing subscription request.
     */
    @Test
    void testDelete() {
        doReturn(Optional.of(this.existingSubscriptionRequest)).when(this.secomSubscriptionRepo).findById(this.existingSubscriptionRequest.getUuid());
        doReturn(mock(CompletableFuture.class)).when(this.secomV1SubscriptionNotificationService).sendNotification(any(String.class), any(), any());

        // Perform the service call
        UUID result = this.secomV1SubscriptionService.delete(this.existingSubscriptionRequest.getUuid());

        // Make sure everything seems OK
        assertNotNull(result);
        assertEquals(this.existingSubscriptionRequest.getUuid(), result);
        verify(this.secomV1SubscriptionNotificationService, times(1)).sendNotification(
                eq(this.existingSubscriptionRequest.getClientMrn()),
                eq(this.existingSubscriptionRequest.getUuid()),
                eq(SubscriptionEventEnum.SUBSCRIPTION_REMOVED));

        // Make sure the subscription notification was also sent
        verify(this.secomV1SubscriptionNotificationService, times(1)).sendNotification(
                eq(this.existingSubscriptionRequest.getClientMrn()),
                eq(this.existingSubscriptionRequest.getUuid()),
                eq(SubscriptionEventEnum.SUBSCRIPTION_REMOVED));
    }

    /**
     * Test that if we try to delete a subscription that does not exist in the
     * database, a SecomNotFoundException will be thrown, which should then
     * be handled by the SECOM controller interface.
     */
    @Test
    void testDeleteNotFound() {
        doReturn(Optional.empty()).when(this.secomSubscriptionRepo).findById(this.existingSubscriptionRequest.getUuid());

        // Perform the service call
        assertThrows(SecomNotFoundException.class, () -> this.secomV1SubscriptionService.delete(this.existingSubscriptionRequest.getUuid()));

        // Make sure no subscription notifications were sent
        verify(this.secomV1SubscriptionNotificationService, never()).sendNotification(any(String.class), any(), any());
    }

    /**
     * Test that for a given subscription and a list of received Aids to
     * Navigation messages, this function will send the appropriate message to
     * the SECOM client discovered through the SECOM service, packaged as an
     * S-100 dataset.
     */
    @Test
    void testSendToSubscriptionDataset() {
        // Specify that this is for an S-100 dataset
        this.existingSubscriptionRequest.setContainerType(SecomUtils.translateSecomContainerTypeEnum(ContainerTypeEnum.S100_DataSet));

        // Mock a SECOM client
        final SecomClient secomClient = mock(SecomClient.class);
        doReturn(secomClient).when(this.secomV1Service).getClient(this.existingSubscriptionRequest.getClientMrn());

        // Perform the service call
        this.secomV1SubscriptionService.sendToSubscription(this.existingSubscriptionRequest, this.s201Dataset);

        // Verify that we upload the constructed SECOM upload object
        ArgumentCaptor<UploadObject> uploadArgument = ArgumentCaptor.forClass(UploadObject.class);
        verify(secomClient).upload(uploadArgument.capture());

        // Verify that the constructed object seems valid
        assertNotNull(uploadArgument.getValue());
        assertNotNull(uploadArgument.getValue().getEnvelope());
        assertTrue(uploadArgument.getValue().getEnvelope().getData().length > 0);
        assertEquals(SECOM_DataProductType.S201, uploadArgument.getValue().getEnvelope().getDataProductType());
        assertEquals(Boolean.TRUE, uploadArgument.getValue().getEnvelope().getFromSubscription());
        assertEquals(AckRequestEnum.DELIVERED_ACK_REQUESTED, uploadArgument.getValue().getEnvelope().getAckRequest());
        assertNotNull(uploadArgument.getValue().getEnvelope().getTransactionIdentifier());

        // Verify that we updated the timestamp of the subscription
        verify(this.secomSubscriptionRepo, times(1)).save(any());
        assertNotNull(this.existingSubscriptionRequest.getUpdatedAt());
    }

    /**
     * Test that for a given subscription and a list of received Aids to
     * Navigation messages, this function will send the appropriate message to
     * the SECOM client discovered through the SECOM service, packaged as an
     * S-100 exchange set.
     */
    @Test
    void testSendToSubscriptionExchangeSet() throws JAXBException, IOException {
        // Specify that this is for an S-100 exchange set and mock the generation
        this.existingSubscriptionRequest.setContainerType(SecomUtils.translateSecomContainerTypeEnum(ContainerTypeEnum.S100_ExchangeSet));
        doReturn("exchangeSet".getBytes()).when(this.s100ExchangeSetService).packageToExchangeSet(any(), any(), any());

        // Mock a SECOM client
        final SecomClient secomClient = mock(SecomClient.class);
        doReturn(secomClient).when(this.secomV1Service).getClient(this.existingSubscriptionRequest.getClientMrn());

        // Perform the service call
        this.secomV1SubscriptionService.sendToSubscription(this.existingSubscriptionRequest, this.s201Dataset);

        // Verify that we upload the constructed SECOM upload object
        ArgumentCaptor<UploadObject> uploadArgument = ArgumentCaptor.forClass(UploadObject.class);
        verify(secomClient).upload(uploadArgument.capture());

        // Verify that the constructed object seems valid
        assertNotNull(uploadArgument.getValue());
        assertNotNull(uploadArgument.getValue().getEnvelope());
        assertTrue(uploadArgument.getValue().getEnvelope().getData().length > 0);
        assertEquals("exchangeSet", new String(uploadArgument.getValue().getEnvelope().getData(), StandardCharsets.UTF_8));
        assertEquals(SECOM_DataProductType.S201, uploadArgument.getValue().getEnvelope().getDataProductType());
        assertEquals(Boolean.TRUE, uploadArgument.getValue().getEnvelope().getFromSubscription());
        assertEquals(AckRequestEnum.DELIVERED_ACK_REQUESTED, uploadArgument.getValue().getEnvelope().getAckRequest());
        assertNotNull(uploadArgument.getValue().getEnvelope().getTransactionIdentifier());

        // Verify that we updated the timestamp of the subscription
        verify(this.secomSubscriptionRepo, times(1)).save(any());
        assertNotNull(this.existingSubscriptionRequest.getUpdatedAt());
    }

    /**
     * Test that we can successfully update the update timestamp of a
     * subscription to keep track of when the last information was sent to it.
     */
    @Test
    void testUpdateSubscriptionTimestamp() {
        // Make sure we don't have an update time
        this.existingSubscriptionRequest.setUpdatedAt(null);

        // Perform the service call
        this.secomV1SubscriptionService.updateSubscriptionTimestamp(this.existingSubscriptionRequest);

        // And check that the time was updated
        verify(this.secomSubscriptionRepo, times(1)).save(any());
        assertNotNull(this.existingSubscriptionRequest.getUpdatedAt());
    }
}