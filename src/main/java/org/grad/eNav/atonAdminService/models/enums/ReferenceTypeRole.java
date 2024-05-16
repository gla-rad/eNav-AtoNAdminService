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
 * The Reference Type Role Enum.
 * <p/>
 * This enumeration encodes the applicable information for the S-201 dataset
 * reference types, including child and parent relationships, aggregations
 * and associations.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public enum ReferenceTypeRole {
    AGGREGATION("aggregation","urn:IALA:s201:roles:aggregation"),
    ASSOCIATION("association","urn:IALA:s201:roles:association"),
    CHILD("child","urn:IALA:s201:roles:child"),
    PARENT("parent","urn:IALA:s201:roles:parent");

    // Enum Variables
    private String role;
    private String archRole;

    /**
     * The Enum Constructor
     *
     * @param role          The reference type role value
     * @param archRole      The reference type arcRole value
     */
    ReferenceTypeRole(String role, String archRole) {
        this.role = role;
        this.archRole = archRole;
    }

    /**
     * Gets role.
     *
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * Gets arch role.
     *
     * @return the arch role
     */
    public String getArchRole() {
        return archRole;
    }

}
