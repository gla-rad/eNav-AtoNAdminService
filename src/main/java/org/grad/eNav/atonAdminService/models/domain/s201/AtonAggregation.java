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

import _int.iho.s_201.gml.cs0._2.CategoryOfAggregationType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The S-201 Aggregation Entity Class
 * <p/>
 * This class implements the {@link _int.iho.s_201.gml.cs0._2.AtonAggregation}
 * objects of the S-201 data product. These can be used to group multiple
 * Aids to Navigation into a single aggregation with a give type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.AtonAggregation
 */
@Entity
public class AtonAggregation implements Serializable {

    // Class Variables
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aggregation_generator")
    @SequenceGenerator(name="aggregation_generator", sequenceName = "aggregation_seq", allocationSize=1)
    private BigInteger id;

    @Enumerated(EnumType.STRING)
    private CategoryOfAggregationType categoryOfAggregationType;

    @JsonBackReference
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "aggregation_join_table",
            joinColumns = { @JoinColumn(name = "aggregation_id") },
            inverseJoinColumns = { @JoinColumn(name = "aton_id") }
    )
    final private Set<AidsToNavigation> atonAggregationBies = new HashSet<>();

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
     * Gets category of aggregation type.
     *
     * @return the category of aggregation type
     */
    public CategoryOfAggregationType getCategoryOfAggregationType() {
        return categoryOfAggregationType;
    }

    /**
     * Sets category of aggregation type.
     *
     * @param categoryOfAggregationType the category of aggregation type
     */
    public void setCategoryOfAggregationType(CategoryOfAggregationType categoryOfAggregationType) {
        this.categoryOfAggregationType = categoryOfAggregationType;
    }

    /**
     * Gets aton aggregation by entries.
     *
     * @return the aton aggregation by entries
     */
    public Set<AidsToNavigation> getAtonAggregationBies() {
        return atonAggregationBies;
    }

    /**
     * Sets aton aggregation by entries.
     *
     * @param atonAggregationBies the aton aggregation by entries
     */
    public void setAtonAggregationBies(Set<AidsToNavigation> atonAggregationBies) {
        this.atonAggregationBies.clear();
        if(atonAggregationBies != null) {
            atonAggregationBies.forEach(peer -> peer.getPeerAtonAggregations().add(this));
            this.atonAggregationBies.addAll(atonAggregationBies);
        }
    }

    /**
     * Overrides the equality operator of the class.
     *
     * @param o the object to check the equality
     * @return whether the two objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AtonAggregation that)) return false;
        return categoryOfAggregationType == that.categoryOfAggregationType
                && Objects.equals(this.getAtonAggregationBies().size(), that.getAtonAggregationBies().size())
                && new HashSet<>(this.getPeerIDCodes()).containsAll(that.getPeerIDCodes());
    }

    /**
     * Overrides the hashcode generation of the object.
     *
     * @return the generated hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                categoryOfAggregationType,
                Arrays.hashCode(this.getPeerIDCodes().toArray())
        );
    }

    /**
     * Returns a set of all the peer AtoN ID Codes included in the aggregation.
     *
     * @return a set of all the peer AtoN ID Codes included in the aggregation
     */
    @JsonIgnore
    public Set<String> getPeerIDCodes() {
        return this.getAtonAggregationBies()
                .stream()
                .map(AidsToNavigation::getIdCode)
                .collect(Collectors.toSet());
    }
}
