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

package org.grad.eNav.atonAdminService.models.dtos.datatables;

/**
 * The type Search.
 *
 * The Datatables Search Class definition.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class DtSearch {

    // Class Variables
    private String value;
    private String regexp;
    private Boolean includeCancelled;

    /**
     * Instantiates a new Search.
     */
    public DtSearch() {
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets value.
     *
     * @param value the value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets regexp.
     *
     * @return the regexp
     */
    public String getRegexp() {
        return regexp;
    }

    /**
     * Sets regexp.
     *
     * @param regexp the regexp
     */
    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    /**
     * Gets include cancelled.
     *
     * @return the include cancelled
     */
    public Boolean getIncludeCancelled() {
        return includeCancelled;
    }

    /**
     * Sets include cancelled.
     *
     * @param includeCancelled the include cancelled
     */
    public void setIncludeCancelled(Boolean includeCancelled) {
        this.includeCancelled = includeCancelled;
    }
}
