/*
 * Copyright (c) 2024 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grad.eNav.atonAdminService.models.domain.s201;

import _int.iho.s_201.gml.cs0._2.StatusType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.util.Set;

/**
 * The S-201 Environment Observation Equipment Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Environment
 * Observation Equipment type. It is modelled as an entity that extends the
 * {@link Equipment} super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.EnvironmentObservationEquipment
 */
public class EnvironmentObservationEquipment extends Equipment {

    // Class Variables
    private BigDecimal height;

    @ElementCollection
    private Set<String> typeOfEnvironmentObservationEquipments;

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
     * Gets type of environment observation equipments.
     *
     * @return the type of environment observation equipments
     */
    public Set<String> getTypeOfEnvironmentObservationEquipments() {
        return typeOfEnvironmentObservationEquipments;
    }

    /**
     * Sets type of environment observation equipments.
     *
     * @param typeOfEnvironmentObservationEquipments the type of environment observation equipments
     */
    public void setTypeOfEnvironmentObservationEquipments(Set<String> typeOfEnvironmentObservationEquipments) {
        this.typeOfEnvironmentObservationEquipments = typeOfEnvironmentObservationEquipments;
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
