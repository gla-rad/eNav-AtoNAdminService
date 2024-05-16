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

package org.grad.eNav.atonAdminService.models.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.grad.eNav.atonAdminService.models.enums.DatasetOperation;
import org.grad.eNav.atonAdminService.models.enums.DatasetType;
import org.grad.eNav.atonAdminService.utils.GeometryJSONDeserializer;
import org.grad.eNav.atonAdminService.utils.GeometryJSONSerializer;
import org.locationtech.jts.geom.Geometry;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The Dataset Content Log DTO.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class DatasetContentLogDto {

    // Class Variables
    private BigInteger id;
    private DatasetType datasetType;
    private UUID uuid;
    private DatasetOperation operation;
    private BigInteger sequenceNo;
    private LocalDateTime generatedAt;
    @JsonSerialize(using = GeometryJSONSerializer.class)
    @JsonDeserialize(using = GeometryJSONDeserializer.class)
    private Geometry geometry;

    /**
     * Gets id.
     *
     * @return the id
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(BigInteger id) {
        this.id = id;
    }

    /**
     * Gets dataset type.
     *
     * @return the dataset type
     */
    public DatasetType getDatasetType() {
        return datasetType;
    }

    /**
     * Sets dataset type.
     *
     * @param datasetType the dataset type
     */
    public void setDatasetType(DatasetType datasetType) {
        this.datasetType = datasetType;
    }

    /**
     * Gets uuid.
     *
     * @return the uuid
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Sets uuid.
     *
     * @param uuid the uuid
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets operation.
     *
     * @return the operation
     */
    public DatasetOperation getOperation() {
        return operation;
    }

    /**
     * Sets operation.
     *
     * @param operation the operation
     */
    public void setOperation(DatasetOperation operation) {
        this.operation = operation;
    }

    /**
     * Gets sequence no.
     *
     * @return the sequence no
     */
    public BigInteger getSequenceNo() {
        return sequenceNo;
    }

    /**
     * Sets sequence no.
     *
     * @param sequenceNo the sequence no
     */
    public void setSequenceNo(BigInteger sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    /**
     * Gets generated at.
     *
     * @return the generated at
     */
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    /**
     * Sets generated at.
     *
     * @param generatedAt the generated at
     */
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    /**
     * Gets geometry.
     *
     * @return the geometry
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * Sets geometry.
     *
     * @param geometry the geometry
     */
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

}
