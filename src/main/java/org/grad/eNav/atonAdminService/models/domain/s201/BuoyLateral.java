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

import _int.iho.s_201.gml.cs0._2.CategoryOfLateralMarkType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * The S-201 Buoy Lateral Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Buoy Lateral
 * type. It is modelled as an entity that extends the {@link GenericBuoy} super
 * class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.BuoyLateral
 */
@Entity
public class BuoyLateral extends GenericBuoy {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private CategoryOfLateralMarkType categoryOfLateralMark;

    /**
     * Gets category of lateral mark.
     *
     * @return the category of lateral mark
     */
    public CategoryOfLateralMarkType getCategoryOfLateralMark() {
        return categoryOfLateralMark;
    }

    /**
     * Sets category of lateral mark.
     *
     * @param categoryOfLateralMark the category of lateral mark
     */
    public void setCategoryOfLateralMark(CategoryOfLateralMarkType categoryOfLateralMark) {
        this.categoryOfLateralMark = categoryOfLateralMark;
    }
}
