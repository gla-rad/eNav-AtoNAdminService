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

import _int.iho.s_201.gml.cs0._2.CategoryOfFogSignalType;
import _int.iho.s_201.gml.cs0._2.SignalGenerationType;
import _int.iho.s_201.gml.cs0._2.StatusType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

/**
 * The S-201 Fog Signal Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Fog Signal
 * type. It is modelled as an entity that extends the {@link Equipment} super
 * class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.FogSignal
 */
@Entity
public class FogSignal extends Equipment {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private CategoryOfFogSignalType categoryOfFogSignal;

    private BigInteger signalFrequency;

    private SignalGenerationType signalGeneration;

    private String signalGroup;

    private BigDecimal signalPeriod;

    private SignalSequence signalSequence;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private Set<StatusType> statuses;

    private BigDecimal valueOfMaximumRange;

    private Double signalOutput;

    /**
     * Gets category of fog signal.
     *
     * @return the category of fog signal
     */
    public CategoryOfFogSignalType getCategoryOfFogSignal() {
        return categoryOfFogSignal;
    }

    /**
     * Sets category of fog signal.
     *
     * @param categoryOfFogSignal the category of fog signal
     */
    public void setCategoryOfFogSignal(CategoryOfFogSignalType categoryOfFogSignal) {
        this.categoryOfFogSignal = categoryOfFogSignal;
    }

    /**
     * Gets signal frequency.
     *
     * @return the signal frequency
     */
    public BigInteger getSignalFrequency() {
        return signalFrequency;
    }

    /**
     * Sets signal frequency.
     *
     * @param signalFrequency the signal frequency
     */
    public void setSignalFrequency(BigInteger signalFrequency) {
        this.signalFrequency = signalFrequency;
    }

    /**
     * Gets signal generation.
     *
     * @return the signal generation
     */
    public SignalGenerationType getSignalGeneration() {
        return signalGeneration;
    }

    /**
     * Sets signal generation.
     *
     * @param signalGeneration the signal generation
     */
    public void setSignalGeneration(SignalGenerationType signalGeneration) {
        this.signalGeneration = signalGeneration;
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

    /**
     * Gets value of maximum range.
     *
     * @return the value of maximum range
     */
    public BigDecimal getValueOfMaximumRange() {
        return valueOfMaximumRange;
    }

    /**
     * Sets value of maximum range.
     *
     * @param valueOfMaximumRange the value of maximum range
     */
    public void setValueOfMaximumRange(BigDecimal valueOfMaximumRange) {
        this.valueOfMaximumRange = valueOfMaximumRange;
    }

    /**
     * Gets signal output.
     *
     * @return the signal output
     */
    public Double getSignalOutput() {
        return signalOutput;
    }

    /**
     * Sets signal output.
     *
     * @param signalOutput the signal output
     */
    public void setSignalOutput(Double signalOutput) {
        this.signalOutput = signalOutput;
    }
}
