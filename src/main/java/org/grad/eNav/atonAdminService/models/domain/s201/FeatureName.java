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

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * The S-201 Feature Name Entity Class.
 * <p/>
 * This class implements the FeatureName type of the S-201 Aids to Navigation
 * objects which includes a description of the entity, as well as the language
 * code and whether this should be selected for the final UI display.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s201.gml.cs0._1.FeatureNameType
 */
@Entity
public class FeatureName implements Serializable  {

    // Class Variables
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feature_name_generator")
    @SequenceGenerator(name="feature_name_generator", sequenceName = "feature_name_seq", allocationSize=1)
    private BigInteger id;
    private String name;
    private String language;
    private Boolean displayName;

    @JsonBackReference
    @ManyToOne
    private AidsToNavigation feature;

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
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets language.
     *
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets language.
     *
     * @param language the language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Gets display name.
     *
     * @return the display name
     */
    public Boolean getDisplayName() {
        return displayName;
    }

    /**
     * Sets display name.
     *
     * @param displayName the display name
     */
    public void setDisplayName(Boolean displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets feature.
     *
     * @return the feature
     */
    public AidsToNavigation getFeature() {
        return feature;
    }

    /**
     * Sets feature.
     *
     * @param feature the feature
     */
    public void setFeature(AidsToNavigation feature) {
        this.feature = feature;
    }
}
