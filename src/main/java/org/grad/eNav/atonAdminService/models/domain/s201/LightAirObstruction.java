/*
 * Copyright (c) 2025 GLA Research and Development Directorate
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

package org.grad.eNav.atonAdminService.models.domain.s201;

import _int.iho.s_201.gml.cs0._2.ExhibitionConditionOfLightType;
import _int.iho.s_201.gml.cs0._2.LightVisibilityType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.util.Set;

/**
 * The S-201 Light Air Obstruction Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Light Air
 * Obstruction type. It is modelled as an entity that extends the
 * {@link GenericLight} super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.LightAirObstruction
 */
@Entity
public class LightAirObstruction extends GenericLight {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private ExhibitionConditionOfLightType exhibitionConditionOfLight;

    /**
     * The Value of geographic range.
     */
    private BigDecimal valueOfGeographicRange;

    /**
     * The Value of luminous range.
     */
    private BigDecimal valueOfLuminousRange;

    /**
     * The Value of nominal range.
     */
    private BigDecimal valueOfNominalRange;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = LightVisibilityType.class)
    private Set<LightVisibilityType> lightVisibilities;

    private MultiplicityOfFeatures multiplicity;

    /**
     * Gets exhibition condition of light.
     *
     * @return the exhibition condition of light
     */
    public ExhibitionConditionOfLightType getExhibitionConditionOfLight() {
        return exhibitionConditionOfLight;
    }

    /**
     * Sets exhibition condition of light.
     *
     * @param exhibitionConditionOfLight the exhibition condition of light
     */
    public void setExhibitionConditionOfLight(ExhibitionConditionOfLightType exhibitionConditionOfLight) {
        this.exhibitionConditionOfLight = exhibitionConditionOfLight;
    }

    /**
     * Gets value of geographic range.
     *
     * @return the value of geographic range
     */
    public BigDecimal getValueOfGeographicRange() {
        return valueOfGeographicRange;
    }

    /**
     * Sets value of geographic range.
     *
     * @param valueOfGeographicRange the value of geographic range
     */
    public void setValueOfGeographicRange(BigDecimal valueOfGeographicRange) {
        this.valueOfGeographicRange = valueOfGeographicRange;
    }

    /**
     * Gets value of luminous range.
     *
     * @return the value of luminous range
     */
    public BigDecimal getValueOfLuminousRange() {
        return valueOfLuminousRange;
    }

    /**
     * Sets value of luminous range.
     *
     * @param valueOfLuminousRange the value of luminous range
     */
    public void setValueOfLuminousRange(BigDecimal valueOfLuminousRange) {
        this.valueOfLuminousRange = valueOfLuminousRange;
    }

    /**
     * Gets value of nominal range.
     *
     * @return the value of nominal range
     */
    public BigDecimal getValueOfNominalRange() {
        return valueOfNominalRange;
    }

    /**
     * Sets value of nominal range.
     *
     * @param valueOfNominalRange the value of nominal range
     */
    public void setValueOfNominalRange(BigDecimal valueOfNominalRange) {
        this.valueOfNominalRange = valueOfNominalRange;
    }

    /**
     * Gets light visibilities.
     *
     * @return the light visibilities
     */
    public Set<LightVisibilityType> getLightVisibilities() {
        return lightVisibilities;
    }

    /**
     * Sets light visibilities.
     *
     * @param lightVisibilities the light visibilities
     */
    public void setLightVisibilities(Set<LightVisibilityType> lightVisibilities) {
        this.lightVisibilities = lightVisibilities;
    }

    /**
     * Gets multiplicity.
     *
     * @return the multiplicity
     */
    public MultiplicityOfFeatures getMultiplicity() {
        return multiplicity;
    }

    /**
     * Sets multiplicity.
     *
     * @param multiplicity the multiplicity
     */
    public void setMultiplicity(MultiplicityOfFeatures multiplicity) {
        this.multiplicity = multiplicity;
    }
}
