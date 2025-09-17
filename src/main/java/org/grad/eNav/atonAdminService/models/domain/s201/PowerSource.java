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

import java.util.Set;

/**
 * The S-201 PowerSource Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible PowerSource type.
 * It is modelled as an entity that extends the {@link Equipment} super
 * class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.PowerSource
 */
@Entity
public class PowerSource extends Equipment {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private CategoryOfPowerSourceType categoryOfPowerSource;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private Set<StatusType> statuses;

    private String manufacturer;

    /**
     * Gets category of power source.
     *
     * @return the category of power source
     */
    public CategoryOfPowerSourceType getCategoryOfPowerSource() {
        return categoryOfPowerSource;
    }

    /**
     * Sets category of power source.
     *
     * @param categoryOfPowerSource the category of power source
     */
    public void setCategoryOfPowerSource(CategoryOfPowerSourceType categoryOfPowerSource) {
        this.categoryOfPowerSource = categoryOfPowerSource;
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
}
