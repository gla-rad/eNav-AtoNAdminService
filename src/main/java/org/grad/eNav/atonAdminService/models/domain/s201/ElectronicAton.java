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

import _int.iho.s_201.gml.cs0._2.StatusType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import java.util.Set;

/**
 * The S-201 SElectronic AtoN Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Electronic
 * AtoN type. It is modelled as an entity that extends the
 * {@link AidsToNavigation} super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.ElectronicAtonType
 * @see _int.iho.s_201.gml.cs0._2.PhysicalAISAidToNavigation
 * @see _int.iho.s_201.gml.cs0._2.VirtualAISAidToNavigation
 * @see _int.iho.s_201.gml.cs0._2.SyntheticAISAidToNavigation
 */
@Entity
public abstract class ElectronicAton extends AidsToNavigation {

    @KeywordField(name="aton_number", sortable = Sortable.YES)
    private String atonNumber;

    @KeywordField(sortable = Sortable.YES)
    private String mmsiCode;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private Set<StatusType> statuses;

    @JsonBackReference
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "broadcast_by_join_table",
            joinColumns = { @JoinColumn(name = "electronic_aton_id") },
            inverseJoinColumns = { @JoinColumn(name = "radio_station_id") }
    )
    private Set<RadioStation> broadcastBy;

    /**
     * Gets ato n number.
     *
     * @return the ato n number
     */
    public String getAtonNumber() {
        return atonNumber;
    }

    /**
     * Sets ato n number.
     *
     * @param atonNumber the ato n number
     */
    public void setAtonNumber(String atonNumber) {
        this.atonNumber = atonNumber;
    }

    /**
     * Gets mmsi code.
     *
     * @return the mmsi code
     */
    public String getMmsiCode() {
        return mmsiCode;
    }

    /**
     * Sets mmsi code.
     *
     * @param mmsiCode the mmsi code
     */
    public void setMmsiCode(String mmsiCode) {
        this.mmsiCode = mmsiCode;
    }

    /**
     * Gets statuses.
     *
     * @return the statuses
     */
    public Set<StatusType> getStatuses() {
        return statuses;
    }

    /**
     * Sets statuses.
     *
     * @param statuses the statuses
     */
    public void setStatuses(Set<StatusType> statuses) {
        this.statuses = statuses;
    }

    /**
     * Gets broadcast by.
     *
     * @return the broadcast by
     */
    public Set<RadioStation> getBroadcastBy() {
        return broadcastBy;
    }

    /**
     * Sets broadcast by.
     *
     * @param broadcastBy the broadcast by
     */
    public void setBroadcastBy(Set<RadioStation> broadcastBy) {
        this.broadcastBy = broadcastBy;
    }
}
