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

package org.grad.eNav.atonAdminService.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UnLoCodeServiceTest {

    /**
     * The Tested Service.
     */
    @InjectMocks
    @Spy
    private UnLoCodeService unLoCodeService;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setup() throws IOException {
        // Initialise the service
        this.unLoCodeService.objectMapper = new ObjectMapper();
        this.unLoCodeService.init();
    }

    /**
     * Test that the UnLoCodeService when loading it can read the UnLoCode
     * mapping from the resources.
     */
    @Test
    void testInit() {
        assertFalse(this.unLoCodeService.UnLoCodeMap.isEmpty());
    }

    /**
     * Test that we can indeed retrieve a UN/LOCODE entry from the service
     * if we provide the correct mapping key.
     */
    @Test
    void testGetUnLoCodeMapEntry() {
        assertNull(this.unLoCodeService.getUnLoCodeMapEntry("invalid"));
        assertNotNull(this.unLoCodeService.getUnLoCodeMapEntry("ADALV"));
    }

}