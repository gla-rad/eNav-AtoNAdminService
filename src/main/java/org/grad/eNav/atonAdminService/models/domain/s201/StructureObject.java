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

import _int.iho.s_201.gml.cs0._2.AidAvailabilityCategoryType;
import _int.iho.s_201.gml.cs0._2.ConditionType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

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
 * @see _int.iho.s_201.gml.cs0._2.StructureObjectType
 */
@Entity
public abstract class StructureObject extends AidsToNavigation {

    //Class Variables
    @KeywordField(name="aton_number", sortable = Sortable.YES)
    private String atonNumber;

    @Enumerated(EnumType.STRING)
    private AidAvailabilityCategoryType aidAvailabilityCategory;

    @Enumerated(EnumType.STRING)
    private ConditionType condition;

    private ContactAddress contactAddress;

    @JsonManagedReference
    @OneToMany(mappedBy = "feature", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Information> informations = new HashSet<>();

    private Set<AtonFixingMethod> fixingMethods = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "positions", cascade = CascadeType.ALL, orphanRemoval = true)
    final private Set<PositioningInformation> positioningMethods = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE, orphanRemoval = true)
    final private Set<Equipment> children = new HashSet<>();

    /**
     * Gets aton number.
     *
     * @return the aton number
     */
    public String getAtonNumber() {
        return atonNumber;
    }

    /**
     * Sets aton number.
     *
     * @param atonNumber the aton number
     */
    public void setAtonNumber(String atonNumber) {
        this.atonNumber = atonNumber;
    }

    /**
     * Gets aid availability category.
     *
     * @return the aid availability category
     */
    public AidAvailabilityCategoryType getAidAvailabilityCategory() {
        return aidAvailabilityCategory;
    }

    /**
     * Sets aid availability category.
     *
     * @param aidAvailabilityCategory the aid availability category
     */
    public void setAidAvailabilityCategory(AidAvailabilityCategoryType aidAvailabilityCategory) {
        this.aidAvailabilityCategory = aidAvailabilityCategory;
    }

    /**
     * Gets condition type.
     *
     * @return the condition type
     */
    public ConditionType getCondition() {
        return condition;
    }

    /**
     * Sets condition type.
     *
     * @param condition the condition type
     */
    public void setCondition(ConditionType condition) {
        this.condition = condition;
    }

    /**
     * Gets contact address.
     *
     * @return the contact address
     */
    public ContactAddress getContactAddress() {
        return contactAddress;
    }

    /**
     * Sets contact address.
     *
     * @param contactAddress the contact address
     */
    public void setContactAddress(ContactAddress contactAddress) {
        this.contactAddress = contactAddress;
    }

    /**
     * Gets positioning methods.
     *
     * @return the positioning methods
     */
    public Set<PositioningInformation> getPositioningMethods() {
        return positioningMethods;
    }

    /**
     * Sets positioning methods.
     *
     * @param positioningMethods the positioning methods
     */
    public void setPositioningMethods(Set<PositioningInformation> positioningMethods) {
        if(positioningMethods != null)  {
            positioningMethods.forEach(positioningMethod -> positioningMethod.setPositions(this));
        }
        this.positioningMethods.clear();
        this.positioningMethods.addAll(positioningMethods);
    }

    /**
     * Gets fixing methods.
     *
     * @return the fixing methods
     */
    public Set<AtonFixingMethod> getFixingMethods() {
        return fixingMethods;
    }

    /**
     * Sets fixing methods.
     *
     * @param fixingMethods the fixing methods
     */
    public void setFixingMethods(Set<AtonFixingMethod> fixingMethods) {
        if(fixingMethods != null) {
            fixingMethods.forEach(positioningMethod -> positioningMethod.setFixes(this));
        }
        this.fixingMethods.clear();
        this.fixingMethods.addAll(fixingMethods);
    }

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
        if(children != null) {
            children.forEach(child -> child.setParent(this));
        }
        this.children.clear();
        this.children.addAll(children);
    }
}
