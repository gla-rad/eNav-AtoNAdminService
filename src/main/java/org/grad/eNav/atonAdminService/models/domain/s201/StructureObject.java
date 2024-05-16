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

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.HashSet;
import java.util.Set;

/**
 * The S-201 Structure Object Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Structure
 * type. It is modelled as an entity class on hibernate, but it is abstract so
 * that we can extend this for each Structure Object type.
 * <p>
 * Each structure contains a list of equipment objects that is hosts.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s201.gml.cs0._1.StructureObjectType
 */
@Entity
public abstract class StructureObject extends AidsToNavigation {

    //Class Variables
    @JsonManagedReference
    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE, orphanRemoval = true)
    final private Set<Equipment> children = new HashSet<>();

    /**
     * Gets children.
     *
     * @return the children
     */
    public Set<Equipment> getChildren() {
        return children;
    }

    /**
     * Sets children.
     *
     * @param children the children
     */
    public void setChildren(Set<Equipment> children) {
        this.children.clear();
        if (children!= null) {
            this.children.addAll(children);
        }
    }
}
