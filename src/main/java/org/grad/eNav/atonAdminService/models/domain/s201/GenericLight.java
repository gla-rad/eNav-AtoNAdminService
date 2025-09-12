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
 * The S-201 Generic Light Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Light type.
 * It is modelled as an entity class on hibernate, but it is abstract so
 * that we can extend this for each Light type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.GenericLightType
 */
@Entity
public abstract class GenericLight extends Equipment {

    // Class Variables
    private BigDecimal height;

    @Enumerated(EnumType.STRING)
    private VerticalDatumType verticalDatum;

    private BigDecimal verticalLength;

    private BigDecimal effectiveIntensity;

    private BigDecimal peakIntensity;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ColourType.class)
    private Set<ColourType> colours;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private Set<StatusType> statuses;

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
     * Gets effective intensity.
     *
     * @return the effective intensity
     */
    public BigDecimal getEffectiveIntensity() {
        return effectiveIntensity;
    }

    /**
     * Sets effective intensity.
     *
     * @param effectiveIntensity the effective intensity
     */
    public void setEffectiveIntensity(BigDecimal effectiveIntensity) {
        this.effectiveIntensity = effectiveIntensity;
    }

    /**
     * Gets peak intensity.
     *
     * @return the peak intensity
     */
    public BigDecimal getPeakIntensity() {
        return peakIntensity;
    }

    /**
     * Sets peak intensity.
     *
     * @param peakIntensity the peak intensity
     */
    public void setPeakIntensity(BigDecimal peakIntensity) {
        this.peakIntensity = peakIntensity;
    }

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
}
