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

package org.grad.eNav.atonAdminService.components;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PubSubErrorHandlerTest {

    /**
     * The Tested Component.
     */
    @InjectMocks
    @Spy
    PubSubErrorHandler pubSubErrorHandler;

    /**
     * Test that our PubSubErrorHandler can accept new errors to be handled.
     */
    @Test
    void testHandleErrors() {
        // Create a random exception
        Throwable error = new Exception("This is a random exception");

        // Perform the component call
        pubSubErrorHandler.handleError(error);

        // Make sure it's in
        assertEquals(1, pubSubErrorHandler.getHandlerErrors().size());
    }

}