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

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.List;

/**
 * The S-201 AIS Aids to Navigation Entity Class.
 * <p>
 * This class is not really defined in S-201 (althought this might not be such
 * a bad idea) and it was introduced so that we can easily model and extend
 * the three AIS AtoN types, i.e the Physical, the Virtual and the Synthetic
 * one.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s201.gml.cs0._1.PhysicalAISAidToNavigation
 * @see _int.iho.s201.gml.cs0._1.VirtualAISAidToNavigation
 * @see _int.iho.s201.gml.cs0._1.SyntheticAISAidToNavigation
 */
@Entity
public abstract class AISAidToNavigation extends Equipment {

    // Class Variables
    @JsonBackReference
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "broadcast_by_join_table",
            joinColumns = { @JoinColumn(name = "ais_aton_id") },
            inverseJoinColumns = { @JoinColumn(name = "radio_station_id") }
    )
    private List<RadioStation> broadcastBy;

    /**
     * Gets broadcast by.
     *
     * @return the broadcast by
     */
    public List<RadioStation> getBroadcastBy() {
        return broadcastBy;
    }

    /**
     * Sets broadcast by.
     *
     * @param broadcastBy the broadcast by
     */
    public void setBroadcastBy(List<RadioStation> broadcastBy) {
        this.broadcastBy = broadcastBy;
    }
}
