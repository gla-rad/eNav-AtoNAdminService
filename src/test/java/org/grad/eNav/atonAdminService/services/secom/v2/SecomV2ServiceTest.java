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

package org.grad.eNav.atonAdminService.services.secom.v2;

import org.grad.secomv2.core.exceptions.SecomValidationException;
import org.grad.secomv2.core.models.ResponseSearchObject;
import org.grad.secomv2.core.models.ServiceInstanceObject;
import org.grad.secomv2.springboot4.components.SecomClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SecomV2ServiceTest {

    /**
     * The Tested Service.
     */
    @InjectMocks
    @Spy
    SecomV2Service secomV2Service;

    /**
     * The Discovery Service mock.
     */
    @Mock
    SecomClient discoveryService;

    // Test Variables
    ResponseSearchObject responseSearchObject;
    List<ServiceInstanceObject> instances;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setUp() {
        // Set the discovery service URL variable
        this.secomV2Service.discoveryServiceUrl = "http://localhost:8444/v1/searchService";

        // Create a set of retrieved instances
        ServiceInstanceObject serviceInstanceObject1 = new ServiceInstanceObject();
        serviceInstanceObject1.setVersion("0.0.1");
        serviceInstanceObject1.setEndpointUri("http://localhost/");
        ServiceInstanceObject serviceInstanceObject2 = new ServiceInstanceObject();
        serviceInstanceObject2.setVersion("0.0.2");
        serviceInstanceObject2.setEndpointUri("http://localhost/");
        this.instances = Arrays.asList(serviceInstanceObject1, serviceInstanceObject2);

        // Create the response search object
        this.responseSearchObject = new ResponseSearchObject();
        this.responseSearchObject.setSearchServiceResult(this.instances);
    }

    /**
     * That that during its initialisation the SECOM service will construct the
     * SECOM discovery service client.
     */
    @Test
    void testInit() {
        // Perform the service call
        this.secomV2Service.init();

        // Make sure the discovery service was initialise properly
        assertNotNull(this.secomV2Service.discoveryService);
    }

    /**
     * That that during its termination the SECOM service will destroy the
     * SECOM discovery service client.
     */
    @Test
    void testDestroy() {
        // Perform the service call
        this.secomV2Service.init();
        this.secomV2Service.destroy();

        // Make sure the discovery service was destroyed properly
        assertNull(this.secomV2Service.discoveryService);
    }

    /**
     * Test that the SECOM service will contact the SECOM discovery service
     * allocated to it, to discover the requested clients based on their MRNs.
     */
    @Test
    void testGetClient() {
        // And mock a SECOM discovery service client
        this.secomV2Service.discoveryService = mock(SecomClient.class);
        doReturn(Optional.of(this.responseSearchObject)).when(this.secomV2Service.discoveryService).searchService(any(), any(), any());

        // Perform the service call
        SecomClient result = this.secomV2Service.getClient("urn:mrn:org:test");

        // Make sure the client seems OK
        assertNotNull(result);
    }

    /**
     * Test that the SECOM service can also create SECOM clients based on the
     * provided URL without contacting the SECOM discovery service. This is
     * the preferred way of operation for SECOM v2.0.
     */
    @Test
    void testGetClientByUrl() throws MalformedURLException {
        // Perform the service call
        SecomClient result = this.secomV2Service.getClient(URI.create("http://localhost").toURL());

        // Make sure the client seems OK
        assertNotNull(result);
    }


    /**
     * Test that the SECOM service will contact the SECOM discovery service
     * allocated to it, to discover the requested clients based on their MRNs.
     * If the discovered URL does not seem valid, a SecomValidationException
     * will be thrown.
     */
    @Test
    void testGetClientWithBrokenUrl() {
        // Break the URL of the latest instance
        this.responseSearchObject.getSearchServiceResult().get(1).setEndpointUri("a broken URL");

        // And mock a SECOM discovery service client
        this.secomV2Service.discoveryService = mock(SecomClient.class);
        doReturn(Optional.of(this.responseSearchObject)).when(this.secomV2Service.discoveryService).searchService(any(), any(), any());

        // Perform the service call
        assertThrows(SecomValidationException.class, () -> this.secomV2Service.getClient("urn:mrn:org:test"));
    }

}