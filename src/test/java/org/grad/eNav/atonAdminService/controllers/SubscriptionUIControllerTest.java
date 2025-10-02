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
import org.grad.eNav.atonAdminService.models.domain.secom.SubscriptionRequest;
import org.grad.eNav.atonAdminService.models.dtos.datatables.*;
import org.grad.eNav.atonAdminService.models.dtos.secom.SubscriptionRequestDto;
import org.grad.eNav.atonAdminService.services.secom.v2.SecomV2SubscriptionService;
import org.grad.eNav.atonAdminService.utils.SecomUtils;
import org.grad.secom.core.models.enums.ContainerTypeEnum;
import org.grad.secom.core.models.enums.SECOM_DataProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SubscriptionUIController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Import({TestingConfiguration.class, TestFeignSecurityConfig.class})
class SubscriptionUIControllerTest {

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
    SecomV2SubscriptionService secomV2SubscriptionService;

    // Test Variables
    private List<SubscriptionRequest> subscriptionList;
    private Pageable pageable;
    private GeometryFactory factory;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setUp() {
        // Create a temp geometry factory to get a test geometries
        this.factory = new GeometryFactory(new PrecisionModel(), 4326);

        // Initialise the subscriptions nodes list
        this.subscriptionList = new ArrayList<>();
        for(long i=0; i<10; i++) {
            SubscriptionRequest subscription = new SubscriptionRequest();
            subscription.setUuid(UUID.randomUUID());
            subscription.setContainerType(SecomUtils.translateSecomContainerTypeEnum(ContainerTypeEnum.S100_DataSet));
            subscription.setDataProductType(SecomUtils.translateSecomDataProductTypeEnum(SECOM_DataProductType.S201));
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
            this.subscriptionList.add(subscription);
        }

        // Create a pageable definition
        this.pageable = PageRequest.of(0, 5);
    }

    /**
     * Test that the API supports the jQuery Datatables server-side paging
     * and search requests.
     */
    @Test
    void testGetSubscriptionsForDatatables() throws Exception {
        // Create a test datatables paging request
        DtColumn dtColumn = new DtColumn("id");
        dtColumn.setName("ID");
        dtColumn.setOrderable(true);
        DtOrder dtOrder = new DtOrder();
        dtOrder.setColumn(0);
        dtOrder.setDir(DtDirection.asc);
        DtPagingRequest dtPagingRequest = new DtPagingRequest();
        dtPagingRequest.setStart(0);
        dtPagingRequest.setLength(this.subscriptionList.size());
        dtPagingRequest.setDraw(1);
        dtPagingRequest.setSearch(new DtSearch());
        dtPagingRequest.setOrder(Collections.singletonList(dtOrder));
        dtPagingRequest.setColumns(Collections.singletonList(dtColumn));

        // Created a result page to be returned by the mocked service
        Page<SubscriptionRequest> page = new PageImpl<>(this.subscriptionList.subList(0, 5), this.pageable, this.subscriptionList.size());
        doReturn(page).when(this.secomV2SubscriptionService).handleDatatablesPagingRequest(any());

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(post("/api/subscriptions/dt")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(dtPagingRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Parse and validate the response
        DtPage<SubscriptionRequestDto> result = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(page.getSize(), result.getData().size());

        // Validate the entries one by one
        for(int i=0; i< page.getSize(); i++) {
            assertNotNull(result.getData().get(i));
            assertEquals(page.getContent().get(i).getUuid(), result.getData().get(i).getUuid());
            assertEquals(page.getContent().get(i).getContainerType(), result.getData().get(i).getContainerType());
            assertEquals(page.getContent().get(i).getDataProductType(), result.getData().get(i).getDataProductType());
            assertEquals(page.getContent().get(i).getProductVersion(), result.getData().get(i).getProductVersion());
            assertEquals(page.getContent().get(i).getDataReference(), result.getData().get(i).getDataReference());
            assertEquals(page.getContent().get(i).getGeometry(), result.getData().get(i).getGeometry());
            assertEquals(page.getContent().get(i).getUnlocode(), result.getData().get(i).getUnlocode());
            assertEquals(page.getContent().get(i).getSubscriptionPeriodStart(), result.getData().get(i).getSubscriptionPeriodStart());
            assertEquals(page.getContent().get(i).getSubscriptionPeriodEnd(), result.getData().get(i).getSubscriptionPeriodEnd());
            assertEquals(page.getContent().get(i).getCreatedAt(), result.getData().get(i).getCreatedAt());
            assertEquals(page.getContent().get(i).getUpdatedAt(), result.getData().get(i).getUpdatedAt());
            assertEquals(page.getContent().get(i).getSubscriptionGeometry(), result.getData().get(i).getSubscriptionGeometry());
            assertEquals(page.getContent().get(i).getClientMrn(), result.getData().get(i).getClientMrn());
        }
    }
}