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

import _int.iho.s_201.gml.cs0._2.StatusType;
import _int.iho.s_201.gml.cs0._2.VerticalDatumType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import java.util.List;

/**
 * The S-201 Radar Reflector Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Radar Reflector
 * type. It is modelled as an entity that extends the {@link Equipment} super
 * class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.RadarReflector
 */
@Entity
public class RadarReflector extends Equipment {

    // Class Variables
    protected BigDecimal height;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private List<StatusType> statuses;

    @Enumerated(EnumType.STRING)
    protected VerticalDatumType verticalDatum;

    protected BigDecimal verticalAccuracy;

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
     * Gets vertical accuracy.
     *
     * @return the vertical accuracy
     */
    public BigDecimal getVerticalAccuracy() {
        return verticalAccuracy;
    }

    /**
     * Sets vertical accuracy.
     *
     * @param verticalAccuracy the vertical accuracy
     */
    public void setVerticalAccuracy(BigDecimal verticalAccuracy) {
        this.verticalAccuracy = verticalAccuracy;
    }
}
