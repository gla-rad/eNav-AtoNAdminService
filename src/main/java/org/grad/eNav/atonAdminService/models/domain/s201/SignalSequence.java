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

import _int.iho.s_201.gml.cs0._2.SignalStatusType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The S-201 Fog Signal Sequence Embeddable Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Signal
 * Sequence type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.SignalSequenceType
 */
@Embeddable
public class SignalSequence implements Serializable {

    // Class Variables
    private BigDecimal signalDuration;

    @Enumerated(EnumType.STRING)
    private SignalStatusType signalStatus;

    /**
     * Gets signal duration.
     *
     * @return the signal duration
     */
    public BigDecimal getSignalDuration() {
        return signalDuration;
    }

    /**
     * Sets signal duration.
     *
     * @param signalDuration the signal duration
     */
    public void setSignalDuration(BigDecimal signalDuration) {
        this.signalDuration = signalDuration;
    }

    /**
     * Gets signal status.
     *
     * @return the signal status
     */
    public SignalStatusType getSignalStatus() {
        return signalStatus;
    }

    /**
     * Sets signal status.
     *
     * @param signalStatus the signal status
     */
    public void setSignalStatus(SignalStatusType signalStatus) {
        this.signalStatus = signalStatus;
    }


}
