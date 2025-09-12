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
import java.util.List;

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
    private List<CategoryOfOffshorePlatformType> categoryOfOffshorePlatforms;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ColourType.class)
    private List<ColourType> colours;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ColourPatternType.class)
    private List<ColourPatternType> colourPatterns;

    @Enumerated(EnumType.STRING)
    private ConditionType condition;

    private Boolean radarConspicuous;

    private VisualProminenceType visualProminence;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = NatureOfConstructionType.class)
    private List<NatureOfConstructionType> natureOfConstructions;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private List<StatusType> statuses;

    /**
     * Gets category of offshore platforms.
     *
     * @return the category of offshore platforms
     */
    public List<CategoryOfOffshorePlatformType> getCategoryOfOffshorePlatforms() {
        return categoryOfOffshorePlatforms;
    }

    /**
     * Sets category of offshore platforms.
     *
     * @param categoryOfOffshorePlatforms the category of offshore platforms
     */
    public void setCategoryOfOffshorePlatforms(List<CategoryOfOffshorePlatformType> categoryOfOffshorePlatforms) {
        this.categoryOfOffshorePlatforms = categoryOfOffshorePlatforms;
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
     * Gets condition.
     *
     * @return the condition
     */
    public ConditionType getCondition() {
        return condition;
    }

    /**
     * Sets condition.
     *
     * @param condition the condition
     */
    public void setCondition(ConditionType condition) {
        this.condition = condition;
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
