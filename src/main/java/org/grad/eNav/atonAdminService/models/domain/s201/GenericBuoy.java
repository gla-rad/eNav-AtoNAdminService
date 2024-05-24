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
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * The S-201 Generic Buoy Entity Class.
 *
 * This is the basic class for implementing the S-201-compatible Generic Buoy
 * type. It is modelled as an entity class on hibernate, but it is abstract so
 * that we can extend this for each Buoy type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s201.gml.cs0._1.GenericBuoyType
 */
@Entity
public abstract class GenericBuoy extends StructureObject {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private BuoyShapeType buoyShape;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ColourType.class)
    private List<ColourType> colours;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ColourPatternType.class)
    private List<ColourPatternType> colourPatterns;

    @Enumerated(EnumType.STRING)
    private RadarConspicuousType radarConspicuous;

    @Enumerated(EnumType.STRING)
    private MarksNavigationalSystemOfType marksNavigationalSystemOf;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = NatureOfConstructionType.class)
    private List<NatureOfConstructionType> natureOfconstuctions;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private List<StatusType> statuses;

    private BigDecimal verticalLength;

    private BigDecimal verticalAccuracy;

    private String typeOfBuoy;

    /**
     * Gets buoy shape.
     *
     * @return the buoy shape
     */
    public BuoyShapeType getBuoyShape() {
        return buoyShape;
    }

    /**
     * Sets buoy shape.
     *
     * @param buoyShape the buoy shape
     */
    public void setBuoyShape(BuoyShapeType buoyShape) {
        this.buoyShape = buoyShape;
    }

    /**
     * Gets colours.
     *
     * @return the colours
     */
    public List<ColourType> getColours() {
        return colours;
    }

    /**
     * Sets colours.
     *
     * @param colours the colours
     */
    public void setColours(List<ColourType> colours) {
        this.colours = colours;
    }

    /**
     * Gets colour patterns.
     *
     * @return the colour patterns
     */
    public List<ColourPatternType> getColourPatterns() {
        return colourPatterns;
    }

    /**
     * Sets colour patterns.
     *
     * @param colourPatterns the colour patterns
     */
    public void setColourPatterns(List<ColourPatternType> colourPatterns) {
        this.colourPatterns = colourPatterns;
    }

    /**
     * Gets radar conspicuous.
     *
     * @return the radar conspicuous
     */
    public RadarConspicuousType getRadarConspicuous() {
        return radarConspicuous;
    }

    /**
     * Sets radar conspicuous.
     *
     * @param radarConspicuous the radar conspicuous
     */
    public void setRadarConspicuous(RadarConspicuousType radarConspicuous) {
        this.radarConspicuous = radarConspicuous;
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
     * Gets nature ofconstuctions.
     *
     * @return the nature ofconstuctions
     */
    public List<NatureOfConstructionType> getNatureOfconstuctions() {
        return natureOfconstuctions;
    }

    /**
     * Sets nature ofconstuctions.
     *
     * @param natureOfconstuctions the nature ofconstuctions
     */
    public void setNatureOfconstuctions(List<NatureOfConstructionType> natureOfconstuctions) {
        this.natureOfconstuctions = natureOfconstuctions;
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
     * Gets vertical length.
     *
     * @return the vertical length
     */
    public BigDecimal getVerticalLength() {
        return verticalLength;
    }

    /**
     * Sets vertical length.
     *
     * @param verticalLength the vertical length
     */
    public void setVerticalLength(BigDecimal verticalLength) {
        this.verticalLength = verticalLength;
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
     * Gets type of buoy.
     *
     * @return the type of buoy
     */
    public String getTypeOfBuoy() {
        return typeOfBuoy;
    }

    /**
     * Sets type of buoy.
     *
     * @param typeOfBuoy the type of buoy
     */
    public void setTypeOfBuoy(String typeOfBuoy) {
        this.typeOfBuoy = typeOfBuoy;
    }
}
