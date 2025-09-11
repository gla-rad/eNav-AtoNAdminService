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

package org.grad.eNav.atonAdminService.pacts.secomV1;

import au.com.dius.pact.provider.junitsupport.State;
import org.grad.eNav.atonAdminService.models.domain.secom.SubscriptionRequest;
import org.grad.eNav.atonAdminService.services.secom.SecomSubscriptionService;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

/**
 * The interface for testing the SECOM subscription controller using the Pacts
 * consumer driver contracts.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public interface SubscriptionSecomControllerTestInterface {

    /**
     * Provides the mocked SECOM subscription service to the tests.
     *
     * @return the mocked SECOM subscription service
     */
    SecomSubscriptionService getSecomSubscriptionService();

    /**
     * Test that the SECOM subscription interface will return an appropriate
     * response on various queries.
     *
     * @param data the request data
     */
    @State("Test SECOM Subscription Interface") // Method will be run before testing interactions that require "with-data" state
    default void testSecomSubscription(Map<?,?> data) {
        // Create a subscription request object to be returned
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setUuid(UUID.randomUUID());

        // Mock the service responses
        doReturn(subscriptionRequest).when(this.getSecomSubscriptionService()).save(any(), any());

        // And proceed with the testing
        System.out.println("Service now checking the subscription interface with " + data);
    }

}
