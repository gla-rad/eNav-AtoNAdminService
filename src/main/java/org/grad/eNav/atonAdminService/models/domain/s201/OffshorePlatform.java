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
import java.util.Set;

/**
 * The S-201 Offshore Platform Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Offshore
 * Platform type. It is modelled as an entity that extends the
 * {@link StructureObject} super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.OffshorePlatform
 */
@Entity
public class OffshorePlatform extends StructureObject {

    // Class Variables
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = CategoryOfOffshorePlatformType.class)
    private Set<CategoryOfOffshorePlatformType> categoryOfOffshorePlatforms;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ColourType.class)
    private Set<ColourType> colours;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ColourPatternType.class)
    private Set<ColourPatternType> colourPatterns;

    private BigDecimal height;

    private Boolean mannedStructure;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = NatureOfConstructionType.class)
    private Set<NatureOfConstructionType> natureOfConstructions;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = NatureOfConstructionType.class)
    private Set<ProductType> products;

    private Boolean radarConspicuous;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private Set<StatusType> statuses;

    @Enumerated(EnumType.STRING)
    private VerticalDatumType verticalDatum;

    private BigDecimal verticalLength;

    @Enumerated(EnumType.STRING)
    private VisualProminenceType visualProminence;

    private BigDecimal verticalAccuracy;

    /**
     * Gets category of offshore platforms.
     *
     * @return the category of offshore platforms
     */
    public Set<CategoryOfOffshorePlatformType> getCategoryOfOffshorePlatforms() {
        return categoryOfOffshorePlatforms;
    }

    /**
     * Sets category of offshore platforms.
     *
     * @param categoryOfOffshorePlatforms the category of offshore platforms
     */
    public void setCategoryOfOffshorePlatforms(Set<CategoryOfOffshorePlatformType> categoryOfOffshorePlatforms) {
        this.categoryOfOffshorePlatforms = categoryOfOffshorePlatforms;
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
     * Gets products.
     *
     * @return the products
     */
    public Set<ProductType> getProducts() {
        return products;
    }

    /**
     * Sets products.
     *
     * @param products the products
     */
    public void setProducts(Set<ProductType> products) {
        this.products = products;
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
}
