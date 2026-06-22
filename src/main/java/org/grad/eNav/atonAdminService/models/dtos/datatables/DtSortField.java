/*
 * Copyright (c) 2026 GLA Research and Development Directorate
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

package org.grad.eNav.atonAdminService.models.dtos.datatables;

import java.util.Objects;

/**
 * The Datatables Sort Field definition.
 * <p>
 * A backend-agnostic description of a single search sort field, holding the
 * name of the indexed field to sort on and the sorting direction. This is used
 * to build the Hibernate Search portable sort DSL, replacing the previous
 * Lucene-specific sort fields.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public final class DtSortField {

    // Class Variables
    private final String fieldName;
    private final boolean descending;

    /**
     * Instantiates a new Dt sort field.
     *
     * @param fieldName  the name of the indexed field to sort on
     * @param descending whether the sorting should be performed in a descending order
     */
    public DtSortField(String fieldName, boolean descending) {
        this.fieldName = fieldName;
        this.descending = descending;
    }

    /**
     * Field name string.
     *
     * @return the string
     */
    public String fieldName() {
        return fieldName;
    }

    /**
     * Descending boolean.
     *
     * @return the boolean
     */
    public boolean descending() {
        return descending;
    }

    /**
     * Overrides the equals() method to provide a standardised equality
     * operation to compare two DtSortField objects.
     *
     * @param obj the object to check for equality with
     * @return whether the two objects are equal or not
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DtSortField) obj;
        return Objects.equals(this.fieldName, that.fieldName) &&
                this.descending == that.descending;
    }

    /**
     * Overrides the hashCode() method to provide a standardised
     * hash-code for the specific DtSortField object.
     *
     * @return the integer hashcode of the specific DtSortField object
     */
    @Override
    public int hashCode() {
        return Objects.hash(fieldName, descending);
    }

    /**
     * Overrides the toString() method to provide a standardised
     * string format representation of the DtSortField object.
     *
     * @return the string representation of the DtSortField object
     */
    @Override
    public String toString() {
        return "DtSortField[" +
                "fieldName=" + fieldName + ", " +
                "descending=" + descending + ']';
    }


}
