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

package org.grad.eNav.atonAdminService.utils;

import _int.iho.s201.s100.gml.profiles._5_0.ReferenceType;
import _int.iho.s201.s100.gml.profiles._5_0.impl.ReferenceTypeImpl;
import org.grad.eNav.atonAdminService.models.enums.ReferenceTypeRole;
import org.grad.eNav.atonAdminService.models.domain.s201.AidsToNavigation;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The type Reference Type S-201 Converter Class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class ReferenceTypeS201Converter {

    /**
     * Convert an S-201 Aid to Navigation entry to an S-201 reference type
     * object.
     *
     * @param aidToNavigation   The Local Aids to Navigation object
     * @param role              The role to be assigned to the generated reference types
     * @return the respective S-201 reference type
     */
    public ReferenceType convertToReferenceType(AidsToNavigation aidToNavigation, ReferenceTypeRole role) {
        return Optional.ofNullable(aidToNavigation)
                .map(peer -> {
                    ReferenceType referenceType = new ReferenceTypeImpl();
                    referenceType.setHref("#ID-ATON-" + peer.getId());
                    referenceType.setRole(role.getRole());
                    referenceType.setArcrole(role.getArchRole());
                    return referenceType;
                })
                .orElse(null);
    }

    /**
     * Converts a list of S-201 Aids to Navigation to a list of S-201 reference
     * type objects.
     *
     * @param aidsToNavigation  The list of Aids to Navigation objects
     * @param role              The role to be assigned to the generated reference types
     * @return the respective list of S-201 reference types
     */
    public List<ReferenceType> convertToReferenceTypes(List<? extends AidsToNavigation> aidsToNavigation, ReferenceTypeRole role) {
        return aidsToNavigation.stream()
                .map(aton -> this.convertToReferenceType(aton, role))
                .collect(Collectors.toList());
    }

    /**
     * Converts a set of S-201 Aids to Navigation to a set of S-201 reference
     * type objects.
     *
     * @param aidsToNavigation  The set of Aids to Navigation objects
     * @param role              The role to be assigned to the generated reference types
     * @return the respective set of S-201 reference types
     */
    public List<ReferenceType> convertToReferenceTypes(Set<? extends AidsToNavigation> aidsToNavigation, ReferenceTypeRole role) {
        return aidsToNavigation.stream()
                .map(aton -> this.convertToReferenceType(aton, role))
                .collect(Collectors.toList());
    }

}
