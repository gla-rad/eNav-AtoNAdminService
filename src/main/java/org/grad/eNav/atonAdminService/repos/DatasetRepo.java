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

package org.grad.eNav.atonAdminService.repos;

import org.grad.eNav.atonAdminService.models.domain.s201.S201Dataset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;

/**
 * Spring Data JPA repository for the S-201 Dataset entities.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public interface DatasetRepo extends JpaRepository<S201Dataset, UUID> {

    /**
     * Find whether a specific UUID exists that belongs to a cancelled dataset.
     * This function should be used to retrieve data, but can be utilised to
     * check whether a dataset has been cancelled.
     *
     * @param uuid      The UUID of the dataset to be checked
     * @param cancelled Whether the dataset has been cancelled or not
     * @return the dataset matching the UUID and cancellation status
     */
    Optional<S201Dataset> findByUuidAndCancelled(UUID uuid, Boolean cancelled);
    
}
