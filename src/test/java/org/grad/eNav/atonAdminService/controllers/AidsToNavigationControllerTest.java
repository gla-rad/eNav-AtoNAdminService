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
import org.grad.eNav.atonAdminService.models.domain.s201.AidsToNavigation;
import org.grad.eNav.atonAdminService.models.domain.s201.BeaconCardinal;
import org.grad.eNav.atonAdminService.models.domain.s201.FeatureName;
import org.grad.eNav.atonAdminService.models.domain.s201.Information;
import org.grad.eNav.atonAdminService.models.dtos.datatables.*;
import org.grad.eNav.atonAdminService.models.dtos.s201.AidsToNavigationDto;
import org.grad.eNav.atonAdminService.models.dtos.s201.FeatureNameDto;
import org.grad.eNav.atonAdminService.models.dtos.s201.InformationDto;
import org.grad.eNav.atonAdminService.services.AidsToNavigationService;
import org.grad.eNav.atonAdminService.services.DatasetService;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AidsToNavigationController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Import({TestingConfiguration.class, TestFeignSecurityConfig.class})
class AidsToNavigationControllerTest {

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
     * The Aids To Navigation Service mock.
     */
    @MockBean
    AidsToNavigationService aidsToNavigationService;

    /**
     * The Dataset Service mock.
     */
    @MockBean
    DatasetService datasetService;

    // Test Variables
    private List<AidsToNavigation> aidsToNavigationList;
    private Pageable pageable;
    private AidsToNavigation existingAidsToNavigation;
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

        // Initialise the station nodes list
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

        // Create a pageable definition
        this.pageable = PageRequest.of(0, 5);

