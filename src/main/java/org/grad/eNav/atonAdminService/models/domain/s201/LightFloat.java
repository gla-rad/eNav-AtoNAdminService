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

import _int.iho.s_201.gml.cs0._2.*;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.util.List;

/**
 * The S-201 Light Float Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Light Float
 * type. It is modelled as an entity that extends the {@link AidsToNavigation}
 * super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.LightFloat
 */
@Entity
public class LightFloat extends StructureObject {

    // Class Variables
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ColourType.class)
    private List<ColourType> colours;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ColourPatternType.class)
    private List<ColourPatternType> colourPatterns;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = NatureOfConstructionType.class)
    private List<NatureOfConstructionType> natureOfConstructions;

    private Boolean radarConspicuous;

    @Enumerated(EnumType.STRING)
    private VisualProminenceType visualProminence;

    private BigDecimal horizontalAccuracy;

    private BigDecimal horizontalLength;

    private BigDecimal horizontalWidth;

    private BigDecimal verticalAccuracy;

    private BigDecimal verticalLength;

    private Boolean mannedStructure;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    protected List<StatusType> statuses;

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
     * Gets nature of constructions.
     *
     * @return the nature of constructions
     */
    public List<NatureOfConstructionType> getNatureOfConstructions() {
        return natureOfConstructions;
    }

    /**
     * Sets nature of constructions.
     *
     * @param natureOfConstructions the nature of constructions
     */
    public void setNatureOfConstructions(List<NatureOfConstructionType> natureOfConstructions) {
        this.natureOfConstructions = natureOfConstructions;
    }

    /**
     * Gets radar conspicuous.
     *
     * @return the radar conspicuous
     */
    public Boolean getRadarConspicuous() {
        return radarConspicuous;
    }

    /**
     * Sets radar conspicuous.
     *
     * @param radarConspicuous the radar conspicuous
     */
    public void setRadarConspicuous(Boolean radarConspicuous) {
        this.radarConspicuous = radarConspicuous;
    }

    /**
     * Gets visual prominence.
     *
     * @return the visual prominence
     */
    public VisualProminenceType getVisualProminence() {
        return visualProminence;
    }

    /**
     * Sets visual prominence.
     *
     * @param visualProminence the visual prominence
     */
    public void setVisualProminence(VisualProminenceType visualProminence) {
        this.visualProminence = visualProminence;
    }

    /**
     * Gets horizontal accuracy.
     *
     * @return the horizontal accuracy
     */
    public BigDecimal getHorizontalAccuracy() {
        return horizontalAccuracy;
    }

    /**
     * Sets horizontal accuracy.
     *
     * @param horizontalAccuracy the horizontal accuracy
     */
    public void setHorizontalAccuracy(BigDecimal horizontalAccuracy) {
        this.horizontalAccuracy = horizontalAccuracy;
    }

    /**
     * Gets horizontal length.
     *
     * @return the horizontal length
     */
    public BigDecimal getHorizontalLength() {
        return horizontalLength;
    }

    /**
     * Sets horizontal length.
     *
     * @param horizontalLength the horizontal length
     */
    public void setHorizontalLength(BigDecimal horizontalLength) {
        this.horizontalLength = horizontalLength;
    }

    /**
     * Gets horizontal width.
     *
     * @return the horizontal width
     */
    public BigDecimal getHorizontalWidth() {
        return horizontalWidth;
    }

    /**
     * Sets horizontal width.
     *
     * @param horizontalWidth the horizontal width
     */
    public void setHorizontalWidth(BigDecimal horizontalWidth) {
        this.horizontalWidth = horizontalWidth;
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
     * Gets manned structure.
     *
     * @return the manned structure
     */
    public Boolean getMannedStructure() {
        return mannedStructure;
    }

    /**
     * Sets manned structure.
     *
     * @param mannedStructure the manned structure
     */
    public void setMannedStructure(Boolean mannedStructure) {
        this.mannedStructure = mannedStructure;
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
}
