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

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The S-201 Orientation Embeddable Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Orientation
 * type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.OrientationType
 */
@Embeddable
public class Orientation implements Serializable {

    // Class Variables
    private BigDecimal orientationUncertainty;

    private BigDecimal orientationValue;

    /**
     * Gets orientation uncertainty.
     *
     * @return the orientation uncertainty
     */
    public BigDecimal getOrientationUncertainty() {
        return orientationUncertainty;
    }

    /**
     * Sets orientation uncertainty.
     *
     * @param orientationUncertainty the orientation uncertainty
     */
    public void setOrientationUncertainty(BigDecimal orientationUncertainty) {
        this.orientationUncertainty = orientationUncertainty;
    }

    /**
     * Gets orientation value.
     *
     * @return the orientation value
     */
    public BigDecimal getOrientationValue() {
        return orientationValue;
    }

    /**
     * Sets orientation value.
     *
     * @param orientationValue the orientation value
     */
    public void setOrientationValue(BigDecimal orientationValue) {
        this.orientationValue = orientationValue;
    }
}
