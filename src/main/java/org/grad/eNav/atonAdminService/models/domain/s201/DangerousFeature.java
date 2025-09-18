/*
 * Copyright (c) 2025 GLA Research and Development Directorate
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
import org.grad.eNav.atonAdminService.utils.GeometryBinder;
import org.grad.eNav.atonAdminService.utils.GeometryJSONDeserializer;
import org.grad.eNav.atonAdminService.utils.GeometryJSONSerializer;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBinderRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.NonStandardField;
import org.locationtech.jts.geom.Geometry;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * The S-201 Dangerous Feature Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Dangerous
 * Feature type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.DangerousFeature
 */
@Entity
public class DangerousFeature implements Serializable {

    // Class Variables
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dangerous_feature_generator")
    @SequenceGenerator(name="dangerous_feature_generator", sequenceName = "dangerous_feature_seq", allocationSize=1)
    private BigInteger id;

    @KeywordField(sortable = Sortable.YES)
    @Column(unique=true)
    private String interoperabilityIdentifier;

    @JsonManagedReference
    @OneToMany(mappedBy = "dangerousFeature", cascade = CascadeType.ALL, orphanRemoval = true)
    final private Set<Information> informations = new HashSet<>();

    @JsonManagedReference
    @ManyToMany(mappedBy = "dangers")
    final private Set<AtonAssociation> markingAtons = new HashSet<>();

    @JsonSerialize(using = GeometryJSONSerializer.class)
    @JsonDeserialize(using = GeometryJSONDeserializer.class)
    @NonStandardField(name="geometry", valueBinder = @ValueBinderRef(type = GeometryBinder.class))
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
     * Gets interoperability identifier.
     *
     * @return the interoperability identifier
     */
    public String getInteroperabilityIdentifier() {
        return interoperabilityIdentifier;
    }

    /**
     * Sets interoperability identifier.
     *
     * @param interoperabilityIdentifier the interoperability identifier
     */
    public void setInteroperabilityIdentifier(String interoperabilityIdentifier) {
        this.interoperabilityIdentifier = interoperabilityIdentifier;
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
     * Gets marking atons.
     *
     * @return the marking atons
     */
    public Set<AtonAssociation> getMarkingAtons() {
        return markingAtons;
    }

    /**
     * Sets marking atons.
     *
     * @param markingAtons the marking atons
     */
    public void setMarkingAtons(Set<AtonAssociation> markingAtons) {
        this.markingAtons.clear();
        if(markingAtons != null) {
            markingAtons.forEach(peer -> peer.getDangers().add(this));
            this.markingAtons.addAll(markingAtons);
        }
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
