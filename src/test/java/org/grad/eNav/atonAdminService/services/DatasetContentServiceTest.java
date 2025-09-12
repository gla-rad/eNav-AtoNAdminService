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

import jakarta.persistence.EntityManager;
import org.apache.commons.io.IOUtils;
import org.grad.eNav.atonAdminService.config.GlobalConfig;
import org.grad.eNav.atonAdminService.exceptions.DeletedAtoNsInDatasetContentGenerationException;
import org.grad.eNav.atonAdminService.exceptions.SavingFailedException;
import org.grad.eNav.atonAdminService.models.domain.DatasetContent;
import org.grad.eNav.atonAdminService.models.domain.s201.*;
import org.grad.eNav.atonAdminService.repos.DatasetContentRepo;
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
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatasetContentServiceTest {

    // Regular expression to look for member tags
    final Pattern DATASET_MEMBER_PATTERN = Pattern.compile("<(/)?[\\s\\S][\\s\\S]\\d:CardinalBeacon");

    /**
     * The Tested Service.
     */
    @InjectMocks
    @Spy
    DatasetContentService datasetContentService;

    /**
     * The Model Mapper.
     */
    @Spy
    ModelMapper modelMapper;

    /**
     * The Entity Manager mock.
     */
    @Mock
    EntityManager entityManager;

    /**
     * The Aids to Navigation Service.
     */
    @Mock
    AidsToNavigationService aidsToNavigationService;

    /**
     * The Dataset Service.
     */
    @Mock
    DatasetService datasetService;

    /**
     * The Dataset Content Repo mock.
     */
    @Mock
    DatasetContentRepo datasetContentRepo;

    // Test Variables
    private GeometryFactory factory;
    private List<AidsToNavigation> aidsToNavigationList;
    private S201Dataset newDataset;
    private S201Dataset existingDataset;
    private DatasetContent newDatasetContent;
    private DatasetContent existingDatasetContent;

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
            Information information = new Information();
            information.setText("Description of AtoN No" + i);
            aidsToNavigation.setInformations(Collections.singleton(information));
            this.aidsToNavigationList.add(aidsToNavigation);
        }

        // Create a new Dataset with a UUID (required for saving)
        this.newDataset = new S201Dataset("NewDataset");
        this.newDataset.setUuid(UUID.randomUUID());
        this.newDataset.setGeometry(this.factory.createPoint(new Coordinate(52.98, 2.28)));
        this.newDataset.setCreatedAt(LocalDateTime.now());
        this.newDataset.setLastUpdatedAt(LocalDateTime.now());

        // Create a Dataset with a UUID
        this.existingDataset = new S201Dataset("ExistingDataset");
        this.existingDataset.setUuid(UUID.randomUUID());
        this.existingDataset.setGeometry(this.factory.createPoint(new Coordinate(52.98, 2.28)));
        this.existingDataset.setCreatedAt(LocalDateTime.now());
        this.existingDataset.setLastUpdatedAt(LocalDateTime.now());

        // Create a test new content for the dataset
        this.newDatasetContent = new DatasetContent();
        this.newDatasetContent.setDataset(this.newDataset);
        this.newDatasetContent.setGeneratedAt(LocalDateTime.now());
        this.newDatasetContent.setContent("New dataset content");
        this.newDatasetContent.setContentLength(BigInteger.valueOf(this.newDatasetContent.getContent().length()));
        this.newDatasetContent.setDelta("");
        this.newDatasetContent.setDeltaLength(BigInteger.ZERO);
        this.newDataset.setDatasetContent(newDatasetContent);

        // And create a test existing content for the dataset
        this.existingDatasetContent = new DatasetContent();
        this.existingDatasetContent.setId(BigInteger.TWO);
        this.existingDatasetContent.setDataset(this.existingDataset);
        this.existingDatasetContent.setGeneratedAt(LocalDateTime.now());
        this.existingDatasetContent.setContent("Existing dataset content");
        this.existingDatasetContent.setContentLength(BigInteger.valueOf(this.existingDatasetContent.getContent().length()));
        this.existingDatasetContent.setDelta("Existing dataset delta");
        this.existingDatasetContent.setDeltaLength(BigInteger.valueOf(this.existingDatasetContent.getDelta().length()));
        this.existingDataset.setDatasetContent(this.existingDatasetContent);
    }

    /**
     * Test that we can successfully save a new dataset content into the
     * database and the updated result will be returned.
     */
    @Test
    void testSave() {
        doReturn(this.newDatasetContent).when(this.datasetContentRepo).saveAndFlush(any());

        // Perform the service call
        DatasetContent result = this.datasetContentService.save(this.newDatasetContent);

        // Test the result
        assertNotNull(result);
        assertEquals(this.newDatasetContent.getId(), result.getId());
        assertEquals(this.newDatasetContent.getGeneratedAt(), result.getGeneratedAt());
        assertEquals(this.newDatasetContent.getSequenceNo(), result.getSequenceNo());
        assertEquals(this.newDatasetContent.getContent(), result.getContent());
        assertEquals(this.newDatasetContent.getContentLength(), result.getContentLength());
        assertEquals(this.newDatasetContent.getDelta(), result.getDelta());
        assertEquals(this.newDatasetContent.getDeltaLength(), result.getDeltaLength());
    }

    /**
     * Test that we can successfully save a new dataset content into the
     * database and the updated result will be returned.
     */
    @Test
    void testSaveWithoutDataset() {
        // Remove the dataset link from the dataset content
        this.newDatasetContent.setDataset(null);

        // Perform the service call
        assertThrows(SavingFailedException.class, () ->
                this.datasetContentService.save(this.newDatasetContent)
        );
    }

    /**
     * Test that we can successfully generate the content of a dataset provided
     * that we can access its respective member entries. In the current case
     * this should be an S-201 dataset with the same number of members in the
     * content as the AtoN that are assigned to it.
     */
    @Test
    void testGenerateDatasetContent() throws ExecutionException, InterruptedException {
        final int numOfAtons = 5;
        final Page<AidsToNavigation> aidsToNavigationPage = new PageImpl<>(this.aidsToNavigationList.subList(0, numOfAtons), Pageable.ofSize(5), this.aidsToNavigationList.size());

        // Get the model mapper configuration from the GlobalConfig
        this.datasetContentService.modelMapper = new GlobalConfig().modelMapper();

        // Mock the service calls
        doReturn(this.existingDataset).when(this.datasetService).findOne(eq(this.existingDataset.getUuid()));
        doReturn(aidsToNavigationPage).when(this.aidsToNavigationService).findAll(any(), any(), any(), any(), any());
        doAnswer((inv) -> inv.getArgument(0)).when(this.datasetContentService).save(any());

        // Perform the service call
        CompletableFuture<S201Dataset> result = this.datasetContentService.generateDatasetContent(this.existingDataset.getUuid());

        // Test the result
        assertNotNull(result);
        assertTrue(result.isDone());
        assertNotNull(result.get());

        // Now extract the dataset from the result
        S201Dataset resultDataset = result.get();
        assertNotNull(resultDataset.getDatasetContent());
        assertNotNull(resultDataset.getDatasetContent().getContent());
        assertEquals(2*numOfAtons, DATASET_MEMBER_PATTERN.matcher(resultDataset.getDatasetContent().getContent()).results().count());
        assertEquals(BigInteger.valueOf(result.get().getDatasetContent().getContent().length()), result.get().getDatasetContent().getContentLength());

        // Make also sure that we save and published the generated content
        verify(this.datasetContentService, times(1)).save(any(DatasetContent.class));
    }

    /**
     * Test that if we ty to generate the content of a dataset provided and an
     * exception is thrown, the CompletableFuture response will include the
     * message of that exception.
     */
    @Test
    void testGenerateDatasetContentWithException() {
        final int numOfAtons = 5;
        final Page<AidsToNavigation> aidsToNavigationPage = new PageImpl<>(this.aidsToNavigationList.subList(0, numOfAtons), Pageable.ofSize(5), this.aidsToNavigationList.size());

        // Mock the service calls
        doReturn(this.existingDataset).when(this.datasetService).findOne(eq(this.existingDataset.getUuid()));
        doReturn(aidsToNavigationPage).when(this.aidsToNavigationService).findAll(any(), any(), any(), any(), any());
        doThrow(new MappingException(Collections.emptyList())).when(this.modelMapper).map(any(), any());

        // Perform the service call
        CompletableFuture<S201Dataset> result = this.datasetContentService.generateDatasetContent(this.existingDataset.getUuid());

        // Test the result
        assertNotNull(result);
        assertTrue(result.isDone());
        assertTrue(result.isCompletedExceptionally());
        assertThrows(ExecutionException.class, result::get);

        // Make also sure that we save and published the generated content
        verify(this.datasetContentRepo, never()).save(any(DatasetContent.class));
    }

    /**
     * Test that if we ty to generate the content of a dataset provided and it
     * is detected that AtoNs have been removed from it, the CompletableFuture
     * response will include a DeletedAtoNsInDatasetContentGenerationException.
     */
    @Test
    void testGenerateDatasetContentWithDeletedAtons() throws IOException {
        // Read a valid S-201 content to set it as the dataset content
        final InputStream in = new ClassPathResource("s201-msg.xml").getInputStream();
        this.existingDataset.getDatasetContent().setContent(IOUtils.toString(in, StandardCharsets.UTF_8));

        // Mock the service calls
        doReturn(this.existingDataset).when(this.datasetService).findOne(eq(this.existingDataset.getUuid()));
        doReturn(new PageImpl<>(Collections.emptyList())).when(this.aidsToNavigationService).findAll(any(), any(), any(), any(), any());

        // Perform the service call
        CompletableFuture<S201Dataset> result = this.datasetContentService.generateDatasetContent(this.existingDataset.getUuid());

        // Test the result
        assertNotNull(result);
        assertTrue(result.isDone());
        assertTrue(result.isCompletedExceptionally());
        assertThrows(ExecutionException.class, result::get);

        // Make sure the correct exception was thrown
        try {
            result.get();
        } catch (InterruptedException | ExecutionException ex) {
            assertTrue(ex.getCause() instanceof DeletedAtoNsInDatasetContentGenerationException);
        }

        // Make also sure that did not try to save/publish
        verify(this.datasetContentRepo, never()).save(any(DatasetContent.class));
    }

}