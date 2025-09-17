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

import _int.iho.s_201.gml.cs0._2.*;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

/**
 * The S-201 Light All Around Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Light All
 * Around type. It is modelled as an entity that extends the
 * {@link GenericLight} super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.LightAllAround
 */
@Entity
public class LightAllAround extends GenericLight {

    // Class Variables
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ColourType.class)
    private Set<ColourType> colours;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = CategoryOfLightType.class)
    private Set<CategoryOfLightType> categoryOfLights;

    @Enumerated(EnumType.STRING)
    private ExhibitionConditionOfLightType exhibitionConditionOfLight;

    @Enumerated(EnumType.STRING)
    private LightVisibilityType lightVisibility;

    private Boolean majorLight;

    @Enumerated(EnumType.STRING)
    private MarksNavigationalSystemOfType marksNavigationalSystemOf;

    @Enumerated(EnumType.STRING)
    private SignalGenerationType signalGeneration;

    private BigDecimal valueOfGeographicRange;

    private BigDecimal valueOfLuminousRange;

    private BigDecimal valueOfNominalRange;

    private MultiplicityOfFeatures multiplicityOfFeatures;

    private RhythmOfLight rhythmOfLight;

    private BigInteger flareBearing;

    /**
     * Gets colours.
     *
     * @return the colours
     */
    public Set<ColourType> getColours() {
        return colours;
    }

    /**
     * Sets colours.
     *
     * @param colours the colours
     */
    public void setColours(Set<ColourType> colours) {
        this.colours = colours;
    }

    /**
     * Gets category of lights.
     *
     * @return the category of lights
     */
    public Set<CategoryOfLightType> getCategoryOfLights() {
        return categoryOfLights;
    }

    /**
     * Sets category of lights.
     *
     * @param categoryOfLights the category of lights
     */
    public void setCategoryOfLights(Set<CategoryOfLightType> categoryOfLights) {
        this.categoryOfLights = categoryOfLights;
    }

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
     * Gets light visibility.
     *
     * @return the light visibility
     */
    public LightVisibilityType getLightVisibility() {
        return lightVisibility;
    }

    /**
     * Sets light visibility.
     *
     * @param lightVisibility the light visibility
     */
    public void setLightVisibility(LightVisibilityType lightVisibility) {
        this.lightVisibility = lightVisibility;
    }

    /**
     * Gets major light.
     *
     * @return the major light
     */
    public Boolean getMajorLight() {
        return majorLight;
    }

    /**
     * Sets major light.
     *
     * @param majorLight the major light
     */
    public void setMajorLight(Boolean majorLight) {
        this.majorLight = majorLight;
    }

    /**
     * Gets marks navigational system of.
     *
     * @return the marks navigational system of
     */
    public MarksNavigationalSystemOfType getMarksNavigationalSystemOf() {
        return marksNavigationalSystemOf;
    }

    /**
     * Sets marks navigational system of.
     *
     * @param marksNavigationalSystemOf the marks navigational system of
     */
    public void setMarksNavigationalSystemOf(MarksNavigationalSystemOfType marksNavigationalSystemOf) {
        this.marksNavigationalSystemOf = marksNavigationalSystemOf;
    }

    /**
     * Gets signal generation.
     *
     * @return the signal generation
     */
    public SignalGenerationType getSignalGeneration() {
        return signalGeneration;
    }

    /**
     * Sets signal generation.
     *
     * @param signalGeneration the signal generation
     */
    public void setSignalGeneration(SignalGenerationType signalGeneration) {
        this.signalGeneration = signalGeneration;
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
