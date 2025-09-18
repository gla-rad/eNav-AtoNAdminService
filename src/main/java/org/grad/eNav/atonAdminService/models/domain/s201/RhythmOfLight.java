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
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

/**
 * The S-125 Rhythm Of Light Embeddable Class.
 * <p>
 * This is the basic class for implementing the S-125-compatible Rhythm Of Light type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.RhythmOfLightType
 */
@Embeddable
public class RhythmOfLight implements Serializable {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private LightCharacteristicType lightCharacteristic;

    private Set<String> signalGroups;

    private BigDecimal signalPeriod;

    @ElementCollection
    private Set<SignalSequence> signalSequences;

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
}
