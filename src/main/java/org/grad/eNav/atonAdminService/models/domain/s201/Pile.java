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

import _int.iho.s201.gml.cs0._1.CategoryOfPileType;
import _int.iho.s201.gml.cs0._1.ColourPatternType;
import _int.iho.s201.gml.cs0._1.ColourType;
import _int.iho.s201.gml.cs0._1.VisualProminenceType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import java.util.List;

/**
 * The S-201 Pile Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Pile type.
 * It is modelled as an entity that extends the {@link StructureObject} super
 * class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s201.gml.cs0._1.Pile
 */
@Entity
public class Pile extends StructureObject {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private CategoryOfPileType categoryOfPile;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ColourType.class)
    private List<ColourType> colours;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ColourPatternType.class)
    private List<ColourPatternType> colourPatterns;

    @Enumerated(EnumType.STRING)
    private VisualProminenceType visuallyConspicuous;

    private BigDecimal height;

    /**
     * Gets category of pile.
     *
     * @return the category of pile
     */
    public CategoryOfPileType getCategoryOfPile() {
        return categoryOfPile;
    }

    /**
     * Sets category of pile.
     *
     * @param categoryOfPile the category of pile
     */
    public void setCategoryOfPile(CategoryOfPileType categoryOfPile) {
        this.categoryOfPile = categoryOfPile;
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
     * Gets visually conspicuous.
     *
     * @return the visually conspicuous
     */
    public VisualProminenceType getVisuallyConspicuous() {
        return visuallyConspicuous;
    }

    /**
     * Sets visually conspicuous.
     *
     * @param visuallyConspicuous the visually conspicuous
     */
    public void setVisuallyConspicuous(VisualProminenceType visuallyConspicuous) {
        this.visuallyConspicuous = visuallyConspicuous;
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
}
