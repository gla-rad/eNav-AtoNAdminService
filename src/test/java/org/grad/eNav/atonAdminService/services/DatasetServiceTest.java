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

import _int.iho.s_201.gml.cs0._2.ChangeTypesType;
import jakarta.persistence.EntityManager;
import org.grad.eNav.atonAdminService.exceptions.DataNotFoundException;
import org.grad.eNav.atonAdminService.exceptions.DeletedAtoNsInDatasetContentGenerationException;
import org.grad.eNav.atonAdminService.exceptions.SavingFailedException;
import org.grad.eNav.atonAdminService.exceptions.ValidationException;
import org.grad.eNav.atonAdminService.models.domain.DatasetContent;
import org.grad.eNav.atonAdminService.models.domain.s201.*;
import org.grad.eNav.atonAdminService.models.dtos.datatables.*;
import org.grad.eNav.atonAdminService.repos.DatasetRepo;
import org.hibernate.search.engine.search.query.SearchQuery;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.query.SearchResultTotal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.Message;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatasetServiceTest {

    /**
     * The Tested Service.
     */
    @InjectMocks
    @Spy
    DatasetService datasetService;

    /**
     * The Application Context mock.
     */
    @Mock
    ApplicationContext applicationContext;

    /**
     * The Entity Manager mock.
     */
    @Mock
    EntityManager entityManager;

    /**
     * The Task Executor mock.
     */
    @Mock
    Executor taskExecutor;

    /**
     * The Dataset Content Service mock.
     */
    @Mock
    DatasetContentService datasetContentService;

    /**
     * The Dataset Repo mock.
     */
    @Mock
    DatasetRepo datasetRepo;

    /**
     * The S-201 Dataset Channel to publish the published data to.
     */
    @Mock
    PublishSubscribeChannel s201PublicationChannel;

    /**
     * The S-201 Dataset Channel to publish the deleted data to.
     */
    @Mock
    PublishSubscribeChannel s201RemovalChannel;


    // Test Variables
    private List<AidsToNavigation> aidsToNavigationList;
    private List<S201Dataset> datasetList;
    private Pageable pageable;
    private S201Dataset newDataset;
    private S201Dataset existingDataset;
    private DatasetContent newDatasetContent;
    private DatasetContent existingDatasetContent;
    private GeometryFactory factory;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setUp() {
        // Create a temp geometry factory to get a test geometries
        this.factory = new GeometryFactory(new PrecisionModel(), 4326);

        // Initialise the AtoN messages list
        this.aidsToNavigationList = new ArrayList<>();
        for(long i=0; i<10; i++) {
            AidsToNavigation aidsToNavigation = new BeaconCardinal();
            aidsToNavigation.setId(BigInteger.valueOf(i));
            aidsToNavigation.setIdCode("ID"+i);
            aidsToNavigation.setGeometry(factory.createPoint(new Coordinate(i%180, i%90)));
            // Add the feature name entries
            FeatureName featureName = new FeatureName();
            featureName.setName("Aton No" + i);
            aidsToNavigation.setFeatureNames(Collections.singleton(featureName));
            // Add the information entries
            AtonStatusInformation information = new AtonStatusInformation();
            information.setText("Description of AtoN No" + i);
            information.setChangeTypes(ChangeTypesType.TEMPORARY_CHANGES);
            aidsToNavigation.setInformations(Collections.singleton(information));
            this.aidsToNavigationList.add(aidsToNavigation);
        }

        // Initialise the dataset nodes list
        this.datasetList = new ArrayList<>();
        for(long i=0; i<10; i++) {
            S201Dataset dataset = new S201Dataset(String.format("Dataset{}", i));
            dataset.setGeometry(this.factory.createPoint(new Coordinate(i, i)));
            dataset.setCancelled(false);
            this.datasetList.add(dataset);
        }

        // Create a pageable definition
        this.pageable = PageRequest.of(0, 5);

        // Create a Dataset without a UUID
        this.newDataset = new S201Dataset("NewDataset");
        this.newDataset.setGeometry(this.factory.createPoint(new Coordinate(51.98, 1.28)));
        this.newDatasetContent = new DatasetContent();
        this.newDatasetContent.setId(BigInteger.ONE);
        this.newDatasetContent.setDataset(this.newDataset);
        this.newDatasetContent.setSequenceNo(BigInteger.ONE);
        this.newDatasetContent.setContent("New dataset content");
        this.newDatasetContent.setContentLength(BigInteger.valueOf(this.newDatasetContent.getContent().length()));
        this.newDatasetContent.setGeneratedAt(LocalDateTime.now());
        this.newDataset.setDatasetContent(null);
        this.newDataset.setCancelled(false);

        // Create a Dataset with a UUID
        this.existingDataset = new S201Dataset("ExistingDataset");
        this.existingDataset.setUuid(UUID.randomUUID());
        this.existingDataset.setGeometry(this.factory.createPoint(new Coordinate(52.98, 2.28)));
        this.existingDatasetContent = new DatasetContent();
        this.existingDatasetContent.setId(BigInteger.TWO);
        this.existingDatasetContent.setDataset(this.existingDataset);
        this.existingDatasetContent.setSequenceNo(BigInteger.ONE);
        this.existingDatasetContent.setContent("Existing dataset content");
        this.existingDatasetContent.setContentLength(BigInteger.valueOf(this.existingDatasetContent.getContent().length()));
        this.existingDatasetContent.setGeneratedAt(LocalDateTime.now());
        this.existingDataset.setDatasetContent(this.existingDatasetContent);
        this.existingDataset.setCancelled(false);
    }

    /**
     * Test that we can search for a single dataset based on it's ID.
     */
    @Test
    void testFindOne() {
        doReturn(Optional.of(this.existingDataset)).when(this.datasetRepo).findById(this.existingDataset.getUuid());

        // Perform the service call
        S201Dataset result = this.datasetService.findOne(this.existingDataset.getUuid());

        // Test the result
        assertNotNull(result);
        assertEquals(this.existingDataset.getUuid(), result.getUuid());
        assertEquals(this.existingDataset.getGeometry(), result.getGeometry());
        assertNotNull(result.getDatasetIdentificationInformation());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getDatasetTitle(), result.getDatasetIdentificationInformation().getDatasetTitle());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getEncodingSpecification(), result.getDatasetIdentificationInformation().getEncodingSpecification());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getEncodingSpecificationEdition(), result.getDatasetIdentificationInformation().getEncodingSpecificationEdition());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getProductIdentifier(),result.getDatasetIdentificationInformation().getProductIdentifier());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getProductEdition(), result.getDatasetIdentificationInformation().getProductEdition());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getApplicationProfile(), result.getDatasetIdentificationInformation().getApplicationProfile());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getDatasetLanguage(), result.getDatasetIdentificationInformation().getDatasetLanguage());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getDatasetAbstract(), result.getDatasetIdentificationInformation().getDatasetAbstract());
        assertFalse(result.getCancelled());
    }

    /**
     * Test that we if we try to find a dataset with an ID that does not exist,
     * a DataNotFoundException will be thrown.
     */
    @Test
    void testFindOneNotFound() {
        doReturn(Optional.empty()).when(this.datasetRepo).findById(this.existingDataset.getUuid());

        // Perform the service call
        assertThrows(DataNotFoundException.class, () ->
                this.datasetService.delete(this.existingDataset.getUuid())
        );
    }

    /**
     * Test that we can search for all the datasets currently present in the
     * database and matching the provided criteria, through a paged call.
     */
    @Test
    void testFindAllPaged() {
        // Mock the full text query
        SearchQuery<AidsToNavigation> mockedQuery = mock(SearchQuery.class);
        SearchResult<AidsToNavigation> searchResult = mock(SearchResult.class);
        SearchResultTotal searchResultTotal = mock(SearchResultTotal.class);
        doReturn(searchResult).when(mockedQuery).fetch(any(), any());
        doReturn(this.datasetList.subList(0, 5)).when(searchResult).hits();
        doReturn(searchResultTotal).when(searchResult).total();
        doReturn(10L).when(searchResultTotal).hitCount();
        doReturn(mockedQuery).when(this.datasetService).getDatasetSearchQuery(any(), any(), any(), any(), any(), any());

        // Perform the service call
        Page<S201Dataset> result = this.datasetService.findAll(UUID.randomUUID(), null, null, null, Boolean.FALSE, pageable);

        // Test the result
        assertNotNull(result);
        assertEquals(5, result.getSize());

        // Test each of the result entries
        for(int i=0; i < result.getSize(); i++){
            assertNotNull(result.getContent().get(i));
            assertEquals(this.datasetList.get(i).getUuid(), result.getContent().get(i).getUuid());
            assertEquals(this.datasetList.get(i).getGeometry(), result.getContent().get(i).getGeometry());
            assertNotNull(result.getContent().get(i).getDatasetIdentificationInformation());
            assertEquals(this.datasetList.get(i).getDatasetIdentificationInformation().getDatasetTitle(), result.getContent().get(i).getDatasetIdentificationInformation().getDatasetTitle());
            assertEquals(this.datasetList.get(i).getDatasetIdentificationInformation().getEncodingSpecification(), result.getContent().get(i).getDatasetIdentificationInformation().getEncodingSpecification());
            assertEquals(this.datasetList.get(i).getDatasetIdentificationInformation().getEncodingSpecificationEdition(), result.getContent().get(i).getDatasetIdentificationInformation().getEncodingSpecificationEdition());
            assertEquals(this.datasetList.get(i).getDatasetIdentificationInformation().getProductIdentifier(), result.getContent().get(i).getDatasetIdentificationInformation().getProductIdentifier());
            assertEquals(this.datasetList.get(i).getDatasetIdentificationInformation().getProductEdition(), result.getContent().get(i).getDatasetIdentificationInformation().getProductEdition());
            assertEquals(this.datasetList.get(i).getDatasetIdentificationInformation().getApplicationProfile(), result.getContent().get(i).getDatasetIdentificationInformation().getApplicationProfile());
            assertEquals(this.datasetList.get(i).getDatasetIdentificationInformation().getDatasetLanguage(), result.getContent().get(i).getDatasetIdentificationInformation().getDatasetLanguage());
            assertEquals(this.datasetList.get(i).getDatasetIdentificationInformation().getDatasetAbstract(), result.getContent().get(i).getDatasetIdentificationInformation().getDatasetAbstract());
            assertFalse(result.getContent().get(i).getCancelled());
        }
    }

    /**
     * Test that we can retrieve the paged list of datatable entries for a
     * Datatables pagination request (which by the way also includes search and
     * sorting definitions).
     */
    @Test
    void testHandleDatatablesPagingRequest() {
        // First create the pagination request
        DtPagingRequest dtPagingRequest = new DtPagingRequest();
        dtPagingRequest.setStart(0);
        dtPagingRequest.setLength(5);

        // Set the pagination request columns
        dtPagingRequest.setColumns(new ArrayList());
        Stream.of("uid", "type", "message")
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
        doReturn(this.datasetList.subList(0, 5)).when(mockedResult).hits();
        doReturn(mockedResult).when(mockedQuery).fetch(any(), any());
        doReturn(mockedQuery).when(this.datasetService).getDatasetSearchQueryByText(any(), any(), any());

        // Perform the service call
        Page<S201Dataset> result = this.datasetService.handleDatatablesPagingRequest(dtPagingRequest);

        // Validate the result
        assertNotNull(result);
        assertEquals(5, result.getSize());

        // Test each of the result entries
        for(int i=0; i < result.getSize(); i++){
            assertNotNull(result.getContent().get(i));
            assertEquals(this.datasetList.get(i).getUuid(), result.getContent().get(i).getUuid());
            assertEquals(this.datasetList.get(i).getGeometry(), result.getContent().get(i).getGeometry());
            assertNotNull(result.getContent().get(i).getDatasetIdentificationInformation());
            assertEquals(this.datasetList.get(i).getDatasetIdentificationInformation().getDatasetTitle(), result.getContent().get(i).getDatasetIdentificationInformation().getDatasetTitle());
            assertEquals(this.datasetList.get(i).getDatasetIdentificationInformation().getEncodingSpecification(), result.getContent().get(i).getDatasetIdentificationInformation().getEncodingSpecification());
            assertEquals(this.datasetList.get(i).getDatasetIdentificationInformation().getEncodingSpecificationEdition(), result.getContent().get(i).getDatasetIdentificationInformation().getEncodingSpecificationEdition());
            assertEquals(this.datasetList.get(i).getDatasetIdentificationInformation().getProductIdentifier(), result.getContent().get(i).getDatasetIdentificationInformation().getProductIdentifier());
            assertEquals(this.datasetList.get(i).getDatasetIdentificationInformation().getProductEdition(), result.getContent().get(i).getDatasetIdentificationInformation().getProductEdition());
            assertEquals(this.datasetList.get(i).getDatasetIdentificationInformation().getApplicationProfile(), result.getContent().get(i).getDatasetIdentificationInformation().getApplicationProfile());
            assertEquals(this.datasetList.get(i).getDatasetIdentificationInformation().getDatasetLanguage(), result.getContent().get(i).getDatasetIdentificationInformation().getDatasetLanguage());
            assertEquals(this.datasetList.get(i).getDatasetIdentificationInformation().getDatasetAbstract(), result.getContent().get(i).getDatasetIdentificationInformation().getDatasetAbstract());
            assertFalse(result.getContent().get(i).getCancelled());
        }
    }

    /**
     * Test that we can save correctly a new or existing dataset entry if all
     * the validation checks are successful.
     */
    @Test
    void testSave() {
        doReturn(this.newDataset).when(this.datasetRepo).saveAndFlush(any());
        doNothing().when(this.datasetService).requestDatasetContentUpdate(any());

        // Special call ---  Allow the executor task to run
        doAnswer(args -> {
            ((Runnable)args.getArguments()[0]).run();
            return null;
        }).when(this.taskExecutor).execute(any());

        // Perform the service call
        S201Dataset result = this.datasetService.save(new S201Dataset());

        // Test the result
        assertNotNull(result);
        assertEquals(this.newDataset.getUuid(), result.getUuid());
        assertEquals(this.newDataset.getGeometry(), result.getGeometry());
        assertNotNull(result.getDatasetIdentificationInformation());
        assertEquals(this.newDataset.getDatasetIdentificationInformation().getDatasetTitle(), result.getDatasetIdentificationInformation().getDatasetTitle());
        assertEquals(this.newDataset.getDatasetIdentificationInformation().getEncodingSpecification(), result.getDatasetIdentificationInformation().getEncodingSpecification());
        assertEquals(this.newDataset.getDatasetIdentificationInformation().getEncodingSpecificationEdition(), result.getDatasetIdentificationInformation().getEncodingSpecificationEdition());
        assertEquals(this.newDataset.getDatasetIdentificationInformation().getProductIdentifier(),result.getDatasetIdentificationInformation().getProductIdentifier());
        assertEquals(this.newDataset.getDatasetIdentificationInformation().getProductEdition(), result.getDatasetIdentificationInformation().getProductEdition());
        assertEquals(this.newDataset.getDatasetIdentificationInformation().getApplicationProfile(), result.getDatasetIdentificationInformation().getApplicationProfile());
        assertEquals(this.newDataset.getDatasetIdentificationInformation().getDatasetLanguage(), result.getDatasetIdentificationInformation().getDatasetLanguage());
        assertEquals(this.newDataset.getDatasetIdentificationInformation().getDatasetAbstract(), result.getDatasetIdentificationInformation().getDatasetAbstract());
        assertNull(result.getDatasetContent());
        assertFalse(result.getCancelled());

        // Make sure a content generation request was submitted
        verify(this.datasetService, times(1)).requestDatasetContentUpdate(any());
    }

    /**
     * Test that we will not allow cancelled datasets to be saved.
     */
    @Test
    void testSaveCancelled() {
        this.existingDataset.setCancelled(true);
        doReturn(Optional.of(this.existingDataset)).when(this.datasetRepo).findByUuidAndCancelled(any(), any());

        // Perform the service call
        assertThrows(ValidationException.class, () ->
                this.datasetService.save(this.existingDataset)
        );

        // Verify that the saving function of the repo and the content
        // generation operation for the dataset in question were never called
        verify(this.datasetRepo, never()).save(any());
        verify(this.datasetService, never()).requestDatasetContentUpdate(any());
    }

    /**
     * Test that we can cancel correctly an existing dataset entry if all
     * the validation checks are successful.
     */
    @Test
    void testCancel() {
        final BigInteger origSequenceNo = this.existingDataset.getDatasetContent().getSequenceNo();
        doReturn(Optional.of(this.existingDataset)).when(this.datasetRepo).findById(any());
        doAnswer((inv) -> inv.getArgument(0)).when(this.datasetRepo).saveAndFlush((any()));

        // Perform the service call
        S201Dataset result = this.datasetService.cancel(this.existingDataset.getUuid());

        // Test the result
        assertNotNull(result);
        assertEquals(this.existingDataset.getUuid(), result.getUuid());
        assertEquals(this.existingDataset.getGeometry(), result.getGeometry());
        assertNotNull(result.getDatasetIdentificationInformation());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getDatasetTitle(), result.getDatasetIdentificationInformation().getDatasetTitle());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getEncodingSpecification(), result.getDatasetIdentificationInformation().getEncodingSpecification());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getEncodingSpecificationEdition(), result.getDatasetIdentificationInformation().getEncodingSpecificationEdition());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getProductIdentifier(),result.getDatasetIdentificationInformation().getProductIdentifier());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getProductEdition(), result.getDatasetIdentificationInformation().getProductEdition());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getApplicationProfile(), result.getDatasetIdentificationInformation().getApplicationProfile());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getDatasetLanguage(), result.getDatasetIdentificationInformation().getDatasetLanguage());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getDatasetAbstract(), result.getDatasetIdentificationInformation().getDatasetAbstract());
        assertNotNull(result.getDatasetContent());
        assertEquals(this.existingDataset.getDatasetContent().getContent(), result.getDatasetContent().getContent());
        assertEquals(this.existingDataset.getDatasetContent().getContentLength(), result.getDatasetContent().getContentLength());;
        assertNull(result.getDatasetContent().getDelta());
        assertEquals(BigInteger.ZERO, result.getDatasetContent().getDeltaLength());
        assertTrue(result.getCancelled());
    }

    /**
     * Test that if we try to cancel a dataset that doesn't exist, a
     * DataNotFoundException will be thrown.
     */
    @Test
    void testCancelNotFound() {
        doReturn(Optional.empty()).when(this.datasetRepo).findById(any());

        // Perform the service call
        assertThrows(DataNotFoundException.class, () ->
                this.datasetService.cancel(UUID.randomUUID())
        );
    }

    /**
     * Test that if we try to cancel a dataset that has already been cancelled,
     * a ValidationException will be thrown.
     */
    @Test
    void testCancelAlreadyCancelled() {
        this.existingDataset.setCancelled(true);
        doReturn(Optional.of(this.existingDataset)).when(this.datasetRepo).findById(any());
        doReturn(Optional.of(this.existingDataset)).when(this.datasetRepo).findByUuidAndCancelled(any(), any());

        // Perform the service call
        assertThrows(ValidationException.class, () ->
                this.datasetService.cancel(UUID.randomUUID())
        );
    }

    /**
     * Test that we can successfully delete an existing dataset entry.
     */
    @Test
    void testDelete() throws DataNotFoundException {
        final BigInteger origSequenceNo = this.existingDataset.getDatasetContent().getSequenceNo();
        doReturn(Optional.of(this.existingDataset)).when(this.datasetRepo).findById(this.existingDataset.getUuid());
        doNothing().when(this.datasetRepo).delete(this.existingDataset);

        // Perform the service call
        S201Dataset result = this.datasetService.delete(this.existingDataset.getUuid());

        // Test the result
        assertNotNull(result.getDatasetContent());
        assertEquals(this.existingDataset.getDatasetContent().getContent(), result.getDatasetContent().getContent());
        assertEquals(this.existingDataset.getDatasetContent().getContentLength(), result.getDatasetContent().getContentLength());
        assertNull(result.getDatasetContent().getDelta());
        assertEquals(BigInteger.ZERO, result.getDatasetContent().getDeltaLength());

        // Verify that our message was deleted and sent
        verify(this.datasetRepo, times(1)).delete(any());
        verify(this.s201RemovalChannel, times(1)).send(any(Message.class));
    }

    /**
     * Test that if we try to delete a non-existing dataset entry then a
     * DataNotFoundException will be thrown.
     */
    @Test
    void testDeleteNotFound() {
        doReturn(Optional.empty()).when(this.datasetRepo).findById(any());

        // Perform the service call
        assertThrows(DataNotFoundException.class, () ->
                this.datasetService.delete(UUID.randomUUID())
        );
    }


    /**
     * Test that if the service is asked to replace the of a specific dataset,
     * then this will use the existing dataset functionality of cancelling the
     * existing dataset and re-creating a brand-new one based on a copy.
     */
    @Test
    void testReplace() {
        // Cancel the existing dataset
        this.existingDataset.setCancelled(Boolean.TRUE);

        // Mock the service calls
        doReturn(this.datasetService).when(this.applicationContext).getBean(eq(DatasetService.class));
        doReturn(this.existingDataset).when(this.datasetService).cancel(any());
        doReturn(this.existingDataset.replace()).when(this.datasetService).save(any());

        // Perform the service call
        S201Dataset result = this.datasetService.replace(this.existingDataset.getUuid());

        // Test the result
        // Note that this is a branch new entry without any content yet
        assertNotNull(result);
        assertNull(result.getUuid());
        assertEquals(this.existingDataset.getGeometry(), result.getGeometry());
        assertNotNull(result.getDatasetIdentificationInformation());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getDatasetTitle(), result.getDatasetIdentificationInformation().getDatasetTitle());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getEncodingSpecification(), result.getDatasetIdentificationInformation().getEncodingSpecification());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getEncodingSpecificationEdition(), result.getDatasetIdentificationInformation().getEncodingSpecificationEdition());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getProductIdentifier(),result.getDatasetIdentificationInformation().getProductIdentifier());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getProductEdition(), result.getDatasetIdentificationInformation().getProductEdition());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getApplicationProfile(), result.getDatasetIdentificationInformation().getApplicationProfile());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getDatasetLanguage(), result.getDatasetIdentificationInformation().getDatasetLanguage());
        assertEquals(this.existingDataset.getDatasetIdentificationInformation().getDatasetAbstract(), result.getDatasetIdentificationInformation().getDatasetAbstract());
        assertNull(result.getDatasetContent());
        assertFalse(result.getCancelled());
        assertEquals(this.existingDataset.getUuid(), result.getReplaces());
    }

    /**
     * Test that if the service is asked to replace specific dataset, and for
     * some reason in the replacement process the saving operation returned
     * null, a SavingFailedException will be thrown.
     */
    @Test
    void testReplaceFailed() {
        // Cancel the existing dataset
        this.existingDataset.setCancelled(Boolean.TRUE);

        // Mock the service calls
        doReturn(this.datasetService).when(this.applicationContext).getBean(eq(DatasetService.class));
        doReturn(this.existingDataset).when(this.datasetService).cancel(any());
        doReturn(null).when(this.datasetService).save(any());

        // Perform the service call
        assertThrows(SavingFailedException.class, () ->
                this.datasetService.replace(this.existingDataset.getUuid())
        );
    }

    /**
     * Test that we can correctly request a dataset content update from the
     * dataset content service. This will take place asynchronously so that it
     * does not hold back the responses of this service.
     */
    @Test
    void testRequestDatasetContentUpdate() {
        // Create a data content generation response to wait for
        CompletableFuture<S201Dataset> contentGenerationTask = CompletableFuture.completedFuture(this.newDataset);
        doReturn(contentGenerationTask).when(this.datasetContentService).generateDatasetContent(any());

        // Perform the service call
        this.datasetService.requestDatasetContentUpdate(UUID.randomUUID());

        // Wait until the end and verify that the message was published
        assertTrue(contentGenerationTask.isDone());
        verify(this.s201PublicationChannel, timeout(100).times(1)).send(any(Message.class));
    }

    /**
     * Test that if we request a dataset content update from the dataset content
     * service and for this dataset deleted AtoNs were detected, then the
     * default response would be to replace that dataset with a brand-new one.
     */
    @Test
    void testRequestDatasetContentUpdateDeletedAtons() {
        // Create a data content generation response to wait for
        CompletableFuture<S201Dataset> contentGenerationTask = CompletableFuture.failedFuture(
                new CompletionException(new DeletedAtoNsInDatasetContentGenerationException("deleted AtoNs detected"))
        );
        doReturn(contentGenerationTask).when(this.datasetContentService).generateDatasetContent(any());
        doReturn(this.newDataset).when(this.datasetService).replace(any());

        // Perform the service call
        this.datasetService.requestDatasetContentUpdate(this.existingDataset.getUuid());

        // Wait until the end and verify that the message was published
        assertTrue(contentGenerationTask.isCompletedExceptionally());
        verify(this.datasetService, timeout(100).times(1)).replace(eq(this.existingDataset.getUuid()));
        verify(this.s201PublicationChannel, timeout(100).times(0)).send(any(Message.class));
    }

    /**
     * Test that if we request a dataset content update from the dataset content
     * service and there is a problem, this service will handle any potential
     * failures.
     */
    @Test
    void testRequestDatasetContentUpdateFailure() {
        // Create a data content generation response to wait for
        CompletableFuture<S201Dataset> contentGenerationTask = CompletableFuture.failedFuture(
                new CompletionException(new RuntimeException("something went wrong"))
        );
        doReturn(contentGenerationTask).when(this.datasetContentService).generateDatasetContent(any());

        // Perform the service call
        this.datasetService.requestDatasetContentUpdate(this.existingDataset.getUuid());

        // Wait until the end and verify that the message was published
        assertTrue(contentGenerationTask.isCompletedExceptionally());
        verify(this.datasetService, never()).replace(any());
        verify(this.s201PublicationChannel, timeout(100).times(0)).send(any(Message.class));
    }

}