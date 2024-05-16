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

package org.grad.eNav.atonAdminService.models.dtos.s201;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.grad.eNav.atonAdminService.models.domain.s201.S201DatasetIdentification;

import java.math.BigInteger;
import java.time.LocalDate;

/**
 * The Dataset Identification DTO Class
 * <p>
 * This is the basic class for transmitting the S-201 Dataset Identification
 * Information onto third parties. This is going to be encoded as a JSON object,
 * and it does contain all the fields of the locally persisted class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see S201DatasetIdentification
 */
public class S201DataSetIdentificationDto {

    // Class Variables
    private BigInteger id;

    private String encodingSpecification;

    private String encodingSpecificationEdition;

    private String productIdentifier;

    private String productEdition;

    private String applicationProfile;

    private String datasetFileIdentifier;

    private String datasetTitle;

    @JsonFormat(pattern="yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate datasetReferenceDate;

    private String datasetLanguage;

    private String datasetAbstract;

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
     * Gets encoding specification.
     *
     * @return the encoding specification
     */
    public String getEncodingSpecification() {
        return encodingSpecification;
    }

    /**
     * Sets encoding specification.
     *
     * @param encodingSpecification the encoding specification
     */
    public void setEncodingSpecification(String encodingSpecification) {
        this.encodingSpecification = encodingSpecification;
    }

    /**
     * Gets encoding specification edition.
     *
     * @return the encoding specification edition
     */
    public String getEncodingSpecificationEdition() {
        return encodingSpecificationEdition;
    }

    /**
     * Sets encoding specification edition.
     *
     * @param encodingSpecificationEdition the encoding specification edition
     */
    public void setEncodingSpecificationEdition(String encodingSpecificationEdition) {
        this.encodingSpecificationEdition = encodingSpecificationEdition;
    }

    /**
     * Gets product identifier.
     *
     * @return the product identifier
     */
    public String getProductIdentifier() {
        return productIdentifier;
    }

    /**
     * Sets product identifier.
     *
     * @param productIdentifier the product identifier
     */
    public void setProductIdentifier(String productIdentifier) {
        this.productIdentifier = productIdentifier;
    }

    /**
     * Gets product edition.
     *
     * @return the product edition
     */
    public String getProductEdition() {
        return productEdition;
    }

    /**
     * Sets product edition.
     *
     * @param productEdition the product edition
     */
    public void setProductEdition(String productEdition) {
        this.productEdition = productEdition;
    }

    /**
     * Gets application profile.
     *
     * @return the application profile
     */
    public String getApplicationProfile() {
        return applicationProfile;
    }

    /**
     * Sets application profile.
     *
     * @param applicationProfile the application profile
     */
    public void setApplicationProfile(String applicationProfile) {
        this.applicationProfile = applicationProfile;
    }

    /**
     * Gets dataset file identifier.
     *
     * @return the dataset file identifier
     */
    public String getDatasetFileIdentifier() {
        return datasetFileIdentifier;
    }

    /**
     * Sets dataset file identifier.
     *
     * @param datasetFileIdentifier the dataset file identifier
     */
    public void setDatasetFileIdentifier(String datasetFileIdentifier) {
        this.datasetFileIdentifier = datasetFileIdentifier;
    }

    /**
     * Gets dataset title.
     *
     * @return the dataset title
     */
    public String getDatasetTitle() {
        return datasetTitle;
    }

    /**
     * Sets dataset title.
     *
     * @param datasetTitle the dataset title
     */
    public void setDatasetTitle(String datasetTitle) {
        this.datasetTitle = datasetTitle;
    }

    /**
     * Gets dataset reference date.
     *
     * @return the dataset reference date
     */
    public LocalDate getDatasetReferenceDate() {
        return datasetReferenceDate;
    }

    /**
     * Sets dataset reference date.
     *
     * @param datasetReferenceDate the dataset reference date
     */
    public void setDatasetReferenceDate(LocalDate datasetReferenceDate) {
        this.datasetReferenceDate = datasetReferenceDate;
    }

    /**
     * Gets dataset language.
     *
     * @return the dataset language
     */
    public String getDatasetLanguage() {
        return datasetLanguage;
    }

    /**
     * Sets dataset language.
     *
     * @param datasetLanguage the dataset language
     */
    public void setDatasetLanguage(String datasetLanguage) {
        this.datasetLanguage = datasetLanguage;
    }

    /**
     * Gets dataset abstract.
     *
     * @return the dataset abstract
     */
    public String getDatasetAbstract() {
        return datasetAbstract;
    }

    /**
     * Sets dataset abstract.
     *
     * @param datasetAbstract the dataset abstract
     */
    public void setDatasetAbstract(String datasetAbstract) {
        this.datasetAbstract = datasetAbstract;
    }
}
