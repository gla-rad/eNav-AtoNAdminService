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

/**
 * The S-201 Directional Character Embeddable Class.
 * <p/>
 * This is the basic class for implementing the S-201-compatible Directional
 * Character type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.DirectionalCharacterType
 */
@Embeddable
public class DirectionalCharacter {

    // Class Variables
    private Boolean moireEffect;

    private Orientation orientation;

    /**
     * Gets moire effect.
     *
     * @return the moire effect
     */
    public Boolean getMoireEffect() {
        return moireEffect;
    }

    /**
     * Sets moire effect.
     *
     * @param moireEffect the moire effect
     */
    public void setMoireEffect(Boolean moireEffect) {
        this.moireEffect = moireEffect;
    }

    /**
     * Gets orientation.
     *
     * @return the orientation
     */
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * Sets orientation.
     *
     * @param orientation the orientation
     */
    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }
}
