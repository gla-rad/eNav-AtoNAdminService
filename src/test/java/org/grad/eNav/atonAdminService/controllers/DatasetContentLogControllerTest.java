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
import org.grad.eNav.atonAdminService.models.domain.DatasetContentLog;
import org.grad.eNav.atonAdminService.models.dtos.DatasetContentLogDto;
import org.grad.eNav.atonAdminService.models.dtos.datatables.*;
import org.grad.eNav.atonAdminService.models.enums.DatasetOperation;
import org.grad.eNav.atonAdminService.models.enums.DatasetType;
import org.grad.eNav.atonAdminService.services.DatasetContentLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.openfeign.support.PageJacksonModule;
import org.springframework.cloud.openfeign.support.SortJacksonModule;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DatasetContentLogController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Import({TestingConfiguration.class, TestFeignSecurityConfig.class})
class DatasetContentLogControllerTest {

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
    @MockBean
    DatasetContentLogService datasetContentLogService;
    private DatasetContentLog datasetContentLog;

    // Test Variables
    private List<DatasetContentLog> datasetContentLogList;
    private Pageable pageable;
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

        // Create a pageable definition
        this.pageable = PageRequest.of(0, 5);

        // Create an existing Dataset Content Log entry
        this.datasetContentLog = new DatasetContentLog();
        this.datasetContentLog.setId(BigInteger.ONE);
        this.datasetContentLog.setDatasetType(DatasetType.S201);
        this.datasetContentLog.setSequenceNo(BigInteger.ONE);
        this.datasetContentLog.setGeneratedAt(LocalDateTime.now());
        this.datasetContentLog.setGeometry(factory.createPoint(new Coordinate(1, 1)));
        this.datasetContentLog.setOperation(DatasetOperation.UPDATED);
        this.datasetContentLog.setContent("Dataset Content Log");
        this.datasetContentLog.setContentLength(BigInteger.valueOf(this.datasetContentLog.getContent().length()));
        this.datasetContentLog.setDelta("Dataset Content Log Delta");
        this.datasetContentLog.setDeltaLength(BigInteger.valueOf(this.datasetContentLog.getDelta().length()));

