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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.grad.eNav.atonAdminService.utils.GeometryJSONDeserializer;
import org.grad.eNav.atonAdminService.utils.GeometryJSONSerializer;
import org.locationtech.jts.geom.Geometry;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * The S-201 Aids to Navigation DTO Entity Class.
 * <p>
 * This is the basic class for transmitting the S-201 Aids to Navigation data
 * onto third parties. This is going to be encoded as a JSON object and it
 * does not contain AtoN type specific information, just the basics for
 * identifying an AtoN, so the common fields.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see org.grad.eNav.atonAdminService.models.domain.s201.AidsToNavigation
 */
public class AidsToNavigationDto {

    // Class Variables
    private BigInteger id;

    private String idCode;
    
    private LocalDate dateEnd;

    private LocalDate dateStart;

    private LocalDate periodEnd;

    private LocalDate periodStart;

    private LocalDate sourceDate;

    private String sourceIndication;

    private String pictorialRepresentation;

    private String inspectionFrequency;

    private String inspectionRequirements;

    private String atonMaintenanceRecord;

    private LocalDate installationDate;

    private BigInteger scaleMinimum;

    @JsonSerialize(using = GeometryJSONSerializer.class)
    @JsonDeserialize(using = GeometryJSONDeserializer.class)
    private Geometry geometry;

    private Set<InformationDto> informations;

    private Set<FeatureNameDto> featureNames;

    private List<String> seasonalActionRequireds;

    private String atonType;

    private BigInteger mmsiCode;

    private String content;

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
     * Gets source date.
     *
     * @return the source date
     */
    public LocalDate getSourceDate() {
        return sourceDate;
    }

    /**
     * Sets source date.
     *
     * @param sourceDate the source date
     */
    public void setSourceDate(LocalDate sourceDate) {
        this.sourceDate = sourceDate;
    }

    /**
     * Gets source indication.
     *
     * @return the source indication
     */
    public String getSourceIndication() {
        return sourceIndication;
    }

    /**
     * Sets source indication.
     *
     * @param sourceIndication the source indication
     */
    public void setSourceIndication(String sourceIndication) {
        this.sourceIndication = sourceIndication;
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
     * Gets inspection frequency.
     *
     * @return the inspection frequency
     */
    public String getInspectionFrequency() {
        return inspectionFrequency;
    }

    /**
     * Sets inspection frequency.
     *
     * @param inspectionFrequency the inspection frequency
     */
    public void setInspectionFrequency(String inspectionFrequency) {
        this.inspectionFrequency = inspectionFrequency;
    }

    /**
     * Gets inspection requirements.
     *
     * @return the inspection requirements
     */
    public String getInspectionRequirements() {
        return inspectionRequirements;
    }

    /**
     * Sets inspection requirements.
     *
     * @param inspectionRequirements the inspection requirements
     */
    public void setInspectionRequirements(String inspectionRequirements) {
        this.inspectionRequirements = inspectionRequirements;
    }

    /**
     * Gets aton maintenance record.
     *
     * @return the aton maintenance record
     */
    public String getAtonMaintenanceRecord() {
        return atonMaintenanceRecord;
    }

    /**
     * Sets aton maintenance record.
     *
     * @param atonMaintenanceRecord the aton maintenance record
     */
    public void setAtonMaintenanceRecord(String atonMaintenanceRecord) {
        this.atonMaintenanceRecord = atonMaintenanceRecord;
    }

    /**
     * Gets installation date.
     *
     * @return the installation date
     */
    public LocalDate getInstallationDate() {
        return installationDate;
    }

    /**
     * Sets installation date.
     *
     * @param installationDate the installation date
     */
    public void setInstallationDate(LocalDate installationDate) {
        this.installationDate = installationDate;
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
    public Set<InformationDto> getInformations() {
        return informations;
    }

    /**
     * Sets informations.
     *
     * @param informations the informations
     */
    public void setInformations(Set<InformationDto> informations) {
        this.informations = informations;
    }

    /**
     * Gets feature names.
     *
     * @return the feature names
     */
    public Set<FeatureNameDto> getFeatureNames() {
        return featureNames;
    }

    /**
     * Sets feature names.
     *
     * @param featureNames the feature names
     */
    public void setFeatureNames(Set<FeatureNameDto> featureNames) {
        this.featureNames = featureNames;
    }

    /**
     * Gets seasonal action requireds.
     *
     * @return the seasonal action requireds
     */
    public List<String> getSeasonalActionRequireds() {
        return seasonalActionRequireds;
    }

    /**
     * Sets seasonal action requireds.
     *
     * @param seasonalActionRequireds the seasonal action requireds
     */
    public void setSeasonalActionRequireds(List<String> seasonalActionRequireds) {
        this.seasonalActionRequireds = seasonalActionRequireds;
    }

    /**
     * Gets aton type.
     *
     * @return the aton type
     */
    public String getAtonType() {
        return atonType;
    }

    /**
     * Sets aton type.
     *
     * @param atonType the aton type
     */
    public void setAtonType(String atonType) {
        this.atonType = atonType;
    }

    /**
     * Gets mmsi code.
     *
     * @return the mmsi code
     */
    public BigInteger getMmsiCode() {
        return mmsiCode;
    }

    /**
     * Sets mmsi code.
     *
     * @param mmsiCode the mmsi code
     */
    public void setMmsiCode(BigInteger mmsiCode) {
        this.mmsiCode = mmsiCode;
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
}
