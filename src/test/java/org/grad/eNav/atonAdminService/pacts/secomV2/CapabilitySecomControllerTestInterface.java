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

package org.grad.eNav.atonAdminService.pacts.secomV2;

import au.com.dius.pact.provider.junitsupport.State;

import java.util.Map;

/**
 * The interface for testing the SECOM capability controller using the Pacts
 * consumer driver contracts.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public interface CapabilitySecomControllerTestInterface {

    /**
     * Test that the SECOM capability interface will return an appropriate
     * response on various queries.
     *
     * @param data the request data
     */
    @State("Test SECOM Capability Interface") // Method will be run before testing interactions that require "with-data" state
    default void testSecomCapabilitySuccess(Map<?,?> data) {
        System.out.println("Service now checking the capability interface with " + data);
    }

}
