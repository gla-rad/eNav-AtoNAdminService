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

package org.grad.eNav.atonAdminService.models.enums;

/**
 * The enum Dataset operation.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public enum DatasetOperation {
    /**
     * Created dataset operation.
     */
    CREATED("CREATED", false),
    /**
     * Updated dataset operation.
     */
    UPDATED("UPDATED", false),
    /**
     * Cancelled dataset operation.
     */
    CANCELLED("CANCELLED", true),
    /**
     * Deleted dataset operation.
     */
    DELETED("DELETED", true),
    /**
     * Other dataset operation.
     */
    OTHER("OTHER", false),
    /**
     * Automatic dataset operation.
     * <p/>
     * In this case, the system will choose between CREATED/UPDATED.
     */
    AUTO("", false);

    // Enum Variables
    final private String operation;
    final private boolean withdrawal;

    /**
     * The Dataset Operation Enum Constructor.
     *
     * @param operation the operation name
     */
    DatasetOperation(String operation, boolean withdrawal) {
        this.operation = operation;
        this.withdrawal = withdrawal;
    }

    /**
     * Gets operation.
     *
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Is withdrawal boolean.
     *
     * @return the boolean
     */
    public boolean isWithdrawal() {
        return withdrawal;
    }
}