        // Create a AtoN message with an ID
        existingAidsToNavigation = new BeaconCardinal();
        existingAidsToNavigation.setId(BigInteger.valueOf(1));
        existingAidsToNavigation.setIdCode("ID001");
        existingAidsToNavigation.setGeometry(factory.createPoint(new Coordinate(1, 1)));
        // Add the feature name entries
        FeatureName featureName = new FeatureName();
        featureName.setId(BigInteger.ONE);
        featureName.setName("Aton No 1");
        existingAidsToNavigation.setFeatureNames(Collections.singleton(featureName));
        // Add the information entries
        Information information = new Information();
        information.setId(BigInteger.ONE);
        information.setText("Description of AtoN No 1");
        existingAidsToNavigation.setInformations(Collections.singleton(information));
    }

    /**
     * Test that we can retrieve a list of the Aids to Navigation currently in
     * the database in a single result.
     */
    @Test
    void testGetListOfAidsToNavigation() throws Exception {
        // Created a result page to be returned by the mocked service
        Page<AidsToNavigation> page = new PageImpl<>(this.aidsToNavigationList.subList(0, 5), this.pageable, this.aidsToNavigationList.size());
        doReturn(page).when(this.aidsToNavigationService).findAll(any(), any(), any(), any(), any());

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(get("/api/atons/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        // Parse and validate the response
        List<AidsToNavigationDto> result = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(page.getSize(), result.size());

        // Validate the entries one by one
        for(int i=0; i< page.getSize(); i++) {
            assertEquals(page.getContent().get(i).getId(), result.get(i).getId());
            assertEquals(page.getContent().get(i).getIdCode(), result.get(i).getIdCode());
            assertEquals(page.getContent().get(i).getInformations().size(), result.get(i).getInformations().size());
            assertEquals(page.getContent().get(i).getInformations().stream().findFirst().map(Information::getFileLocator).orElse(null),
                    result.get(i).getInformations().stream().findFirst().map(InformationDto::getFileLocator).orElse(null));
            assertEquals(page.getContent().get(i).getInformations().stream().findFirst().map(Information::getFileReference).orElse(null),
                    result.get(i).getInformations().stream().findFirst().map(InformationDto::getFileReference).orElse(null));
            assertEquals(page.getContent().get(i).getInformations().stream().findFirst().map(Information::getHeadline).orElse(null),
                    result.get(i).getInformations().stream().findFirst().map(InformationDto::getHeadline).orElse(null));
            assertEquals(page.getContent().get(i).getInformations().stream().findFirst().map(Information::getLanguage).orElse(null),
                    result.get(i).getInformations().stream().findFirst().map(InformationDto::getLanguage).orElse(null));
            assertEquals(page.getContent().get(i).getInformations().stream().findFirst().map(Information::getText).orElse(null),
                    result.get(i).getInformations().stream().findFirst().map(InformationDto::getText).orElse(null));
            assertEquals(page.getContent().get(i).getFeatureNames().size(), result.get(i).getFeatureNames().size());
            assertEquals(page.getContent().get(i).getFeatureNames().stream().findFirst().map(FeatureName::getName).orElse(null),
                    result.get(i).getFeatureNames().stream().findFirst().map(FeatureNameDto::getName).orElse(null));
            assertEquals(page.getContent().get(i).getFeatureNames().stream().findFirst().map(FeatureName::getDisplayName).orElse(null),
                    result.get(i).getFeatureNames().stream().findFirst().map(FeatureNameDto::getDisplayName).orElse(null));
            assertEquals(page.getContent().get(i).getFeatureNames().stream().findFirst().map(FeatureName::getLanguage).orElse(null),
                    result.get(i).getFeatureNames().stream().findFirst().map(FeatureNameDto::getLanguage).orElse(null));
        }
    }

    /**
     * Test that we can retrieve the Aids to Navigation currently in the database
     * in a paged result.
     */
    @Test
    void testGetAidsToNavigation() throws Exception {
        // Created a result page to be returned by the mocked service
        Page<AidsToNavigation> page = new PageImpl<>(this.aidsToNavigationList.subList(0, 5), this.pageable, this.aidsToNavigationList.size());
        doReturn(page).when(this.aidsToNavigationService).findAll(any(), any(), any(), any(), any());

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(get("/api/atons"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        // Parse and validate the response
        Page<AidsToNavigationDto> result = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(page.getSize(), result.getContent().size());

        // Validate the entries one by one
        for(int i=0; i< page.getSize(); i++) {
            assertEquals(page.getContent().get(i).getId(), result.getContent().get(i).getId());
            assertEquals(page.getContent().get(i).getIdCode(), result.getContent().get(i).getIdCode());
            assertEquals(page.getContent().get(i).getInformations().size(), result.getContent().get(i).getInformations().size());
            assertEquals(page.getContent().get(i).getInformations().stream().findFirst().map(Information::getFileLocator).orElse(null),
                    result.getContent().get(i).getInformations().stream().findFirst().map(InformationDto::getFileLocator).orElse(null));
            assertEquals(page.getContent().get(i).getInformations().stream().findFirst().map(Information::getFileReference).orElse(null),
                    result.getContent().get(i).getInformations().stream().findFirst().map(InformationDto::getFileReference).orElse(null));
            assertEquals(page.getContent().get(i).getInformations().stream().findFirst().map(Information::getHeadline).orElse(null),
                    result.getContent().get(i).getInformations().stream().findFirst().map(InformationDto::getHeadline).orElse(null));
            assertEquals(page.getContent().get(i).getInformations().stream().findFirst().map(Information::getLanguage).orElse(null),
                    result.getContent().get(i).getInformations().stream().findFirst().map(InformationDto::getLanguage).orElse(null));
            assertEquals(page.getContent().get(i).getInformations().stream().findFirst().map(Information::getText).orElse(null),
                    result.getContent().get(i).getInformations().stream().findFirst().map(InformationDto::getText).orElse(null));
            assertEquals(page.getContent().get(i).getFeatureNames().size(), result.getContent().get(i).getFeatureNames().size());
            assertEquals(page.getContent().get(i).getFeatureNames().stream().findFirst().map(FeatureName::getName).orElse(null),
                    result.getContent().get(i).getFeatureNames().stream().findFirst().map(FeatureNameDto::getName).orElse(null));
            assertEquals(page.getContent().get(i).getFeatureNames().stream().findFirst().map(FeatureName::getDisplayName).orElse(null),
                    result.getContent().get(i).getFeatureNames().stream().findFirst().map(FeatureNameDto::getDisplayName).orElse(null));
            assertEquals(page.getContent().get(i).getFeatureNames().stream().findFirst().map(FeatureName::getLanguage).orElse(null),
                    result.getContent().get(i).getFeatureNames().stream().findFirst().map(FeatureNameDto::getLanguage).orElse(null));
        }
    }

    /**
     * Test that the API supports the jQuery Datatables server-side paging
     * and search requests.
     */
    @Test
    void testGetNodesForDatatables() throws Exception {
        // Create a test datatables paging request
        DtColumn dtColumn = new DtColumn("id");
        dtColumn.setName("ID");
        dtColumn.setOrderable(true);
        DtOrder dtOrder = new DtOrder();
        dtOrder.setColumn(0);
        dtOrder.setDir(DtDirection.asc);
        DtPagingRequest dtPagingRequest = new DtPagingRequest();
        dtPagingRequest.setStart(0);
        dtPagingRequest.setLength(this.aidsToNavigationList.size());
        dtPagingRequest.setDraw(1);
        dtPagingRequest.setSearch(new DtSearch());
        dtPagingRequest.setOrder(Collections.singletonList(dtOrder));
        dtPagingRequest.setColumns(Collections.singletonList(dtColumn));

        // Created a result page to be returned by the mocked service
        Page<AidsToNavigation> page = new PageImpl<>(this.aidsToNavigationList.subList(0, 5), this.pageable, this.aidsToNavigationList.size());
        doReturn(page).when(this.aidsToNavigationService).handleDatatablesPagingRequest(any());

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(post("/api/atons/dt")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(dtPagingRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Parse and validate the response
        DtPage<AidsToNavigationDto> result = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(page.getSize(), result.getData().size());

        // Validate the entries one by one
        for(int i=0; i< page.getSize(); i++) {
            assertEquals(page.getContent().get(i).getId(), result.getData().get(i).getId());
            assertEquals(page.getContent().get(i).getIdCode(), result.getData().get(i).getIdCode());
            assertEquals(page.getContent().get(i).getInformations().size(), result.getData().get(i).getInformations().size());
            assertEquals(page.getContent().get(i).getInformations().stream().findFirst().map(Information::getFileLocator).orElse(null),
                    result.getData().get(i).getInformations().stream().findFirst().map(InformationDto::getFileLocator).orElse(null));
            assertEquals(page.getContent().get(i).getInformations().stream().findFirst().map(Information::getFileReference).orElse(null),
                    result.getData().get(i).getInformations().stream().findFirst().map(InformationDto::getFileReference).orElse(null));
            assertEquals(page.getContent().get(i).getInformations().stream().findFirst().map(Information::getHeadline).orElse(null),
                    result.getData().get(i).getInformations().stream().findFirst().map(InformationDto::getHeadline).orElse(null));
            assertEquals(page.getContent().get(i).getInformations().stream().findFirst().map(Information::getLanguage).orElse(null),
                    result.getData().get(i).getInformations().stream().findFirst().map(InformationDto::getLanguage).orElse(null));
            assertEquals(page.getContent().get(i).getInformations().stream().findFirst().map(Information::getText).orElse(null),
                    result.getData().get(i).getInformations().stream().findFirst().map(InformationDto::getText).orElse(null));
            assertEquals(page.getContent().get(i).getFeatureNames().size(), result.getData().get(i).getFeatureNames().size());
            assertEquals(page.getContent().get(i).getFeatureNames().stream().findFirst().map(FeatureName::getName).orElse(null),
                    result.getData().get(i).getFeatureNames().stream().findFirst().map(FeatureNameDto::getName).orElse(null));
            assertEquals(page.getContent().get(i).getFeatureNames().stream().findFirst().map(FeatureName::getDisplayName).orElse(null),
                    result.getData().get(i).getFeatureNames().stream().findFirst().map(FeatureNameDto::getDisplayName).orElse(null));
            assertEquals(page.getContent().get(i).getFeatureNames().stream().findFirst().map(FeatureName::getLanguage).orElse(null),
                    result.getData().get(i).getFeatureNames().stream().findFirst().map(FeatureNameDto::getLanguage).orElse(null));
        }
    }

    /**
     * Test that we can correctly delete an existing Aids to Navigation by using
     * a valid ID.
     */
    @Test
    void testDeleteAidsToNavigation() throws Exception {
        doReturn(this.existingAidsToNavigation).when(this.aidsToNavigationService).delete(any());
        doReturn(Page.empty()).when(this.datasetService).findAll(any(), any(), any(), any(), any(), any());

        // Perform the MVC request
        this.mockMvc.perform(delete("/api/atons/{id}", this.existingAidsToNavigation.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
    }

    /**
     * Test that if we do NOT find the Aids to Navigation we are trying to
     * delete, an HTTP NOT_FOUND response will be returned.
     */
    @Test
    void testDeleteAidsToNavigationNotFound() throws Exception {
        doThrow(DataNotFoundException.class).when(this.aidsToNavigationService).delete(any());

        // Perform the MVC request
        this.mockMvc.perform(delete("/api/atons/{id}", this.existingAidsToNavigation.getId()))
                .andExpect(status().isNotFound());
    }

}