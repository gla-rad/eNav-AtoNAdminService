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

import java.math.BigInteger;

/**
 * The S-201 Information Entity Class.
 * <p/>
 * This class implements the Information type of the S-201 Aids to Navigation
 * objects which includes textual information like file references, headlines
 * and simple text (along with the respective language).
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.InformationType
 */
@Entity
public class Information {

    // Class Variables
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "information_generator")
    @SequenceGenerator(name="information_generator", sequenceName = "information_seq", allocationSize=1)
    private BigInteger id;

    private String fileLocator;

    private String fileReference;

    private String headline;

    private String language;

    private String text;

    @JsonBackReference
    @ManyToOne
    private AidsToNavigation feature;

    @JsonBackReference
    @ManyToOne
    private DangerousFeature dangerousFeature;

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
     * Gets file locator.
     *
     * @return the file locator
     */
    public String getFileLocator() {
        return fileLocator;
    }

    /**
     * Sets file locator.
     *
     * @param fileLocator the file locator
     */
    public void setFileLocator(String fileLocator) {
        this.fileLocator = fileLocator;
    }

    /**
     * Gets file reference.
     *
     * @return the file reference
     */
    public String getFileReference() {
        return fileReference;
    }

    /**
     * Sets file reference.
     *
     * @param fileReference the file reference
     */
    public void setFileReference(String fileReference) {
        this.fileReference = fileReference;
    }

    /**
     * Gets headline.
     *
     * @return the headline
     */
    public String getHeadline() {
        return headline;
    }

    /**
     * Sets headline.
     *
     * @param headline the headline
     */
    public void setHeadline(String headline) {
        this.headline = headline;
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
     * Gets text.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets text.
     *
     * @param text the text
     */
    public void setText(String text) {
        this.text = text;
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

    /**
     * Gets dangerous feature.
     *
     * @return the dangerous feature
     */
    public DangerousFeature getDangerousFeature() {
        return dangerousFeature;
    }

    /**
     * Sets dangerous feature.
     *
     * @param dangerousFeature the dangerous feature
     */
    public void setDangerousFeature(DangerousFeature dangerousFeature) {
        this.dangerousFeature = dangerousFeature;
    }
}
