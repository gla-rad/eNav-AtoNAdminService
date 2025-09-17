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
import java.math.BigInteger;
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
    @ElementCollection(targetClass = ExhibitionConditionOfLightType.class)
    private Set<ExhibitionConditionOfLightType> exhibitionConditionOfLights;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = LightVisibilityType.class)
    private Set<LightVisibilityType> lightVisibilities;

    private BigDecimal valueOfNominalRange;

    private MultiplicityOfFeatures multiplicityOfFeatures;

    private RhythmOfLight rhythmOfLight;

    private BigInteger flareBearing;

    /**
     * Gets exhibition condition of lights.
     *
     * @return the exhibition condition of lights
     */
    public Set<ExhibitionConditionOfLightType> getExhibitionConditionOfLights() {
        return exhibitionConditionOfLights;
    }

    /**
     * Sets exhibition condition of lights.
     *
     * @param exhibitionConditionOfLights the exhibition condition of lights
     */
    public void setExhibitionConditionOfLights(Set<ExhibitionConditionOfLightType> exhibitionConditionOfLights) {
        this.exhibitionConditionOfLights = exhibitionConditionOfLights;
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
     * Gets multiplicity of features.
     *
     * @return the multiplicity of features
     */
    public MultiplicityOfFeatures getMultiplicityOfFeatures() {
        return multiplicityOfFeatures;
    }

    /**
     * Sets multiplicity of features.
     *
     * @param multiplicityOfFeatures the multiplicity of features
     */
    public void setMultiplicityOfFeatures(MultiplicityOfFeatures multiplicityOfFeatures) {
        this.multiplicityOfFeatures = multiplicityOfFeatures;
    }

    /**
     * Gets rhythm of light.
     *
     * @return the rhythm of light
     */
    public RhythmOfLight getRhythmOfLight() {
        return rhythmOfLight;
    }

    /**
     * Sets rhythm of light.
     *
     * @param rhythmOfLight the rhythm of light
     */
    public void setRhythmOfLight(RhythmOfLight rhythmOfLight) {
        this.rhythmOfLight = rhythmOfLight;
    }

    /**
     * Gets flare bearing.
     *
     * @return the flare bearing
     */
    public BigInteger getFlareBearing() {
        return flareBearing;
    }

    /**
     * Sets flare bearing.
     *
     * @param flareBearing the flare bearing
     */
    public void setFlareBearing(BigInteger flareBearing) {
        this.flareBearing = flareBearing;
    }
}
