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

package org.grad.eNav.atonAdminService.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.grad.eNav.atonAdminService.TestFeignSecurityConfig;
import org.grad.eNav.atonAdminService.TestingConfiguration;
import org.grad.eNav.atonAdminService.exceptions.DataNotFoundException;
import org.grad.eNav.atonAdminService.models.domain.s201.S201Dataset;
import org.grad.eNav.atonAdminService.models.dtos.datatables.*;
import org.grad.eNav.atonAdminService.models.dtos.s201.S201DataSetDto;
import org.grad.eNav.atonAdminService.services.DatasetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.cloud.openfeign.support.PageJacksonModule;
import org.springframework.cloud.openfeign.support.SortJacksonModule;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DatasetController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Import({TestingConfiguration.class, TestFeignSecurityConfig.class})
class DatasetControllerTest {

    /**
     * The Mock MVC.
     */
    @Autowired
    MockMvc mockMvc;

    /**
     * The JSON Object Mapper.
     */
    @Autowired
    ObjectMapper objectMapper;

    /**
     * The Dataset Service mock.
     */
    @MockitoBean
    DatasetService datasetService;

    // Test Variables
    private List<S201Dataset> datasetList;
    private Pageable pageable;
    private S201Dataset newDataset;
    private S201Dataset existingDataset;
    private GeometryFactory factory;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setUp() {
        // Create a temp geometry factory to get a test geometries
        this.factory = new GeometryFactory(new PrecisionModel(), 4326);

        // Allow the object mapper to deserialize pages
        this.objectMapper.registerModule(new PageJacksonModule());
        this.objectMapper.registerModule(new SortJacksonModule());

        // Initialise the dataset list
        this.datasetList = new ArrayList<>();
        for(long i=0; i<10; i++) {
            S201Dataset dataset = new S201Dataset(String.format("Dataset %d", i));
            dataset.setGeometry(this.factory.createPoint(new Coordinate(i, i)));
            dataset.setCancelled(false);
            this.datasetList.add(dataset);
        }

        // Create a pageable definition
        this.pageable = PageRequest.of(0, 5);

        // Create a Dataset without an ID
        this.newDataset = new S201Dataset("NewDataset");
        this.newDataset.setGeometry(this.factory.createPoint(new Coordinate(51.98, 1.28)));
        this.newDataset.setCancelled(false);

        // Create a Dataset with an ID
        this.existingDataset = new S201Dataset("ExistingDataset");
        this.existingDataset.setUuid(UUID.randomUUID());
        this.existingDataset.setGeometry(this.factory.createPoint(new Coordinate(52.98, 2.28)));
        this.existingDataset.setCancelled(false);
    }

