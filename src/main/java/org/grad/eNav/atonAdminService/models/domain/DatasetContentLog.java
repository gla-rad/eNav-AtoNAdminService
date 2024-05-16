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

package org.grad.eNav.atonAdminService.models.domain;

import jakarta.persistence.*;
import org.grad.eNav.atonAdminService.models.enums.DatasetOperation;
import org.grad.eNav.atonAdminService.models.enums.DatasetType;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The Dataset Content Log Entity Class
 * <p>
 * This class is used to log the generated S-201 dataset content into a
 * separate table that can be used for normal queries but also auditing
 * purposes. For existing datasets, this table will contain the history
 * of the dataset changes, but for non-existing ones, it will contain
 * a record of the deletion with its timestamp and last content version.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Entity
@Table(indexes = @Index(columnList = "datasetType, uuid, operation, sequenceNo, generatedAt"))
@EntityListeners(AuditingEntityListener.class)
@Cacheable
@Indexed
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DatasetContentLog implements Serializable {

    // Class Variables
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dataset_content_log_generator")
    @SequenceGenerator(name="dataset_content_log_generator", sequenceName = "dataset_content_log_seq", allocationSize=1)
    @Column(name = "id", nullable = false, precision = 24, scale = 0)
    private BigInteger id;

    @KeywordField(sortable = Sortable.YES)
    @Enumerated(EnumType.STRING)
    private DatasetType datasetType;

    @GenericField(sortable = Sortable.YES)
    @Column(nullable = false)
    private UUID uuid;

    @KeywordField(sortable = Sortable.YES)
    @Enumerated(EnumType.STRING)
    private DatasetOperation operation;

    @GenericField(sortable = Sortable.YES)
    private BigInteger sequenceNo;

    @GenericField(sortable = Sortable.YES)
    @CreatedDate
    private LocalDateTime generatedAt;

    private Geometry geometry;

    /*
     * This is actually created in Postgres as an OID field. To actually read
     * the contents of it we can run the following native query:
     * <p>
     *     select convert_from(lo_get(content), 'UTF-8') from dataset_content;
     * </p>
     */
    @Lob
    private String content;

    private BigInteger contentLength;

    /*
     * This is actually created in Postgres as an OID field. To actually read
     * the contents of it we can run the following native query:
     * <p>
     *     select convert_from(lo_get(delta), 'UTF-8') from dataset_content;
     * </p>
     */
    @Lob
    private String delta;

    private BigInteger deltaLength;

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

    /**
     * Gets content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets content.
     *
     * @param content the content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets content length.
     *
     * @return the content length
     */
    public BigInteger getContentLength() {
        return contentLength;
    }

    /**
     * Sets content length.
     *
     * @param contentLength the content length
     */
    public void setContentLength(BigInteger contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * Gets delta.
     *
     * @return the delta
     */
    public String getDelta() {
        return delta;
    }

    /**
     * Sets delta.
     *
     * @param delta the delta
     */
    public void setDelta(String delta) {
        this.delta = delta;
    }

    /**
     * Gets delta length.
     *
     * @return the delta length
     */
    public BigInteger getDeltaLength() {
        return deltaLength;
    }

    /**
     * Sets delta length.
     *
     * @param deltaLength the delta length
     */
    public void setDeltaLength(BigInteger deltaLength) {
        this.deltaLength = deltaLength;
    }
}
