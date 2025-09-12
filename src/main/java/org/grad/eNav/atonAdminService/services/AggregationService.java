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

package org.grad.eNav.atonAdminService.services;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.shaded.com.google.common.collect.Sets;
import org.grad.eNav.atonAdminService.exceptions.DataNotFoundException;
import org.grad.eNav.atonAdminService.models.domain.s201.AtonAggregation;
import org.grad.eNav.atonAdminService.models.domain.s201.AidsToNavigation;
import org.grad.eNav.atonAdminService.repos.AtonAggregationRepo;
import org.grad.eNav.atonAdminService.repos.AidsToNavigationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The Aggregation Service.
 *
 * Service Implementation for managing the S-201 Aggregation objects.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Service
@Slf4j
public class AggregationService {

    /**
     * The Aggregation Repo.
     */
    @Autowired
    AtonAggregationRepo atonAggregationRepo;

    /**
     * The Aids to Navigation Repo.
     */
    @Autowired
    AidsToNavigationRepo aidsToNavigationRepo;

    /**
     * A simple saving operation that persists the models in the database using
     * the correct repository based on the instance type.
     *
     * @param aggregation   The aggregation to be persisted
     * @return the persisted aggregation entity
     */
    @Transactional
    public AtonAggregation save(AtonAggregation aggregation) {
        log.debug("Request to save aggregation : {}", aggregation);

        // Now save for each type
        return this.atonAggregationRepo.save(aggregation);
    }

    /**
     * Delete the Aggregation by ID.
     *
     * @param id the ID of the Aggregation
     */
    @Transactional
    public AtonAggregation delete(BigInteger id) {
        log.debug("Request to delete aggregation with ID : {}", id);

        // Make sure the station node exists
        final AtonAggregation aggregation = this.atonAggregationRepo.findById(id)
                .orElseThrow(() -> new DataNotFoundException(String.format("No aggregation found for the provided ID: %d", id)));

        // Now delete the aggregation
        this.atonAggregationRepo.delete(aggregation);

        // And return the object for AOP
        return aggregation;
    }

    /**
     * Updates the aggregation set of a given Aid to Navigation by retaining
     * the unchanged entries, deleting the obsolete entries and creating all
     * new ones that should be created.
     * <p/>
     * Note that this function is heavily dependent on set operations. The
     * reason is that we receive the aggregation information mainly from
     * S-201 datasets and these do not contain clear identifiers for the
     * aggregations and associations. So we have to rely on the type and peers
     * of the object. Therefore, that's how it is mapped and checked against
     * the existing entries.
     *
     * @param idCode The ID Code of the Aid to Navigation included in the aggregations
     * @param newAggregations The new aggregations to update the Aid to Navigation with
     * @return the update Aids to Navigation
     */
    @Transactional
    public Set<AtonAggregation> updateAidsToNavigationAggregations(@NotNull String idCode, @NotNull Set<AtonAggregation> newAggregations) {
        // Find the matching new aggregations
        final Set<AtonAggregation> oldAggregations = this.atonAggregationRepo.findByIncludedIdCode(idCode);

        // Perform the set operations - find the existing ones to be retained
        final Set<AtonAggregation> existingAggregations = Sets.intersection(oldAggregations, newAggregations);

        // Delete the obsolete aggregations
        final Set<AtonAggregation> deletedAggregations = Sets.difference(oldAggregations, existingAggregations)
                .stream()
                .map(AtonAggregation::getId)
                .map(this::delete)
                .collect(Collectors.toSet());

        // Create the new aggregations
        final Set<AtonAggregation> createdAggregations = Sets.difference(newAggregations, existingAggregations)
                .stream()
                // We need to make sure that we have the correct objects to persist
                .peek(aggregation -> aggregation.setPeers(aggregation.getPeers()
                        .stream()
                        .map(AidsToNavigation::getIdCode)
                        .map(this.aidsToNavigationRepo::findByIdCode)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet())))
                .map(this::save)
                .collect(Collectors.toSet());

        // Combine the existing and new aggregations for the AtoN
        return Sets.union(existingAggregations, createdAggregations);
    }

}
