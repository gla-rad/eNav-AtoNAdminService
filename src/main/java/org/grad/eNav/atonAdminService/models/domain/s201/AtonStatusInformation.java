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

import _int.iho.s_201.gml.cs0._2.ChangeTypesType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * The S-201 AtoN Status Information Entity Class.
 * <p/>
 * This class implements the AtoN Status INformation type of the S-201 Aids to
 * Navigation objects. It is modelled as an entity that extends the
 * {@link Information} super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.AtonStatusInformation
 */
@Entity
public class AtonStatusInformation extends Information {

    // Class Variables
    private ChangeDetails changeDetails;

    @Enumerated(EnumType.STRING)
    private ChangeTypesType changeTypes;

    /**
     * Gets change details.
     *
     * @return the change details
     */
    public ChangeDetails getChangeDetails() {
        return changeDetails;
    }

    /**
     * Sets change details.
     *
     * @param changeDetails the change details
     */
    public void setChangeDetails(ChangeDetails changeDetails) {
        this.changeDetails = changeDetails;
    }

    /**
     * Gets change types.
     *
     * @return the change types
     */
    public ChangeTypesType getChangeTypes() {
        return changeTypes;
    }

    /**
     * Sets change types.
     *
     * @param changeTypes the change types
     */
    public void setChangeTypes(ChangeTypesType changeTypes) {
        this.changeTypes = changeTypes;
    }
}
