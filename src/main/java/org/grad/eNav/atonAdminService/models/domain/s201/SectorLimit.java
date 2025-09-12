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

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * The S-201 Sector Limit Embeddable Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Sector
 * Limit type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.SectorLimitType
 */
@Embeddable
public class SectorLimit implements Serializable {

    // Class Variable
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "sectorBearing",
                    column = @Column(name = "sector_limit_one_bearing")
            ),
            @AttributeOverride(
                    name = "sectorLineLength",
                    column = @Column(name = "sector_limit_one_line_length")
            )
    })
    private SectorLimitDetails sectorLimitOne;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "sectorBearing",
                    column = @Column(name = "sector_limit_two_bearing")
            ),
            @AttributeOverride(
                    name = "sectorLineLength",
                    column = @Column(name = "sector_limit_two_line_length")
            )
    })
    private SectorLimitDetails sectorLimitTwo;

    /**
     * Gets sector limit one.
     *
     * @return the sector limit one
     */
    public SectorLimitDetails getSectorLimitOne() {
        return sectorLimitOne;
    }

    /**
     * Sets sector limit one.
     *
     * @param sectorLimitOne the sector limit one
     */
    public void setSectorLimitOne(SectorLimitDetails sectorLimitOne) {
        this.sectorLimitOne = sectorLimitOne;
    }

    /**
     * Gets sector limit two.
     *
     * @return the sector limit two
     */
    public SectorLimitDetails getSectorLimitTwo() {
        return sectorLimitTwo;
    }

    /**
     * Sets sector limit two.
     *
     * @param sectorLimitTwo the sector limit two
     */
    public void setSectorLimitTwo(SectorLimitDetails sectorLimitTwo) {
        this.sectorLimitTwo = sectorLimitTwo;
    }
}
