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

import java.math.BigDecimal;

/**
 * The S-201 Vertical Uncertainty Embeddable Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Vertical
 * Uncertainty type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.VerticalUncertaintyType
 */
@Embeddable
public class VerticalUncertainty {

    // Class Variables
    private BigDecimal uncertaintyFixed;

    private BigDecimal uncertaintyVariableFactor;

    /**
     * Gets uncertainty fixed.
     *
     * @return the uncertainty fixed
     */
    public BigDecimal getUncertaintyFixed() {
        return uncertaintyFixed;
    }

    /**
     * Sets uncertainty fixed.
     *
     * @param uncertaintyFixed the uncertainty fixed
     */
    public void setUncertaintyFixed(BigDecimal uncertaintyFixed) {
        this.uncertaintyFixed = uncertaintyFixed;
    }

    /**
     * Gets uncertainty variable factor.
     *
     * @return the uncertainty variable factor
     */
    public BigDecimal getUncertaintyVariableFactor() {
        return uncertaintyVariableFactor;
    }

    /**
     * Sets uncertainty variable factor.
     *
     * @param uncertaintyVariableFactor the uncertainty variable factor
     */
    public void setUncertaintyVariableFactor(BigDecimal uncertaintyVariableFactor) {
        this.uncertaintyVariableFactor = uncertaintyVariableFactor;
    }
}
