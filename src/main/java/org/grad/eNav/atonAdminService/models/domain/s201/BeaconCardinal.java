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

import _int.iho.s_201.gml.cs0._2.CategoryOfCardinalMarkType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * The S-201 Beacon Cardinal Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Beacon Cardinal
 * type. It is modelled as an entity that extends the {@link GenericBeacon}
 * super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.BeaconCardinal
 */
@Entity
public class BeaconCardinal extends GenericBeacon {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private CategoryOfCardinalMarkType categoryOfCardinalMark;

    /**
     * Gets category of cardinal mark.
     *
     * @return the category of cardinal mark
     */
    public CategoryOfCardinalMarkType getCategoryOfCardinalMark() {
        return categoryOfCardinalMark;
    }

    /**
     * Sets category of cardinal mark.
     *
     * @param categoryOfCardinalMark the category of cardinal mark
     */
    public void setCategoryOfCardinalMark(CategoryOfCardinalMarkType categoryOfCardinalMark) {
        this.categoryOfCardinalMark = categoryOfCardinalMark;
    }
}
