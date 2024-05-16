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

import _int.iho.s201.gml.cs0._1.StatusType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;

/**
 * The S-201 Physical AIS Aids to Navigation Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Physical AIS
 * Aids to Navigation type. It is modelled as an entity that extends the
 * {@link AISAidToNavigation} super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s201.gml.cs0._1.PhysicalAISAidToNavigation
 */
@Entity
public class PhysicalAISAidToNavigation extends AISAidToNavigation {

    // Class Variables
    private String objectNameInNationalLanguage;

    private String objectName;

    @Enumerated(EnumType.STRING)
    private StatusType status;

    private BigDecimal estimatedRangeOfTransmission;

    /**
     * The Mmsi code.
     */
    protected BigDecimal mmsiCode;

    /**
     * Gets object name in national language.
     *
     * @return the object name in national language
     */
    public String getObjectNameInNationalLanguage() {
        return objectNameInNationalLanguage;
    }

    /**
     * Sets object name in national language.
     *
     * @param objectNameInNationalLanguage the object name in national language
     */
    public void setObjectNameInNationalLanguage(String objectNameInNationalLanguage) {
        this.objectNameInNationalLanguage = objectNameInNationalLanguage;
    }

    /**
     * Gets object name.
     *
     * @return the object name
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * Sets object name.
     *
     * @param objectName the object name
     */
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public StatusType getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(StatusType status) {
        this.status = status;
    }

    /**
     * Gets estimated range of transmission.
     *
     * @return the estimated range of transmission
     */
    public BigDecimal getEstimatedRangeOfTransmission() {
        return estimatedRangeOfTransmission;
    }

    /**
     * Sets estimated range of transmission.
     *
     * @param estimatedRangeOfTransmission the estimated range of transmission
     */
    public void setEstimatedRangeOfTransmission(BigDecimal estimatedRangeOfTransmission) {
        this.estimatedRangeOfTransmission = estimatedRangeOfTransmission;
    }

    /**
     * Gets mmsi code.
     *
     * @return the mmsi code
     */
    public BigDecimal getMmsiCode() {
        return mmsiCode;
    }

    /**
     * Sets mmsi code.
     *
     * @param mmsiCode the mmsi code
     */
    public void setMmsiCode(BigDecimal mmsiCode) {
        this.mmsiCode = mmsiCode;
    }
}
