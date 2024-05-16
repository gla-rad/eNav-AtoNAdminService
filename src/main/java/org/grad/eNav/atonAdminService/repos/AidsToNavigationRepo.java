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

import org.grad.eNav.atonAdminService.models.domain.s201.AidsToNavigation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Generic Aid to Navigation entities.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public interface AidsToNavigationRepo extends JpaRepository<AidsToNavigation, BigInteger>  {

    /**
     * Retrieves a database entry based on the Aids to Navigation ID Code if
     * that exists.
     *
     * @param idCode the Aids to Navigation ID code.
     * @return The Aids to Navigation if that exists
     */
    Optional<AidsToNavigation> findByIdCode(String idCode);

}