    /**
     * Test that we can retrieve the datasets currently in the database in a
     * paged result.
     */
    @Test
    void testGetDatasets() throws Exception {
        // Created a result page to be returned by the mocked service
        Page<S201Dataset> page = new PageImpl<>(this.datasetList.subList(0, 5), this.pageable, this.datasetList.size());
        doReturn(page).when(this.datasetService).findAll(any(), any(), any(), any(), any(), any());

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(get("/api/dataset"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        // Parse and validate the response
        Page<S201DataSetDto> result = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(page.getSize(), result.getContent().size());

        // Validate the entries one by one
        for(int i=0; i< page.getSize(); i++) {
            assertNotNull(result.getContent().get(i));
            assertEquals(page.getContent().get(i).getUuid(), result.getContent().get(i).getUuid());
            assertEquals(page.getContent().get(i).getGeometry(), result.getContent().get(i).getGeometry());
            assertNotNull(result.getContent().get(i).getDatasetIdentificationInformation());
            assertEquals(page.getContent().get(i).getDatasetIdentificationInformation().getDatasetTitle(), result.getContent().get(i).getDatasetIdentificationInformation().getDatasetTitle());
            assertEquals(page.getContent().get(i).getDatasetIdentificationInformation().getEncodingSpecification(), result.getContent().get(i).getDatasetIdentificationInformation().getEncodingSpecification());
            assertEquals(page.getContent().get(i).getDatasetIdentificationInformation().getEncodingSpecificationEdition(), result.getContent().get(i).getDatasetIdentificationInformation().getEncodingSpecificationEdition());
            assertEquals(page.getContent().get(i).getDatasetIdentificationInformation().getProductIdentifier(), result.getContent().get(i).getDatasetIdentificationInformation().getProductIdentifier());
            assertEquals(page.getContent().get(i).getDatasetIdentificationInformation().getProductEdition(), result.getContent().get(i).getDatasetIdentificationInformation().getProductEdition());
            assertEquals(page.getContent().get(i).getDatasetIdentificationInformation().getApplicationProfile(), result.getContent().get(i).getDatasetIdentificationInformation().getApplicationProfile());
            assertEquals(page.getContent().get(i).getDatasetIdentificationInformation().getDatasetLanguage(), result.getContent().get(i).getDatasetIdentificationInformation().getDatasetLanguage());
            assertEquals(page.getContent().get(i).getDatasetIdentificationInformation().getDatasetAbstract(), result.getContent().get(i).getDatasetIdentificationInformation().getDatasetAbstract());
            assertEquals(page.getContent().get(i).getCancelled(),  result.getContent().get(i).isCancelled());
        }
    }

    /**
     * Test that the API supports the jQuery Datatables server-side paging
     * and search requests.
     */
    @Test
    void testGetDatasetsForDatatables() throws Exception {
        // Create a test datatables paging request
        DtColumn dtColumn = new DtColumn("id");
        dtColumn.setName("ID");
        dtColumn.setOrderable(true);
        DtOrder dtOrder = new DtOrder();
        dtOrder.setColumn(0);
        dtOrder.setDir(DtDirection.asc);
        DtPagingRequest dtPagingRequest = new DtPagingRequest();
        dtPagingRequest.setStart(0);
        dtPagingRequest.setLength(this.datasetList.size());
        dtPagingRequest.setDraw(1);
        dtPagingRequest.setSearch(new DtSearch());
        dtPagingRequest.setOrder(Collections.singletonList(dtOrder));
        dtPagingRequest.setColumns(Collections.singletonList(dtColumn));

        // Created a result page to be returned by the mocked service
        Page<S201Dataset> page = new PageImpl<>(this.datasetList.subList(0, 5), this.pageable, this.datasetList.size());
        doReturn(page).when(this.datasetService).handleDatatablesPagingRequest(any());

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(post("/api/dataset/dt?includeCancelled=true")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(dtPagingRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Parse and validate the response
        DtPage<S201DataSetDto> result = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(page.getSize(), result.getData().size());

        // Validate the entries one by one
        for(int i=0; i< page.getSize(); i++) {
            assertNotNull(result.getData().get(i));
            assertEquals(page.getContent().get(i).getUuid(), result.getData().get(i).getUuid());
            assertEquals(page.getContent().get(i).getGeometry(), result.getData().get(i).getGeometry());
            assertNotNull(result.getData().get(i).getDatasetIdentificationInformation());
            assertEquals(page.getContent().get(i).getDatasetIdentificationInformation().getDatasetTitle(), result.getData().get(i).getDatasetIdentificationInformation().getDatasetTitle());
            assertEquals(page.getContent().get(i).getDatasetIdentificationInformation().getEncodingSpecification(), result.getData().get(i).getDatasetIdentificationInformation().getEncodingSpecification());
            assertEquals(page.getContent().get(i).getDatasetIdentificationInformation().getEncodingSpecificationEdition(), result.getData().get(i).getDatasetIdentificationInformation().getEncodingSpecificationEdition());
            assertEquals(page.getContent().get(i).getDatasetIdentificationInformation().getProductIdentifier(),result.getData().get(i).getDatasetIdentificationInformation().getProductIdentifier());
            assertEquals(page.getContent().get(i).getDatasetIdentificationInformation().getProductEdition(), result.getData().get(i).getDatasetIdentificationInformation().getProductEdition());
            assertEquals(page.getContent().get(i).getDatasetIdentificationInformation().getApplicationProfile(), result.getData().get(i).getDatasetIdentificationInformation().getApplicationProfile());
            assertEquals(page.getContent().get(i).getDatasetIdentificationInformation().getDatasetLanguage(), result.getData().get(i).getDatasetIdentificationInformation().getDatasetLanguage());
            assertEquals(page.getContent().get(i).getDatasetIdentificationInformation().getDatasetAbstract(), result.getData().get(i).getDatasetIdentificationInformation().getDatasetAbstract());
            assertEquals(page.getContent().get(i).getCancelled(),  result.getData().get(i).isCancelled());
        }
    }

    /**
     * Test that we can create a new dataset correctly through a POST request.
     * The incoming station should NOT have an ID, while the returned
     * value will have the UUID field populated.
     */
    @Test
    void testCreateDataset() throws Exception {
        // Mock the service call for creating a new instance
        doReturn(this.existingDataset).when(this.datasetService).save(any());

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(post("/api/dataset")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(new ModelMapper().map(this.newDataset, S201DataSetDto.class))))
                .andExpect(status().isCreated())
                .andExpect(header().exists("location"))
                .andExpect(header().string("location", String.format("/api/dataset/%s", this.existingDataset.getUuid())))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        // Parse and validate the response
        S201DataSetDto result = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), S201DataSetDto.class);
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
        assertEquals(this.existingDataset.getCancelled(),  result.isCancelled());
    }

    /**
     * Test that if we try to create a dataset with an existing UUID field,
     * an HTTP BAD_REQUEST response will be returns, with a description of
     * the error in the header.
     */
    @Test
    void testCreateDatasetWithId() throws Exception {
        // Perform the MVC request
        this.mockMvc.perform(post("/api/dataset")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(new ModelMapper().map(this.existingDataset, S201DataSetDto.class))))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-atonService-error"))
                .andExpect(header().exists("X-atonService-params"))
                .andReturn();
    }

