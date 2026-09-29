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

import java.math.BigInteger;
import java.util.List;

/**
 * The S-201 Aids to Navigation Details DTO Entity Class.
 * <p>
 * The type specific description of a single Aid to Navigation, as shown in the
 * popup of the chart view. The {@link AidsToNavigationMapEntryDto} only
 * carries what every Aid to Navigation has in common, since it is requested
 * for the whole dataset in one go, while this one is looked up for the single
 * feature the user actually clicked on.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see org.grad.eNav.atonAdminService.utils.AtonAttributeUtils
 */
public class AidsToNavigationDetailsDto {

    // Class Variables
    private BigInteger id;

    private String idCode;

    private String atonType;

    private List<AtonAttributeGroupDto> attributeGroups;

    /**
     * Gets id.
     *
     * @return the id
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(BigInteger id) {
        this.id = id;
    }

    /**
     * Gets id code.
     *
     * @return the id code
     */
    public String getIdCode() {
        return idCode;
    }

    /**
     * Sets id code.
     *
     * @param idCode the id code
     */
    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    /**
     * Gets aton type.
     *
     * @return the aton type
     */
    public String getAtonType() {
        return atonType;
    }

    /**
     * Sets aton type.
     *
     * @param atonType the aton type
     */
    public void setAtonType(String atonType) {
        this.atonType = atonType;
    }

    /**
     * Gets attribute groups.
     *
     * @return the attribute groups
     */
    public List<AtonAttributeGroupDto> getAttributeGroups() {
        return attributeGroups;
    }

    /**
     * Sets attribute groups.
     *
     * @param attributeGroups the attribute groups
     */
    public void setAttributeGroups(List<AtonAttributeGroupDto> attributeGroups) {
        this.attributeGroups = attributeGroups;
    }

}
