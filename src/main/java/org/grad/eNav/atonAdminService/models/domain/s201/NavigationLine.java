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

import _int.iho.s_201.gml.cs0._2.CategoryOfNavigationLineType;
import _int.iho.s_201.gml.cs0._2.StatusType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * The S-201 Navigation Line Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Navigation Line
 * type. It is modelled as an entity that extends the {@link AidsToNavigation}
 * super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.NavigationLine
 */
@Entity
public class NavigationLine extends AidsToNavigation {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private CategoryOfNavigationLineType categoryOfNavigationLine;

    private BigDecimal orientation;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private List<StatusType> statuses;

    @ManyToMany(mappedBy = "navigationLines")
    private List<RecommendedTrack> navigableTracks;

    /**
     * Gets category of navigation line.
     *
     * @return the category of navigation line
     */
    public CategoryOfNavigationLineType getCategoryOfNavigationLine() {
        return categoryOfNavigationLine;
    }

    /**
     * Sets category of navigation line.
     *
     * @param categoryOfNavigationLine the category of navigation line
     */
    public void setCategoryOfNavigationLine(CategoryOfNavigationLineType categoryOfNavigationLine) {
        this.categoryOfNavigationLine = categoryOfNavigationLine;
    }

    /**
     * Gets orientation.
     *
     * @return the orientation
     */
    public BigDecimal getOrientation() {
        return orientation;
    }

    /**
     * Sets orientation.
     *
     * @param orientation the orientation
     */
    public void setOrientation(BigDecimal orientation) {
        this.orientation = orientation;
    }

    /**
     * Gets statuses.
     *
     * @return the statuses
     */
    public List<StatusType> getStatuses() {
        return statuses;
    }

    /**
     * Sets statuses.
     *
     * @param statuses the statuses
     */
    public void setStatuses(List<StatusType> statuses) {
        this.statuses = statuses;
    }

    /**
     * Gets navigable tracks.
     *
     * @return the navigable tracks
     */
    public List<RecommendedTrack> getNavigableTracks() {
        return navigableTracks;
    }

    /**
     * Sets navigable tracks.
     *
     * @param navigableTracks the navigable tracks
     */
    public void setNavigableTracks(List<RecommendedTrack> navigableTracks) {
        this.navigableTracks = navigableTracks;
    }
}
