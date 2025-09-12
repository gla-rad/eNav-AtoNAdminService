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

/**
 * The S-201 Obscured Sector Embeddable Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Obscured
 * Sector ype.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.ObscuredSectorType
 */
@Embeddable
public class ObscuredSector implements Serializable {

    // Class Variables
    private SectorLimit sectorLimit;

    private SectorInformation sectorInformation;

    /**
     * Gets sector limit.
     *
     * @return the sector limit
     */
    public SectorLimit getSectorLimit() {
        return sectorLimit;
    }

    /**
     * Sets sector limit.
     *
     * @param sectorLimit the sector limit
     */
    public void setSectorLimit(SectorLimit sectorLimit) {
        this.sectorLimit = sectorLimit;
    }

    /**
     * Gets sector information.
     *
     * @return the sector information
     */
    public SectorInformation getSectorInformation() {
        return sectorInformation;
    }

    /**
     * Sets sector information.
     *
     * @param sectorInformation the sector information
     */
    public void setSectorInformation(SectorInformation sectorInformation) {
        this.sectorInformation = sectorInformation;
    }
}
