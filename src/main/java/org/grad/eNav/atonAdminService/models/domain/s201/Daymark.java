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
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Set;

/**
 * The S-201 Daymark Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Daymark type.
 * It is modelled as an entity that extends the {@link Equipment} super
 * class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.Daymark
 */
@Entity
public class Daymark extends Equipment {

    // Class Variables
    private CategoryOfSpecialPurposeMarkType categoryOfSpecialPurposeMark;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ColourType.class)
    private Set<ColourType> colours;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ColourPatternType.class)
    private Set<ColourPatternType> colourPatterns;

    private BigDecimal elevation;

    private BigDecimal height;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = NatureOfConstructionType.class)
    private Set<NatureOfConstructionType> natureOfConstructions;

    private BigDecimal orientationValue;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private Set<StatusType> statuses;

    private String topmarkDaymarkShape;

    /**
     * The Vertical datum.
     */
    @Enumerated(EnumType.STRING)
    private VerticalDatumType verticalDatum;

    private BigDecimal verticalLength;

    private ShapeInformation shapeInformation;

    private boolean isSlatted;

    /**
     * Gets category of special purpose mark.
     *
     * @return the category of special purpose mark
     */
    public CategoryOfSpecialPurposeMarkType getCategoryOfSpecialPurposeMark() {
        return categoryOfSpecialPurposeMark;
    }

    /**
     * Sets category of special purpose mark.
     *
     * @param categoryOfSpecialPurposeMark the category of special purpose mark
     */
    public void setCategoryOfSpecialPurposeMark(CategoryOfSpecialPurposeMarkType categoryOfSpecialPurposeMark) {
        this.categoryOfSpecialPurposeMark = categoryOfSpecialPurposeMark;
    }

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
     * Gets colour patterns.
     *
     * @return the colour patterns
     */
    public Set<ColourPatternType> getColourPatterns() {
        return colourPatterns;
    }

    /**
     * Sets colour patterns.
     *
     * @param colourPatterns the colour patterns
     */
    public void setColourPatterns(Set<ColourPatternType> colourPatterns) {
        this.colourPatterns = colourPatterns;
    }

    /**
     * Gets elevation.
     *
     * @return the elevation
     */
    public BigDecimal getElevation() {
        return elevation;
    }

    /**
     * Sets elevation.
     *
     * @param elevation the elevation
     */
    public void setElevation(BigDecimal elevation) {
        this.elevation = elevation;
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
     * Gets nature of constructions.
     *
     * @return the nature of constructions
     */
    public Set<NatureOfConstructionType> getNatureOfConstructions() {
        return natureOfConstructions;
    }

    /**
     * Sets nature of constructions.
     *
     * @param natureOfConstructions the nature of constructions
     */
    public void setNatureOfConstructions(Set<NatureOfConstructionType> natureOfConstructions) {
        this.natureOfConstructions = natureOfConstructions;
    }

    /**
     * Gets orientation value.
     *
     * @return the orientation value
     */
    public BigDecimal getOrientationValue() {
        return orientationValue;
    }

    /**
     * Sets orientation value.
     *
     * @param orientationValue the orientation value
     */
    public void setOrientationValue(BigDecimal orientationValue) {
        this.orientationValue = orientationValue;
    }

    /**
     * Gets statuses.
     *
     * @return the statuses
     */
    public Set<StatusType> getStatuses() {
        return statuses;
    }

    /**
     * Sets statuses.
     *
     * @param statuses the statuses
     */
    public void setStatuses(Set<StatusType> statuses) {
        this.statuses = statuses;
    }

    /**
     * Gets topmark daymark shape.
     *
     * @return the topmark daymark shape
     */
    public String getTopmarkDaymarkShape() {
        return topmarkDaymarkShape;
    }

    /**
     * Sets topmark daymark shape.
     *
     * @param topmarkDaymarkShape the topmark daymark shape
     */
    public void setTopmarkDaymarkShape(String topmarkDaymarkShape) {
        this.topmarkDaymarkShape = topmarkDaymarkShape;
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
     * Gets shape information.
     *
     * @return the shape information
     */
    public ShapeInformation getShapeInformation() {
        return shapeInformation;
    }

    /**
     * Sets shape information.
     *
     * @param shapeInformation the shape information
     */
    public void setShapeInformation(ShapeInformation shapeInformation) {
        this.shapeInformation = shapeInformation;
    }

    /**
     * Is slatted boolean.
     *
     * @return the boolean
     */
    public boolean isSlatted() {
        return isSlatted;
    }

    /**
     * Sets slatted.
     *
     * @param slatted the slatted
     */
    public void setSlatted(boolean slatted) {
        isSlatted = slatted;
    }
}
