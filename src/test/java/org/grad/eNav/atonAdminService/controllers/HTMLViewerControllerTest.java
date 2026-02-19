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

import org.grad.eNav.atonAdminService.TestFeignSecurityConfig;
import org.grad.eNav.atonAdminService.TestingConfiguration;
import org.grad.eNav.atonAdminService.models.domain.s100.ServiceInformationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableConfigurationProperties(value = ServiceInformationConfig.class)
@WebMvcTest(controllers = HTMLViewerController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Import({TestingConfiguration.class, TestFeignSecurityConfig.class})
class HTMLViewerControllerTest {

    /**
     * The Mock MVC.
     */
    @Autowired
    MockMvc mockMvc;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setUp() {
        // Create a temp geometry factory to get a test geometries
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
    }

    /**
     * Test that we can access the main index HTML page.
     */
    @Test
    void testGetIndex() throws Exception {
        // Perform the MVC request
        this.mockMvc.perform(get("/index")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
    }

    /**
     * Test that we can access the AtoNs HTML page.
     */
    @Test
    void testGetAtons() throws Exception {
        // Perform the MVC request
        this.mockMvc.perform(get("/atons")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
    }

    /**
     * Test that we can access the Subscriptions HTML page.
     */
    @Test
    void testGetSubscriptions() throws Exception {
        // Perform the MVC request
        this.mockMvc.perform(get("/subscriptions")
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
    }

    /**
     * Test that we can access the Logs HTML page.
     */
    @Test
    void testGetLogs() throws Exception {
        // Perform the MVC request
        this.mockMvc.perform(get("/logs")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
    }

    /**
     * Test that we can access the about HTML page.
     */
    @Test
    void testGetAbout() throws Exception {
        // Perform the MVC request
        this.mockMvc.perform(get("/about")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
    }

}