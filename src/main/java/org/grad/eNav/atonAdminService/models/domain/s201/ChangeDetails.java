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

import _int.iho.s_201.gml.cs0._2.*;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.io.Serializable;

/**
 * The S-201 Change Details Embeddable Class.
 * <p/>
 * This class implements the Change Details type of the S-125 Aids to
 * Navigation objects. It contains the list of enums that can describe
 * the available changes for any AtoN.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.ChangeDetailsType
 */
@Embeddable
public class ChangeDetails implements Serializable {

    @Enumerated(EnumType.STRING)
    private AtonCommissioningType atonCommissioning;

    @Enumerated(EnumType.STRING)
    private AtonRemovalType atonRemoval;

    @Enumerated(EnumType.STRING)
    private AtonReplacementType atonReplacement;

    @Enumerated(EnumType.STRING)
    private FixedAtonChangeType fixedAtonChange;

    @Enumerated(EnumType.STRING)
    private FloatingAtonChangeType floatingAtonChange;

    @Enumerated(EnumType.STRING)
    private AudibleSignalAtonChangeType audibleSignalAtonChange;

    @Enumerated(EnumType.STRING)
    private LightedAtonChangeType lightedAtonChange;

    @Enumerated(EnumType.STRING)
    private ElectronicAtonChangeType electronicAtonChange;

    /**
     * Gets aton commissioning.
     *
     * @return the aton commissioning
     */
    public AtonCommissioningType getAtonCommissioning() {
        return atonCommissioning;
    }

    /**
     * Sets aton commissioning.
     *
     * @param atonCommissioning the aton commissioning
     */
    public void setAtonCommissioning(AtonCommissioningType atonCommissioning) {
        this.atonCommissioning = atonCommissioning;
    }

    /**
     * Gets aton removal.
     *
     * @return the aton removal
     */
    public AtonRemovalType getAtonRemoval() {
        return atonRemoval;
    }

    /**
     * Sets aton removal.
     *
     * @param atonRemoval the aton removal
     */
    public void setAtonRemoval(AtonRemovalType atonRemoval) {
        this.atonRemoval = atonRemoval;
    }

    /**
     * Gets aton replacement.
     *
     * @return the aton replacement
     */
    public AtonReplacementType getAtonReplacement() {
        return atonReplacement;
    }

    /**
     * Sets aton replacement.
     *
     * @param atonReplacement the aton replacement
     */
    public void setAtonReplacement(AtonReplacementType atonReplacement) {
        this.atonReplacement = atonReplacement;
    }

    /**
     * Gets fixed aton change.
     *
     * @return the fixed aton change
     */
    public FixedAtonChangeType getFixedAtonChange() {
        return fixedAtonChange;
    }

    /**
     * Sets fixed aton change.
     *
     * @param fixedAtonChange the fixed aton change
     */
    public void setFixedAtonChange(FixedAtonChangeType fixedAtonChange) {
        this.fixedAtonChange = fixedAtonChange;
    }

    /**
     * Gets floating aton change.
     *
     * @return the floating aton change
     */
    public FloatingAtonChangeType getFloatingAtonChange() {
        return floatingAtonChange;
    }

    /**
     * Sets floating aton change.
     *
     * @param floatingAtonChange the floating aton change
     */
    public void setFloatingAtonChange(FloatingAtonChangeType floatingAtonChange) {
        this.floatingAtonChange = floatingAtonChange;
    }

    /**
     * Gets audible signal aton change.
     *
     * @return the audible signal aton change
     */
    public AudibleSignalAtonChangeType getAudibleSignalAtonChange() {
        return audibleSignalAtonChange;
    }

    /**
     * Sets audible signal aton change.
     *
     * @param audibleSignalAtonChange the audible signal aton change
     */
    public void setAudibleSignalAtonChange(AudibleSignalAtonChangeType audibleSignalAtonChange) {
        this.audibleSignalAtonChange = audibleSignalAtonChange;
    }

    /**
     * Gets lighted aton change.
     *
     * @return the lighted aton change
     */
    public LightedAtonChangeType getLightedAtonChange() {
        return lightedAtonChange;
    }

    /**
     * Sets lighted aton change.
     *
     * @param lightedAtonChange the lighted aton change
     */
    public void setLightedAtonChange(LightedAtonChangeType lightedAtonChange) {
        this.lightedAtonChange = lightedAtonChange;
    }

    /**
     * Gets electronic aton change.
     *
     * @return the electronic aton change
     */
    public ElectronicAtonChangeType getElectronicAtonChange() {
        return electronicAtonChange;
    }

    /**
     * Sets electronic aton change.
     *
     * @param electronicAtonChange the electronic aton change
     */
    public void setElectronicAtonChange(ElectronicAtonChangeType electronicAtonChange) {
        this.electronicAtonChange = electronicAtonChange;
    }
}
