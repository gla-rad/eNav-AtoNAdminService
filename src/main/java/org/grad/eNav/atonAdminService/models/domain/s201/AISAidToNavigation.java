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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "broadcasts", orphanRemoval = false)
    private List<RadioStation> broadcastedBy;

    /**
     * Gets broadcasted by.
     *
     * @return the broadcasted by
     */
    public List<RadioStation> getBroadcastedBy() {
        return broadcastedBy;
    }

    /**
     * Sets broadcasted by.
     *
     * @param broadcastedBy the broadcasted by
     */
    public void setBroadcastedBy(List<RadioStation> broadcastedBy) {
        this.broadcastedBy = broadcastedBy;
    }
}