    /**
     * Test that we can update an existing dataset correctly through a PUT
     * request. The incoming dataset should always have a UUID.
     */
    @Test
    void testUpdateDataset() throws Exception {
        // Mock the service call for updating an existing instance
        doReturn(this.existingDataset).when(this.datasetService).save(any());

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(put("/api/dataset/{uuid}", this.existingDataset.getUuid())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(new ModelMapper().map(this.existingDataset, S201DataSetDto.class))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        // Parse and validate the response
        S201DataSetDto result = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), S201DataSetDto.class);
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
        assertEquals(existingDataset.getCancelled(),  result.isCancelled());
    }

    /**
     * Test that if we fail to update the provided dataset due to a general
     * error, an HTTP BAD_REQUEST response will be returned, with a description
     * of the error in the header.
     */
    @Test
    void testUpdateDatasetFailure() throws Exception {
        // Mock a general Exception when saving the instance
        doThrow(RuntimeException.class).when(this.datasetService).cancel(any());

        // Perform the MVC request
        this.mockMvc.perform(put("/api/dataset/{uuid}", this.existingDataset.getUuid())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(new ModelMapper().map(this.newDataset, S201DataSetDto.class))))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-atonService-error"))
                .andExpect(header().exists("X-atonService-params"))
                .andReturn();
    }

    /**
     * Test that we can cancel an existing dataset correctly through a PUT
     * request. The incoming dataset should always have a UUID.
     */
    @Test
    void testCancelDataset() throws Exception {
        // First cancel the dataset for the response
        this.existingDataset.setCancelled(true);

        // Mock the service call for updating an existing instance
        doReturn(this.existingDataset).when(this.datasetService).cancel(any());

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(put("/api/dataset/{uuid}/cancel", this.existingDataset.getUuid())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(new ModelMapper().map(this.existingDataset, S201DataSetDto.class))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        // Parse and validate the response
        S201DataSetDto result = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), S201DataSetDto.class);
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
        assertTrue(result.isCancelled());
    }

    /**
     * Test that if we fail to cancel the provided dataset due to a general
     * error, an HTTP BAD_REQUEST response will be returned, with a description
     * of the error in the header.
     */
    @Test
    void testCancelDatasetFailure() throws Exception {
        // Mock a general Exception when saving the instance
        doThrow(RuntimeException.class).when(this.datasetService).cancel(any());

        // Perform the MVC request
        this.mockMvc.perform(put("/api/dataset/{uuid}/cancel", this.existingDataset.getUuid())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(new ModelMapper().map(this.newDataset, S201DataSetDto.class))))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-atonService-error"))
                .andExpect(header().exists("X-atonService-params"))
                .andReturn();
    }

    /**
     * Test that we can replace an existing dataset correctly through a PUT
     * request. The incoming dataset should always have a UUID.
     */
    @Test
    void testReplaceDataset() throws Exception {
        // First cancel the dataset for the response
        this.existingDataset.setCancelled(true);
        // And create a new UUID for the new one
        this.newDataset.setUuid(UUID.randomUUID());

        // Mock the service call for updating an existing instance
        doReturn(this.newDataset).when(this.datasetService).replace(any());

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(put("/api/dataset/{uuid}/replace", this.existingDataset.getUuid())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(new ModelMapper().map(this.existingDataset, S201DataSetDto.class))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        // Parse and validate the response
        S201DataSetDto result = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), S201DataSetDto.class);
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
        assertFalse(result.isCancelled());
    }

    /**
     * Test that if we fail to replace the provided dataset due to a general
     * error, an HTTP BAD_REQUEST response will be returned, with a description
     * of the error in the header.
     */
    @Test
    void testReplaceDatasetFailure() throws Exception {
        // Mock a general Exception when saving the instance
        doThrow(RuntimeException.class).when(this.datasetService).replace(any());

        // Perform the MVC request
        this.mockMvc.perform(put("/api/dataset/{uuid}/replace", this.existingDataset.getUuid())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(new ModelMapper().map(this.newDataset, S201DataSetDto.class))))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-atonService-error"))
                .andExpect(header().exists("X-atonService-params"))
                .andReturn();
    }

    /**
     * Test that we can correctly delete an existing dataset by using a valid
     * UUID.
     */
    @Test
    void testDeleteDataset() throws Exception {
        // Mock the service call for deleting an existing instance
        doReturn(this.existingDataset).when(this.datasetService).delete(any());

        // Perform the MVC request
        this.mockMvc.perform(delete("/api/dataset/{uuid}", this.existingDataset.getUuid())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
    }

    /**
     * Test that if we do NOT find the dataset we are trying to delete, an HTTP
     * NOT_FOUND response will be returned.
     */
    @Test
    void testDeleteDatasetNotFound() throws Exception {
        doThrow(DataNotFoundException.class).when(this.datasetService).delete(any());

        // Perform the MVC request
        this.mockMvc.perform(delete("/api/dataset/{uuid}", this.existingDataset.getUuid()))
                .andExpect(status().isNotFound());
    }
}