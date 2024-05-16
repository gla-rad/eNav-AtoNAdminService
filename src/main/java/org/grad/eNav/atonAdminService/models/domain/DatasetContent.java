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
import org.grad.eNav.atonAdminService.models.domain.s201.S201Dataset;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * The S-201 Dataset Entity Content Class
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
@EntityListeners(AuditingEntityListener.class)
@Cacheable
@Indexed
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DatasetContent implements Serializable {

    // Class Variables
    @Id
    @ScaledNumberField(name = "id_sort", decimalScale=0, sortable = Sortable.YES)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dataset_content_generator")
    @SequenceGenerator(name="dataset_content_generator", sequenceName = "dataset_content_seq", allocationSize=1)
    @Column(name = "id", nullable = false, precision = 24, scale = 0)
    private BigInteger id;

    @AssociationInverseSide(inversePath = @ObjectPath(@PropertyValue(propertyName = "datasetContent")))
    @OneToOne(mappedBy = "datasetContent")
    private S201Dataset dataset;

    @GenericField(sortable = Sortable.YES)
    @LastModifiedDate
    private LocalDateTime generatedAt;

    @GenericField(sortable = Sortable.YES)
    private BigInteger sequenceNo;

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
     * Gets dataset.
     *
     * @return the dataset
     */
    public S201Dataset getDataset() {
        return dataset;
    }

    /**
     * Sets dataset.
     *
     * @param dataset the dataset
     */
    public void setDataset(S201Dataset dataset) {
        this.dataset = dataset;
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
     * Sets generated at.
     *
     * @param generatedAt the generated at
     */
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
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

    /**
     * This helper function will completely strip out the content information
     * from this dataset content object.
     */
    public DatasetContent clearContent() {
        // Clear the content
        this.content = null;
        this.contentLength = BigInteger.ZERO;

        // And return the object for easy use
        return this;
    }

    /**
     * This helper function will completely strip out the delta information
     * from this dataset content object.
     */
    public DatasetContent clearDelta() {
        // Clear the delta
        //this.content = null;
        //this.contentLength = BigInteger.ZERO;
        this.delta = null;
        this.deltaLength = BigInteger.ZERO;

        // And return the object for easy use
        return this;
    }

    /**
     * Whenever a persistence/update operation takes place, the sequence number
     * of the dataset content should be increased.
     * <p/>
     * NOTE: This function is be annotated with a @PrePersist/@PreUpdate
     * annotation, but this is not fired in cases where the updated entry
     * remains the same. Note a big issue, but we need to be careful to create
     * it even in those cases.
     */
    @PrePersist
    @PreUpdate
    @PreRemove
    public void increaseSequenceNo() {
        this.sequenceNo = Optional.ofNullable(this.sequenceNo)
                .map(BigInteger.ONE::add)
                .orElse(BigInteger.ZERO);
    }

}
