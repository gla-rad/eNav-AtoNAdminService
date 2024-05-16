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

package org.grad.eNav.atonAdminService.pacts;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import org.grad.eNav.atonAdminService.TestFeignSecurityConfig;
import org.grad.eNav.atonAdminService.TestingConfiguration;
import org.grad.eNav.atonAdminService.feign.CKeeperClient;
import org.grad.eNav.atonAdminService.services.DatasetService;
import org.grad.eNav.atonAdminService.services.UnLoCodeService;
import org.grad.eNav.atonAdminService.services.secom.SecomSubscriptionService;
import org.grad.secom.core.components.SecomSignatureFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * The SecomProviderContractTest Class.
 * <p/>
 * The main class for performing the Pact contract testing. This class
 * initiates the Springboot testing environment (without security) and
 * also instantiates the pact verification context. Afterwards, the SECOM
 * controller testing interface classes will run the individual tests
 * using the states available.
 * <p/>
 * These tests will check against the latest consumer pact contract published
 * for the SecomService, i.e. generic SECOM-compliant services that do not
 * specialise in any S-100 data.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
@Import({TestingConfiguration.class, TestFeignSecurityConfig.class})
@IgnoreNoPactsToVerify
@PactBroker
@Provider("SecomService")
public class BasicSecomProviderContractTest implements
        CapabilitySecomControllerTestInterface,
        GetSummarySecomControllerTestInterface,
        GetSecomControllerTestInterface,
        AcknowledgementSecomControllerTestInterface,
        SubscriptionSecomControllerTestInterface,
        RemoveSubscriptionSecomControllerTestInterface
{
    /**
     * The port the test service is running on.
     */
    @LocalServerPort
    private int serverPort;

    /**
     * The SECOM Signature Filter mock.
     * <p/>
     * This will basically disable the certificate and signature checking on
     * SECOM requests and will allow testing pacts, without security on.
     */
    @MockBean
    SecomSignatureFilter secomSignatureFilter;

    /**
     * A geometry factory to facilitate testing.
     */
    private GeometryFactory geometryFactory;

    /**
     * The CKeeper Client mock.
     */
    @MockBean
    CKeeperClient cKeeperClient;

    /**
     * The Dataset Service mock.
     */
    @MockBean
    DatasetService datasetService;

    /**
     * The UnLoCodeService Service mock.
     */
    @MockBean
    UnLoCodeService unLoCodeService;

    /**
     * The SecomSubscriptionService Service mock.
     */
    @MockBean
    SecomSubscriptionService secomSubscriptionService;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setup() {
        this.geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    }

    /**
     * Establish the appropriate target to run the pact testing on.
     *
     * @param context the pact testing verification context
     */
    @BeforeEach
    void setupTestTarget(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", this.serverPort, "/api/secom"));
    }

    /**
     * This is the main test template for the pact testing. It basically uses
     * the current verification context to run each interaction specified in the
     * consumer contract individually.
     *
     * @param context the pact testing verification context
     */
    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    /**
     * Implements the method for returning the geometry factory.
     *
     * @return the geometry factory
     */
    @Override
    public GeometryFactory getGeometryFactory() {
        return this.geometryFactory;
    }

    /**
     * Provides the mocked cKeeper client to the tests.
     *
     * @return the mocked cKeeper client
     */
    @Override
    public CKeeperClient getCKeeperClient() {
        return this.cKeeperClient;
    }

    /**
     * Implements the method for returning the mocked cKeeper client.
     *
     * @return the mocked cKeeper client
     */
    @Override
    public DatasetService getDatasetService() {
        return this.datasetService;
    }

    /**
     * Implements the method for returning the mocked UnLoCode service.
     *
     * @return the mocked UnLoCode service
     */
    @Override
    public UnLoCodeService getUnLoCodeService() {
        return this.unLoCodeService;
    }

    /**
     * Implements the method for returning the mocked SecomSubscription service.
     *
     * @return the mocked SecomSubscriptionService service
     */
    @Override
    public SecomSubscriptionService getSecomSubscriptionService() {
        return this.secomSubscriptionService;
    }

}
