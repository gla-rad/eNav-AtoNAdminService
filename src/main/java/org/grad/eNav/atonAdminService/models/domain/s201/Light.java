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

package org.grad.eNav.atonAdminService.models.domain.s201;

import _int.iho.s201.gml.cs0._1.*;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * The S-201 Light Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Light type.
 * It is modelled as an entity that extends the {@link Equipment} super
 * class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s201.gml.cs0._1.Light
 */
@Entity
public class Light extends Equipment {

    // Class Variables
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = CategoryOfLightType.class)
    private List<CategoryOfLightType> categoryOfLights;

    @Enumerated(EnumType.STRING)
    private ColourType colour;

    @Enumerated(EnumType.STRING)
    private ExhibitionConditionOfLightType exhibitionConditionOfLight;

    private BigDecimal height;

    @Enumerated(EnumType.STRING)
    private LightCharacteristicType lightCharacteristic;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = LightVisibilityType.class)
    private List<LightVisibilityType> lightVisibilities;

    @Enumerated(EnumType.STRING)
    private MarksNavigationalSystemOfType marksNavigationalSystemOf;

    private BigInteger multiplicityOfLights;

    private BigDecimal orientation;

    private BigDecimal sectorLimitOne;

    private BigDecimal sectorLimitTwo;

    private String signalGroup;

    private String signalPeriod;

    private String signalSequence;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private List<StatusType> statuses;

    private BigDecimal verticalAccuracy;

    private BigDecimal valueOfNominalRange;

    @Enumerated(EnumType.STRING)
    protected VerticalDatumType verticalDatum;

    private String typeOfLight;

    private BigDecimal valueOfGeographicalRange;

    private BigDecimal candela;

    private BigDecimal valueOfLuminousRange;

    /**
     * Gets category of lights.
     *
     * @return the category of lights
     */
    public List<CategoryOfLightType> getCategoryOfLights() {
        return categoryOfLights;
    }

    /**
     * Sets category of lights.
     *
     * @param categoryOfLights the category of lights
     */
    public void setCategoryOfLights(List<CategoryOfLightType> categoryOfLights) {
        this.categoryOfLights = categoryOfLights;
    }

    /**
     * Gets colour.
     *
     * @return the colour
     */
    public ColourType getColour() {
        return colour;
    }

    /**
     * Sets colour.
     *
     * @param colour the colour
     */
    public void setColour(ColourType colour) {
        this.colour = colour;
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
     * Gets height.
     *
     * @return the height
     */
    public BigDecimal getHeight() {
        return height;
    }

    /**
     * Sets height.
     *
     * @param height the height
     */
    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    /**
     * Gets light characteristic.
     *
     * @return the light characteristic
     */
    public LightCharacteristicType getLightCharacteristic() {
        return lightCharacteristic;
    }

    /**
     * Sets light characteristic.
     *
     * @param lightCharacteristic the light characteristic
     */
    public void setLightCharacteristic(LightCharacteristicType lightCharacteristic) {
        this.lightCharacteristic = lightCharacteristic;
    }

    /**
     * Gets light visibilities.
     *
     * @return the light visibilities
     */
    public List<LightVisibilityType> getLightVisibilities() {
        return lightVisibilities;
    }

    /**
     * Sets light visibilities.
     *
     * @param lightVisibilities the light visibilities
     */
    public void setLightVisibilities(List<LightVisibilityType> lightVisibilities) {
        this.lightVisibilities = lightVisibilities;
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
     * Gets multiplicity of lights.
     *
     * @return the multiplicity of lights
     */
    public BigInteger getMultiplicityOfLights() {
        return multiplicityOfLights;
    }

    /**
     * Sets multiplicity of lights.
     *
     * @param multiplicityOfLights the multiplicity of lights
     */
    public void setMultiplicityOfLights(BigInteger multiplicityOfLights) {
        this.multiplicityOfLights = multiplicityOfLights;
    }

    /**
     * Gets orientation.
     *
     * @return the orientation
     */
    public BigDecimal getOrientation() {
        return orientation;
    }

    /**
     * Sets orientation.
     *
     * @param orientation the orientation
     */
    public void setOrientation(BigDecimal orientation) {
        this.orientation = orientation;
    }

    /**
     * Gets sector limit one.
     *
     * @return the sector limit one
     */
    public BigDecimal getSectorLimitOne() {
        return sectorLimitOne;
    }

    /**
     * Sets sector limit one.
     *
     * @param sectorLimitOne the sector limit one
     */
    public void setSectorLimitOne(BigDecimal sectorLimitOne) {
        this.sectorLimitOne = sectorLimitOne;
    }

    /**
     * Gets sector limit two.
     *
     * @return the sector limit two
     */
    public BigDecimal getSectorLimitTwo() {
        return sectorLimitTwo;
    }

    /**
     * Sets sector limit two.
     *
     * @param sectorLimitTwo the sector limit two
     */
    public void setSectorLimitTwo(BigDecimal sectorLimitTwo) {
        this.sectorLimitTwo = sectorLimitTwo;
    }

    /**
     * Gets signal group.
     *
     * @return the signal group
     */
    public String getSignalGroup() {
        return signalGroup;
    }

    /**
     * Sets signal group.
     *
     * @param signalGroup the signal group
     */
    public void setSignalGroup(String signalGroup) {
        this.signalGroup = signalGroup;
    }

    /**
     * Gets signal period.
     *
     * @return the signal period
     */
    public String getSignalPeriod() {
        return signalPeriod;
    }

    /**
     * Sets signal period.
     *
     * @param signalPeriod the signal period
     */
    public void setSignalPeriod(String signalPeriod) {
        this.signalPeriod = signalPeriod;
    }

    /**
     * Gets signal sequence.
     *
     * @return the signal sequence
     */
    public String getSignalSequence() {
        return signalSequence;
    }

    /**
     * Sets signal sequence.
     *
     * @param signalSequence the signal sequence
     */
    public void setSignalSequence(String signalSequence) {
        this.signalSequence = signalSequence;
    }

    /**
     * Gets statuses.
     *
     * @return the statuses
     */
    public List<StatusType> getStatuses() {
        return statuses;
    }

    /**
     * Sets statuses.
     *
     * @param statuses the statuses
     */
    public void setStatuses(List<StatusType> statuses) {
        this.statuses = statuses;
    }

    /**
     * Gets vertical accuracy.
     *
     * @return the vertical accuracy
     */
    public BigDecimal getVerticalAccuracy() {
        return verticalAccuracy;
    }

    /**
     * Sets vertical accuracy.
     *
     * @param verticalAccuracy the vertical accuracy
     */
    public void setVerticalAccuracy(BigDecimal verticalAccuracy) {
        this.verticalAccuracy = verticalAccuracy;
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
     * Gets vertical datum.
     *
     * @return the vertical datum
     */
    public VerticalDatumType getVerticalDatum() {
        return verticalDatum;
    }

    /**
     * Sets vertical datum.
     *
     * @param verticalDatum the vertical datum
     */
    public void setVerticalDatum(VerticalDatumType verticalDatum) {
        this.verticalDatum = verticalDatum;
    }

    /**
     * Gets type of light.
     *
     * @return the type of light
     */
    public String getTypeOfLight() {
        return typeOfLight;
    }

    /**
     * Sets type of light.
     *
     * @param typeOfLight the type of light
     */
    public void setTypeOfLight(String typeOfLight) {
        this.typeOfLight = typeOfLight;
    }

    /**
     * Gets value of geographical range.
     *
     * @return the value of geographical range
     */
    public BigDecimal getValueOfGeographicalRange() {
        return valueOfGeographicalRange;
    }

    /**
     * Sets value of geographical range.
     *
     * @param valueOfGeographicalRange the value of geographical range
     */
    public void setValueOfGeographicalRange(BigDecimal valueOfGeographicalRange) {
        this.valueOfGeographicalRange = valueOfGeographicalRange;
    }

    /**
     * Gets candela.
     *
     * @return the candela
     */
    public BigDecimal getCandela() {
        return candela;
    }

    /**
     * Sets candela.
     *
     * @param candela the candela
     */
    public void setCandela(BigDecimal candela) {
        this.candela = candela;
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
}
