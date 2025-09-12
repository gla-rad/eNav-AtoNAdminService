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

import _int.iho.s_201.gml.cs0._2.CategoryOfRadioStationType;
import _int.iho.s_201.gml.cs0._2.StatusType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

/**
 * The S-201 Radio Station Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Radio Station
 * type. It is modelled as an entity that extends the {@link Equipment} super
 * class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.RadioStation
 */
@Entity
public class RadioStation extends Equipment {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private CategoryOfRadioStationType categoryOfRadioStation;

    @Enumerated(EnumType.STRING)
    private StatusType status;

    @JsonManagedReference
    @ManyToMany(mappedBy = "broadcastBy")
    private List<AISAidToNavigation> broadcasts;

    /**
     * Gets category of radio station.
     *
     * @return the category of radio station
     */
    public CategoryOfRadioStationType getCategoryOfRadioStation() {
        return categoryOfRadioStation;
    }

    /**
     * Sets category of radio station.
     *
     * @param categoryOfRadioStation the category of radio station
     */
    public void setCategoryOfRadioStation(CategoryOfRadioStationType categoryOfRadioStation) {
        this.categoryOfRadioStation = categoryOfRadioStation;
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
     * Gets broadcasts.
     *
     * @return the broadcasts
     */
    public List<AISAidToNavigation> getBroadcasts() {
        return broadcasts;
    }

    /**
     * Sets broadcasts.
     *
     * @param broadcasts the broadcasts
     */
    public void setBroadcasts(List<AISAidToNavigation> broadcasts) {
        this.broadcasts = broadcasts;
    }
}