        // Initialise the dataset content log list
        this.datasetContentLogList = new ArrayList<>();
        for(long i=0; i<10; i++) {
            DatasetContentLog datasetContentLog = new DatasetContentLog();
            datasetContentLog.setId(BigInteger.valueOf(i));
            datasetContentLog.setDatasetType(DatasetType.S201);
            datasetContentLog.setSequenceNo(BigInteger.ONE);
            datasetContentLog.setGeneratedAt(LocalDateTime.now());
            datasetContentLog.setGeometry(factory.createPoint(new Coordinate(i%180, i%90)));
            datasetContentLog.setOperation(DatasetOperation.UPDATED);
            datasetContentLog.setContent("Existing Dataset Content " + i);
            datasetContentLog.setContentLength(BigInteger.valueOf(datasetContentLog.getContent().length()));
            datasetContentLog.setDelta("Dataset Content Log Delta " + i);
            datasetContentLog.setDeltaLength(BigInteger.valueOf(datasetContentLog.getDelta().length()));
            this.datasetContentLogList.add(datasetContentLog);
        }
    }

    /**
     * Test that we can retrieve the data content of a single dataset content
     * log entry if this exists.
     */
    @Test
    void getDatasetContentLogData() throws Exception {
        // Created a result page to be returned by the mocked service
        doReturn(datasetContentLog).when(this.datasetContentLogService).findOne(any());

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(get("/api/datasetcontentlog/" + this.datasetContentLog.getId() + "/data"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andReturn();

        // Parse and validate the response
        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
        assertEquals(this.datasetContentLog.getContent(), result);
    }

    /**
     * Test that we can retrieve the delta of a single dataset content log entry
     * if this exists.
     */
    @Test
    void getDatasetContentLogDelta() throws Exception {
        // Created a result page to be returned by the mocked service
        doReturn(datasetContentLog).when(this.datasetContentLogService).findOne(any());

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(get("/api/datasetcontentlog/" + this.datasetContentLog.getId() + "/delta"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andReturn();

        // Parse and validate the response
        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
        assertEquals(this.datasetContentLog.getDelta(), result);
    }

    /**
     * Test that we can retrieve the dataset content logs currently in the
     * database in a paged result.
     */
    @Test
    void testGetDatasetDatasetContentLogs() throws Exception {
        // Created a result page to be returned by the mocked service
        Page<DatasetContentLog> page = new PageImpl<>(this.datasetContentLogList.subList(0, 5), this.pageable, this.datasetContentLogList.size());
        doReturn(page).when(this.datasetContentLogService).findAll(any());

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(get("/api/datasetcontentlog"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        // Parse and validate the response
        Page<DatasetContentLogDto> result = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(page.getSize(), result.getContent().size());

        // Validate the entries one by one
        for(int i=0; i< page.getSize(); i++) {
            assertNotNull(result.getContent().get(i));
            assertEquals(page.getContent().get(i).getId(), result.getContent().get(i).getId());
            assertEquals(page.getContent().get(i).getUuid(), result.getContent().get(i).getUuid());
            assertEquals(page.getContent().get(i).getDatasetType(), result.getContent().get(i).getDatasetType());
            assertEquals(page.getContent().get(i).getGeneratedAt(), result.getContent().get(i).getGeneratedAt());
            assertEquals(page.getContent().get(i).getGeometry(), result.getContent().get(i).getGeometry());
            assertEquals(page.getContent().get(i).getOperation(), result.getContent().get(i).getOperation());
            assertEquals(page.getContent().get(i).getSequenceNo(), result.getContent().get(i).getSequenceNo());
        }
    }

    /**
     * Test that the API supports the jQuery Datatables server-side paging
     * and search requests.
     */
    @Test
    void testGetDatasetContentLogsForDatatables() throws Exception {
        // Create a test datatables paging request
        DtColumn dtColumn = new DtColumn("id");
        dtColumn.setName("ID");
        dtColumn.setOrderable(true);
        DtOrder dtOrder = new DtOrder();
        dtOrder.setColumn(0);
        dtOrder.setDir(DtDirection.asc);
        DtPagingRequest dtPagingRequest = new DtPagingRequest();
        dtPagingRequest.setStart(0);
        dtPagingRequest.setLength(this.datasetContentLogList.size());
        dtPagingRequest.setDraw(1);
        dtPagingRequest.setSearch(new DtSearch());
        dtPagingRequest.setOrder(Collections.singletonList(dtOrder));
        dtPagingRequest.setColumns(Collections.singletonList(dtColumn));

        // Created a result page to be returned by the mocked service
        Page<DatasetContentLog> page = new PageImpl<>(this.datasetContentLogList.subList(0, 5), this.pageable, this.datasetContentLogList.size());
        doReturn(page).when(this.datasetContentLogService).handleDatatablesPagingRequest(any());

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(post("/api/datasetcontentlog/dt")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(dtPagingRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Parse and validate the response
        DtPage<DatasetContentLogDto> result = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(page.getSize(), result.getData().size());

        // Validate the entries one by one
        for(int i=0; i< page.getSize(); i++) {
            assertNotNull(result.getData().get(i));
            assertEquals(page.getContent().get(i).getId(), result.getData().get(i).getId());
            assertEquals(page.getContent().get(i).getUuid(), result.getData().get(i).getUuid());
            assertEquals(page.getContent().get(i).getDatasetType(), result.getData().get(i).getDatasetType());
            assertEquals(page.getContent().get(i).getGeneratedAt(), result.getData().get(i).getGeneratedAt());
            assertEquals(page.getContent().get(i).getGeometry(), result.getData().get(i).getGeometry());
            assertEquals(page.getContent().get(i).getOperation(), result.getData().get(i).getOperation());
            assertEquals(page.getContent().get(i).getSequenceNo(), result.getData().get(i).getSequenceNo());
        }
    }

}