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

import _int.iho.s201.gml.cs0._1.CategoryOfRadarTransponderBeaconType;
import _int.iho.s201.gml.cs0._1.StatusType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.util.List;

/**
 * The S-201 Radar Transponder Beacon Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Radar
 * Transponder Beacon type. It is modelled as an entity that extends the
 * {@link Equipment} super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s201.gml.cs0._1.RadarTransponderBeacon
 */
@Entity
public class RadarTransponderBeacon extends Equipment {

    // Class Variables
    @Enumerated(EnumType.STRING)
    protected CategoryOfRadarTransponderBeaconType categoryOfRadarTransponderBeaconType;

    protected String radarWaveLength;

    protected BigDecimal sectorLimitOne;

    protected BigDecimal sectorLimitTwo;

    protected String signalGroup;

    protected String signalSequence;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private List<StatusType> statuses;

    /**
     * The Value of nominal range.
     */
    protected BigDecimal valueOfNominalRange;

    /**
     * Gets category of radar transponder beacon type.
     *
     * @return the category of radar transponder beacon type
     */
    public CategoryOfRadarTransponderBeaconType getCategoryOfRadarTransponderBeaconType() {
        return categoryOfRadarTransponderBeaconType;
    }

    /**
     * Sets category of radar transponder beacon type.
     *
     * @param categoryOfRadarTransponderBeaconType the category of radar transponder beacon type
     */
    public void setCategoryOfRadarTransponderBeaconType(CategoryOfRadarTransponderBeaconType categoryOfRadarTransponderBeaconType) {
        this.categoryOfRadarTransponderBeaconType = categoryOfRadarTransponderBeaconType;
    }

    /**
     * Gets radar wave length.
     *
     * @return the radar wave length
     */
    public String getRadarWaveLength() {
        return radarWaveLength;
    }

    /**
     * Sets radar wave length.
     *
     * @param radarWaveLength the radar wave length
     */
    public void setRadarWaveLength(String radarWaveLength) {
        this.radarWaveLength = radarWaveLength;
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
}
