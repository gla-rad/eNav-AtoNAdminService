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

import au.com.dius.pact.provider.junitsupport.State;
import org.grad.eNav.atonAdminService.services.secom.SecomSubscriptionService;
import org.grad.secom.core.exceptions.SecomNotFoundException;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

/**
 * The interface for testing the SECOM remove subscription controller using the
 * Pacts consumer driver contracts.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public interface RemoveSubscriptionSecomControllerTestInterface {

    /**
     * Provides the mocked SECOM subscription service to the tests.
     *
     * @return the mocked SECOM subscription service
     */
    SecomSubscriptionService getSecomSubscriptionService();

    /**
     * Test that the SECOM remove subscription interface will return an
     * appropriate response on various queries.
     *
     * @param data the request data
     */
    @State("Test SECOM Remove Subscription Interface") // Method will be run before testing interactions that require "with-data" state
    default void testSecomRemoveSubscription(Map<?,?> data) {
        // Mock the service responses
        doReturn(UUID.randomUUID()).when(this.getSecomSubscriptionService()).delete(any());

        // And proceed with the testing
        System.out.println("Service now checking the remove subscription interface with " + data);
    }

    /**
     * Test that the SECOM remove subscription interface will return an error
     * if an invalid subscription identifier is provided.
     *
     * @param data the request data
     */
    @State("Test SECOM Remove Subscription Interface without subscriptions") // Method will be run before testing interactions that require "with-data" state
    default void testSecomRemoveSubscriptionForInvalidIdentifier(Map<?,?> data) {
        // Mock the service responses
        doThrow(SecomNotFoundException.class).when(this.getSecomSubscriptionService()).delete(any());

        // And proceed with the testing
        System.out.println("Service now checking the remove subscription interface with " + data);
    }

}
