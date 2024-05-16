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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.grad.eNav.atonAdminService.utils.GeometryBinder;
import org.grad.eNav.atonAdminService.utils.GeometryJSONDeserializer;
import org.grad.eNav.atonAdminService.utils.GeometryJSONSerializer;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBinderRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * The S-201 Aids to Navigation Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Aids to
 * Navigation type. It is modelled as an entity class on hibernate, but it is
 * abstract so that we can extend this for each Aids to Navigation type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s201.gml.cs0._1.AidsToNavigationType
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Cacheable
@Indexed
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public abstract class AidsToNavigation implements Serializable {

    // Class Variables
    @Id
    @ScaledNumberField(name = "id_sort", decimalScale=0, sortable = Sortable.YES)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aids_to_navigation_generator")
    @SequenceGenerator(name="aids_to_navigation_generator", sequenceName = "aids_to_navigation_seq", allocationSize=1)
    private BigInteger id;

    @KeywordField(name="aton_number", sortable = Sortable.YES)
    @Column(unique=true)
    private String atonNumber;

    @NotNull
    @KeywordField(name="id_code", sortable = Sortable.YES)
    @Column(unique=true)
    private String idCode;

    @GenericField(indexNullAs = "9999-01-01")
    private LocalDate dateEnd;

    @GenericField(indexNullAs = "1970-01-01")
    private LocalDate dateStart;

    @GenericField(indexNullAs = "9999-01-01")
    private LocalDate periodEnd;

    @GenericField(indexNullAs = "1970-01-01")
    private LocalDate periodStart;

    private BigInteger scaleMinimum;

    private String pictorialRepresentation;

    @JsonSerialize(using = GeometryJSONSerializer.class)
    @JsonDeserialize(using = GeometryJSONDeserializer.class)
    @NonStandardField(name="geometry", valueBinder = @ValueBinderRef(type = GeometryBinder.class))
    private Geometry geometry;

    @JsonManagedReference
    @OneToMany(mappedBy = "feature", cascade = CascadeType.REMOVE, orphanRemoval = true)
    final private Set<Information> informations = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "feature", cascade = CascadeType.REMOVE, orphanRemoval = true)
    final private Set<FeatureName> featureNames = new HashSet<>();

    @JsonManagedReference
    @ManyToMany(mappedBy = "peers")
    final private Set<Aggregation> aggregations = new HashSet<>();

    @JsonManagedReference
    @ManyToMany(mappedBy = "peers")
    final private Set<Association> associations = new HashSet<>();

    @GenericField()
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

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
     * Gets aton number.
     *
     * @return the aton number
     */
    @Deprecated
    public String getAtonNumber() {
        return atonNumber;
    }

    /**
     * Sets aton number.
     *
     * @param atonNumber the aton number
     */
    @Deprecated
    public void setAtonNumber(String atonNumber) {
        this.atonNumber = atonNumber;
    }

    /**
     * Gets id code.
     *
     * @return the id code
     */
    public String getIdCode() {
        return idCode;
    }

    /**
     * Sets id code.
     *
     * @param idCode the id code
     */
    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    /**
     * Gets date end.
     *
     * @return the date end
     */
    public LocalDate getDateEnd() {
        return dateEnd;
    }

    /**
     * Sets date end.
     *
     * @param dateEnd the date end
     */
    public void setDateEnd(LocalDate dateEnd) {
        this.dateEnd = dateEnd;
    }

    /**
     * Gets date start.
     *
     * @return the date start
     */
    public LocalDate getDateStart() {
        return dateStart;
    }

    /**
     * Sets date start.
     *
     * @param dateStart the date start
     */
    public void setDateStart(LocalDate dateStart) {
        this.dateStart = dateStart;
    }

    /**
     * Gets period end.
     *
     * @return the period end
     */
    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    /**
     * Sets period end.
     *
     * @param periodEnd the period end
     */
    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    /**
     * Gets period start.
     *
     * @return the period start
     */
    public LocalDate getPeriodStart() {
        return periodStart;
    }

    /**
     * Sets period start.
     *
     * @param periodStart the period start
     */
    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    /**
     * Gets scale minimum.
     *
     * @return the scale minimum
     */
    public BigInteger getScaleMinimum() {
        return scaleMinimum;
    }

    /**
     * Sets scale minimum.
     *
     * @param scaleMinimum the scale minimum
     */
    public void setScaleMinimum(BigInteger scaleMinimum) {
        this.scaleMinimum = scaleMinimum;
    }

    /**
     * Gets pictorial representation.
     *
     * @return the pictorial representation
     */
    public String getPictorialRepresentation() {
        return pictorialRepresentation;
    }

    /**
     * Sets pictorial representation.
     *
     * @param pictorialRepresentation the pictorial representation
     */
    public void setPictorialRepresentation(String pictorialRepresentation) {
        this.pictorialRepresentation = pictorialRepresentation;
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
     * Gets informations.
     *
     * @return the informations
     */
    public Set<Information> getInformations() {
        return informations;
    }

    /**
     * Sets informations.
     *
     * @param informations the informations
     */
    public void setInformations(Set<Information> informations) {
        this.informations.clear();
        if (informations != null) {
            this.informations.addAll(informations);
        }
    }

    /**
     * Gets feature names.
     *
     * @return the feature names
     */
    public Set<FeatureName> getFeatureNames() {
        return featureNames;
    }

    /**
     * Sets feature names.
     *
     * @param featureNames the feature names
     */
    public void setFeatureNames(Set<FeatureName> featureNames) {
        this.featureNames.clear();
        if (featureNames != null) {
            this.featureNames.addAll(featureNames);
        }
    }

    /**
     * Gets aggregations.
     *
     * @return the aggregations
     */
    public Set<Aggregation> getAggregations() {
        return aggregations;
    }

    /**
     * Sets aggregations.
     *
     * @param aggregations the aggregations
     */
    public void setAggregations(Set<Aggregation> aggregations) {
        this.aggregations.clear();
        if (aggregations != null) {
            this.aggregations.addAll(aggregations);
        }
    }

    /**
     * Gets associations.
     *
     * @return the associations
     */
    public Set<Association> getAssociations() {
        return associations;
    }

    /**
     * Sets associations.
     *
     * @param associations the associations
     */
    public void setAssociations(Set<Association> associations) {
        this.associations.clear();
        if (associations != null) {
            this.associations.addAll(associations);
        }
    }

    /**
     * Gets last modified at.
     *
     * @return the last modified at
     */
    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    /**
     * Sets last modified at.
     *
     * @param lastModifiedAt the last modified at
     */
    public void setLastModifiedAt(LocalDateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }
}
