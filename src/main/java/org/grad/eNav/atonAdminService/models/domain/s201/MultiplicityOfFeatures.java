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

import java.math.BigInteger;

/**
 * The S-201 Multiplicity Of Features Embeddable Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Multiplicity
 * Of Features type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.MultiplicityOfFeaturesType
 */
@Embeddable
public class MultiplicityOfFeatures {

    // Class Variables
    private Boolean multiplicityKnown;

    private BigInteger numberOfFeatures;

    /**
     * Gets multiplicity known.
     *
     * @return the multiplicity known
     */
    public Boolean getMultiplicityKnown() {
        return multiplicityKnown;
    }

    /**
     * Sets multiplicity known.
     *
     * @param multiplicityKnown the multiplicity known
     */
    public void setMultiplicityKnown(Boolean multiplicityKnown) {
        this.multiplicityKnown = multiplicityKnown;
    }

    /**
     * Gets number of features.
     *
     * @return the number of features
     */
    public BigInteger getNumberOfFeatures() {
        return numberOfFeatures;
    }

    /**
     * Sets number of features.
     *
     * @param numberOfFeatures the number of features
     */
    public void setNumberOfFeatures(BigInteger numberOfFeatures) {
        this.numberOfFeatures = numberOfFeatures;
    }
}
