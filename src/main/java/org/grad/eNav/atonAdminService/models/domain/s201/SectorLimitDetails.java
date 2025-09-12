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
import java.math.BigInteger;

/**
 * The S-201 Sector Limit Details Embeddable Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Sector
 * Limit Details type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.SectorLimitOneType
 * @see _int.iho.s_201.gml.cs0._2.SectorLimitTwoType
 */
@Embeddable
public class SectorLimitDetails implements Serializable {

    // Class Variables
    private BigDecimal sectorBearing;

    private BigInteger sectorLineLength;

    /**
     * Gets sector bearing.
     *
     * @return the sector bearing
     */
    public BigDecimal getSectorBearing() {
        return sectorBearing;
    }

    /**
     * Sets sector bearing.
     *
     * @param sectorBearing the sector bearing
     */
    public void setSectorBearing(BigDecimal sectorBearing) {
        this.sectorBearing = sectorBearing;
    }

    /**
     * Gets sector line length.
     *
     * @return the sector line length
     */
    public BigInteger getSectorLineLength() {
        return sectorLineLength;
    }

    /**
     * Sets sector line length.
     *
     * @param sectorLineLength the sector line length
     */
    public void setSectorLineLength(BigInteger sectorLineLength) {
        this.sectorLineLength = sectorLineLength;
    }
}
