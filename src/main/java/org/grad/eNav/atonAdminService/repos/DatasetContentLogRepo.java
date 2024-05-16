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

import org.grad.eNav.atonAdminService.models.domain.DatasetContentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for the S-201 Dataset Content Log entities.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public interface DatasetContentLogRepo extends JpaRepository<DatasetContentLog, BigInteger> {

    /**
     * Find all the dataset content log entries that match a specific UUID.
     *
     * @param uuid              The UUID of the dataset
     * @return the matching dataset content log entry if it exists
     */
    List<DatasetContentLog> findByUuid(UUID uuid);

    /**
     * Retrieves the initial dataset content entry (i.e. the one with sequence
     * number equal to ZERO (0)) for the provided UUID.
     *
     * @param uuid              The UUID of the dataset
     * @return the original dataset content log entry if it exists
     */
    @Query("select d from DatasetContentLog d where d.uuid = :uuid AND d.sequenceNo = 0")
    Optional<DatasetContentLog> findInitialForUuid(UUID uuid);

    /**
     * Retrieves the latest dataset content entries for a specific UUID. It
     * also accepts a pageable argument to provide only a subset of the
     * matching entries (e.g. just the last one).
     *
     * @param uuid              The UUID of the dataset
     * @param generatedAt       The generation date of the content
     * @return the latest dataset content log entry if it exists
     */
    @Query("select d from DatasetContentLog d where d.uuid = :uuid AND d.generatedAt <= :generatedAt ORDER BY d.generatedAt DESC")
    List<DatasetContentLog> findLatestForUuid(UUID uuid, LocalDateTime generatedAt);

    /**
     * Retrieves the latest dataset content entries for a specific UUID. It
     * also accepts a pageable argument to provide only a subset of the
     * matching entries (e.g. just the last one).
     *
     * @param uuid              The UUID of the dataset
     * @param generatedFrom     The "from" date-time to select the content logs for
     * @param generatedTo       The "to" date-time to select the content logs for
     * @return the latest dataset content log entry if it exists
     */
    @Query("select d from DatasetContentLog d where d.uuid = :uuid AND d.generatedAt >= :generatedFrom AND d.generatedAt <= :generatedTo ORDER BY d.generatedAt ASC")
    List<DatasetContentLog> findDuringForUuid(UUID uuid, LocalDateTime generatedFrom, LocalDateTime generatedTo);

}
