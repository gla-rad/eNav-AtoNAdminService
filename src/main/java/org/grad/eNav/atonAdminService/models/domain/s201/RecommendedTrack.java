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

import _int.iho.s201.gml.cs0._1.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * The S-201 Recommended Track Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Recommended
 * Tracktype. It is modelled as an entity that extends the
 * {@link AidsToNavigation} super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s201.gml.cs0._1.RecommendedTrack
 */
@Entity
public class RecommendedTrack extends AidsToNavigation {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private CategoryOfRecommendedTrackType categoryOfRecommendedTrack;

    private BigDecimal depthRangeMaximumValue;

    private BigDecimal depthRangeMinimumValue;

    private BigDecimal orientation;

    private QualityOfVerticalMeasurementType qualityOfVerticalMeasurement;

    private BigDecimal soundingAccuracy;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private List<StatusType> statuses;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = TechniqueOfSoundingMeasurementType.class)
    private List<TechniqueOfSoundingMeasurementType> techniqueOfSoundingMeasurements;

    @Enumerated(EnumType.STRING)
    private TrafficFlowType trafficFlow;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "recommended_track_nav_lines",
            joinColumns = { @JoinColumn(name = "recommended_track_id") },
            inverseJoinColumns = { @JoinColumn(name = "navigation_line_id") }
    )
    private List<NavigationLine> navigationLines;

    @Enumerated(EnumType.STRING)
    protected VerticalDatumType verticalDatum;

    /**
     * Gets category of recommended track.
     *
     * @return the category of recommended track
     */
    public CategoryOfRecommendedTrackType getCategoryOfRecommendedTrack() {
        return categoryOfRecommendedTrack;
    }

    /**
     * Sets category of recommended track.
     *
     * @param categoryOfRecommendedTrack the category of recommended track
     */
    public void setCategoryOfRecommendedTrack(CategoryOfRecommendedTrackType categoryOfRecommendedTrack) {
        this.categoryOfRecommendedTrack = categoryOfRecommendedTrack;
    }

    /**
     * Gets depth range maximum value.
     *
     * @return the depth range maximum value
     */
    public BigDecimal getDepthRangeMaximumValue() {
        return depthRangeMaximumValue;
    }

    /**
     * Sets depth range maximum value.
     *
     * @param depthRangeMaximumValue the depth range maximum value
     */
    public void setDepthRangeMaximumValue(BigDecimal depthRangeMaximumValue) {
        this.depthRangeMaximumValue = depthRangeMaximumValue;
    }

    /**
     * Gets depth range minimum value.
     *
     * @return the depth range minimum value
     */
    public BigDecimal getDepthRangeMinimumValue() {
        return depthRangeMinimumValue;
    }

    /**
     * Sets depth range minimum value.
     *
     * @param depthRangeMinimumValue the depth range minimum value
     */
    public void setDepthRangeMinimumValue(BigDecimal depthRangeMinimumValue) {
        this.depthRangeMinimumValue = depthRangeMinimumValue;
    }

    /**
     * Gets orientation.
     *
     * @return the orientation
     */
    public BigDecimal getOrientation() {
        return orientation;
    }

    /**
     * Sets orientation.
     *
     * @param orientation the orientation
     */
    public void setOrientation(BigDecimal orientation) {
        this.orientation = orientation;
    }

    /**
     * Gets quality of vertical measurement.
     *
     * @return the quality of vertical measurement
     */
    public QualityOfVerticalMeasurementType getQualityOfVerticalMeasurement() {
        return qualityOfVerticalMeasurement;
    }

    /**
     * Sets quality of vertical measurement.
     *
     * @param qualityOfVerticalMeasurement the quality of vertical measurement
     */
    public void setQualityOfVerticalMeasurement(QualityOfVerticalMeasurementType qualityOfVerticalMeasurement) {
        this.qualityOfVerticalMeasurement = qualityOfVerticalMeasurement;
    }

    /**
     * Gets sounding accuracy.
     *
     * @return the sounding accuracy
     */
    public BigDecimal getSoundingAccuracy() {
        return soundingAccuracy;
    }

    /**
     * Sets sounding accuracy.
     *
     * @param soundingAccuracy the sounding accuracy
     */
    public void setSoundingAccuracy(BigDecimal soundingAccuracy) {
        this.soundingAccuracy = soundingAccuracy;
    }

    /**
     * Gets statuses.
     *
     * @return the statuses
     */
    public List<StatusType> getStatuses() {
        return statuses;
    }

    /**
     * Sets statuses.
     *
     * @param statuses the statuses
     */
    public void setStatuses(List<StatusType> statuses) {
        this.statuses = statuses;
    }

    /**
     * Gets technique of sounding measurements.
     *
     * @return the technique of sounding measurements
     */
    public List<TechniqueOfSoundingMeasurementType> getTechniqueOfSoundingMeasurements() {
        return techniqueOfSoundingMeasurements;
    }

    /**
     * Sets technique of sounding measurements.
     *
     * @param techniqueOfSoundingMeasurements the technique of sounding measurements
     */
    public void setTechniqueOfSoundingMeasurements(List<TechniqueOfSoundingMeasurementType> techniqueOfSoundingMeasurements) {
        this.techniqueOfSoundingMeasurements = techniqueOfSoundingMeasurements;
    }

    /**
     * Gets traffic flow.
     *
     * @return the traffic flow
     */
    public TrafficFlowType getTrafficFlow() {
        return trafficFlow;
    }

    /**
     * Sets traffic flow.
     *
     * @param trafficFlow the traffic flow
     */
    public void setTrafficFlow(TrafficFlowType trafficFlow) {
        this.trafficFlow = trafficFlow;
    }

    /**
     * Gets navigation lines.
     *
     * @return the navigation lines
     */
    public List<NavigationLine> getNavigationLines() {
        return navigationLines;
    }

    /**
     * Sets navigation lines.
     *
     * @param navigationLines the navigation lines
     */
    public void setNavigationLines(List<NavigationLine> navigationLines) {
        this.navigationLines = navigationLines;
    }

    /**
     * Gets vertical datum.
     *
     * @return the vertical datum
     */
    public VerticalDatumType getVerticalDatum() {
        return verticalDatum;
    }

    /**
     * Sets vertical datum.
     *
     * @param verticalDatum the vertical datum
     */
    public void setVerticalDatum(VerticalDatumType verticalDatum) {
        this.verticalDatum = verticalDatum;
    }
}
