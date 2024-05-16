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

package org.grad.eNav.atonAdminService.models.dtos;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * The MCP Entity Type Enum.
 *
 * Communication with cKeeper required that we have some knowledge of the
 * types of entities it supports.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public enum McpEntityType {
    DEVICE("device"),
    SERVICE("service"),
    USER("user"),
    VESSEL("vessel"),
    ROLE("role");

    // Enum Variables
    private final String value;

    /**
     * Enum Constructor
     *
     * @param value the enum value
     */
    McpEntityType(final String value) {
        this.value = value;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    @JsonValue
    public String getValue() { return value; }

    /**
     * Find the enum entry that corresponds to the provided value.
     *
     * @param value the enum value
     * @return The respective enum entry
     */
    public static McpEntityType fromValue(String value) {
        return Arrays.stream(McpEntityType.values())
                .filter(t -> t.getValue().compareTo(value)==0)
                .findFirst()
                .orElse(null);
    }

}
