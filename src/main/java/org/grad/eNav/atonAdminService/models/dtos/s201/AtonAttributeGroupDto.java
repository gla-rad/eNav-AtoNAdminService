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

package org.grad.eNav.atonAdminService.models.dtos.s201;

import java.util.Map;

/**
 * The S-201 Aids to Navigation Attribute Group DTO Entity Class.
 * <p>
 * A named set of attributes describing one facet of an Aid to Navigation, such
 * as the buoy itself, the light it carries, or one sector of that light. Which
 * groups an Aid to Navigation produces - and which attributes they carry -
 * depends entirely on its type, so the presentation layer only ever has to
 * list whatever it is given.
 * <p>
 * The attributes are kept in an ordered map so that the service, which knows
 * the S-201 semantics, also decides the order in which they read best.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class AtonAttributeGroupDto {

    // Class Variables
    private String title;

    private Map<String, String> attributes;

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets attributes.
     *
     * @return the attributes
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * Sets attributes.
     *
     * @param attributes the attributes
     */
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

}
