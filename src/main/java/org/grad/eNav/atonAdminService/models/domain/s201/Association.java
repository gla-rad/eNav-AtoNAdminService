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

import _int.iho.s201.gml.cs0._1.CategoryOfAssociationType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

/**
 * The S-201 Association Entity Class
 * <p/>
 * This class implements the {@link _int.iho.s201.gml.cs0._1.Association}
 * objects of the S-201 data product. These can be used to group multiple
 * Aids to Navigation into a single association with a give type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s201.gml.cs0._1.Association
 */
@Entity
public class Association implements Serializable {

    // Class Variables
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "association_generator")
    @SequenceGenerator(name="association_generator", sequenceName = "association_seq", allocationSize=1)
    private BigInteger id;

    @Enumerated(EnumType.STRING)
    private CategoryOfAssociationType associationType;

    @JsonBackReference
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "association_join_table",
            joinColumns = { @JoinColumn(name = "association_id") },
            inverseJoinColumns = { @JoinColumn(name = "aton_id") }
    )
    final private Set<AidsToNavigation> peers = new HashSet<>();

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
     * Gets association type.
     *
     * @return the association type
     */
    public CategoryOfAssociationType getAssociationType() {
        return associationType;
    }

    /**
     * Sets association type.
     *
     * @param associationType the association type
     */
    public void setAssociationType(CategoryOfAssociationType associationType) {
        this.associationType = associationType;
    }

    /**
     * Gets peers.
     *
     * @return the peers
     */
    public Set<AidsToNavigation> getPeers() {
        return peers;
    }

    /**
     * Sets peers.
     *
     * @param peers the peers
     */
    public void setPeers(Set<AidsToNavigation> peers) {
        this.peers.clear();
        if (peers!= null) {
            this.getPeers().addAll(peers);
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
        if (!(o instanceof Association that)) return false;
        return associationType == that.associationType
                && Objects.equals(this.getPeers().size(), that.getPeers().size())
                && new HashSet<>(this.getPeerIdCodes()).containsAll(that.getPeerIdCodes());
    }

    /**
     * Overrides the hashcode generation of the object.
     *
     * @return the generated hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                associationType,
                Arrays.hashCode(this.getPeerIdCodes().toArray())
        );
    }

    /**
     * Returns a list of all the peer AtoN ID Codes included in the association.
     *
     * @return a list of all the peer AtoN ID Codes included in the association
     */
    @JsonIgnore
    public List<String> getPeerIdCodes() {
        return this.getPeers()
                .stream()
                .map(AidsToNavigation::getIdCode)
                .sorted()
                .toList();
    }
}
