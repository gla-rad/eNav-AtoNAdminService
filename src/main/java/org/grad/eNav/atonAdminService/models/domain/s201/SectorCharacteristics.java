/*
 * Copyright (c) 2025 GLA Research and Development Directorate
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

import _int.iho.s_201.gml.cs0._2.LightCharacteristicType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * The S-201 Sector Characteristics Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Sector
 * Characteristics type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.SectorCharacteristicsType
 */
@Entity
public class SectorCharacteristics implements Serializable {

    // Class Variables
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sector_characteristics_generator")
    @SequenceGenerator(name="sector_characteristics_generator", sequenceName = "sector_characteristics_seq", allocationSize=1)
    private BigInteger id;

    @Enumerated(EnumType.STRING)
    private LightCharacteristicType lightCharacteristic;

    private LightSector lightSector;

    private Set<String> signalGroups;

    private BigDecimal signalPeriod;

    @ElementCollection
    private Set<SignalSequence> signalSequences;

    @JsonBackReference
    @ManyToOne
    private LightSectored sectorOf;

    /**
     * Gets id.
     *
     * @return the id
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(BigInteger id) {
        this.id = id;
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
     * Gets light sector.
     *
     * @return the light sector
     */
    public LightSector getLightSector() {
        return lightSector;
    }

    /**
     * Gets the light sector into a list.
     * <p/>
     * This function is primarity used in the mapping since the S201
     * light sector entries are modelled into a list.
     *
     * @return the light sectors list
     */
    public List<LightSector> getLightSectors() {
        return Collections.singletonList(this.lightSector);
    }

    /**
     * Sets light sector.
     *
     * @param lightSector the light sector
     */
    public void setLightSector(LightSector lightSector) {
        this.lightSector = lightSector;
    }

    /**
     * Gets signal groups.
     *
     * @return the signal groups
     */
    public Set<String> getSignalGroups() {
        return signalGroups;
    }

    /**
     * Sets signal groups.
     *
     * @param signalGroups the signal groups
     */
    public void setSignalGroups(Set<String> signalGroups) {
        this.signalGroups = signalGroups;
    }

    /**
     * Gets signal period.
     *
     * @return the signal period
     */
    public BigDecimal getSignalPeriod() {
        return signalPeriod;
    }

    /**
     * Sets signal period.
     *
     * @param signalPeriod the signal period
     */
    public void setSignalPeriod(BigDecimal signalPeriod) {
        this.signalPeriod = signalPeriod;
    }

    /**
     * Gets signal sequences.
     *
     * @return the signal sequences
     */
    public Set<SignalSequence> getSignalSequences() {
        return signalSequences;
    }

    /**
     * Sets signal sequences.
     *
     * @param signalSequences the signal sequences
     */
    public void setSignalSequences(Set<SignalSequence> signalSequences) {
        this.signalSequences = signalSequences;
    }

    /**
     * Gets sector of.
     *
     * @return the sector of
     */
    public LightSectored getSectorOf() {
        return sectorOf;
    }

    /**
     * Sets sector of.
     *
     * @param sectorOf the sector of
     */
    public void setSectorOf(LightSectored sectorOf) {
        this.sectorOf = sectorOf;
    }
}
