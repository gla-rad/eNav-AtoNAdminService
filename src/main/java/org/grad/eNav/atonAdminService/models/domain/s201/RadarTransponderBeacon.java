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

import _int.iho.s_201.gml.cs0._2.CategoryOfRadarTransponderBeaconType;
import _int.iho.s_201.gml.cs0._2.StatusType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Set;

/**
 * The S-201 Radar Transponder Beacon Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Radar
 * Transponder Beacon type. It is modelled as an entity that extends the
 * {@link Equipment} super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.RadarTransponderBeacon
 */
@Entity
public class RadarTransponderBeacon extends Equipment {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private CategoryOfRadarTransponderBeaconType categoryOfRadarTransponderBeaconType;

    private RadarWaveLength radarWaveLength;

    private String signalGroup;

    private BigDecimal valueOfNominalRange;

    private String manufacturer;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "sectorBearing",
                    column = @Column(name = "racon_sector_limit_one_bearing")
            ),
            @AttributeOverride(
                    name = "sectorLineLength",
                    column = @Column(name = "racon_sector_limit_one_line_length")
            )
    })
    private SectorLimitDetails sectorLimitOne;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "sectorBearing",
                    column = @Column(name = "racon_sector_limit_two_bearing")
            ),
            @AttributeOverride(
                    name = "sectorLineLength",
                    column = @Column(name = "racon_sector_limit_two_line_length")
            )
    })
    private SectorLimitDetails sectorLimitTwo;

    private SignalSequence signalSequence;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private Set<StatusType> statuses;

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
    public RadarWaveLength getRadarWaveLength() {
        return radarWaveLength;
    }

    /**
     * Sets radar wave length.
     *
     * @param radarWaveLength the radar wave length
     */
    public void setRadarWaveLength(RadarWaveLength radarWaveLength) {
        this.radarWaveLength = radarWaveLength;
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
     * Gets manufacturer.
     *
     * @return the manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Sets manufacturer.
     *
     * @param manufacturer the manufacturer
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * Gets sector limit one.
     *
     * @return the sector limit one
     */
    public SectorLimitDetails getSectorLimitOne() {
        return sectorLimitOne;
    }

    /**
     * Sets sector limit one.
     *
     * @param sectorLimitOne the sector limit one
     */
    public void setSectorLimitOne(SectorLimitDetails sectorLimitOne) {
        this.sectorLimitOne = sectorLimitOne;
    }

    /**
     * Gets sector limit two.
     *
     * @return the sector limit two
     */
    public SectorLimitDetails getSectorLimitTwo() {
        return sectorLimitTwo;
    }

    /**
     * Sets sector limit two.
     *
     * @param sectorLimitTwo the sector limit two
     */
    public void setSectorLimitTwo(SectorLimitDetails sectorLimitTwo) {
        this.sectorLimitTwo = sectorLimitTwo;
    }

    /**
     * Gets signal sequence.
     *
     * @return the signal sequence
     */
    public SignalSequence getSignalSequence() {
        return signalSequence;
    }

    /**
     * Sets signal sequence.
     *
     * @param signalSequence the signal sequence
     */
    public void setSignalSequence(SignalSequence signalSequence) {
        this.signalSequence = signalSequence;
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
