/*
 * Copyright (c) 2024 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grad.eNav.atonAdminService.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LocalDateTimeConverterTest {

    // Test Variables
    LocalDateTimeConverter localDateTimeConverter;
    LocalDateTime localDateTime;
    ZonedDateTime zonedDateTime;
    Instant instant;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setUp() {
        this.localDateTimeConverter = new LocalDateTimeConverter();
        this.localDateTime = LocalDateTime.of(2024,8,8,1,0,0);
        this.zonedDateTime = ZonedDateTime.of(this.localDateTime, ZoneId.systemDefault());
        this.instant = zonedDateTime.toInstant();
    }

    /**
     *  Test that we can correctly convert a UTC-based timezoned database column
     *  value to a local date-time java object.
     */
    @Test
    void testConvertToDatabaseColumn() {
        assertEquals(this.zonedDateTime, this.localDateTimeConverter.convertToDatabaseColumn(this.localDateTime));
    }

    /**
     * Test that we can correctly convert a local date-time java object to a
     * UTC-based timezoned database column value.
     */
    @Test
    void testConvertToEntityAttribute() {
        assertEquals(this.localDateTime, this.localDateTimeConverter.convertToEntityAttribute(this.zonedDateTime));
    }

}