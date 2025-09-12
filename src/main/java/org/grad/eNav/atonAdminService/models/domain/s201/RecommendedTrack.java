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

import _int.iho.s_201.gml.cs0._2.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * The S-201 Recommended Track Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Recommended
 * Tracktype. It is modelled as an entity that extends the
 * {@link AidsToNavigation} super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.RecommendedTrack
 */
@Entity
public class RecommendedTrack extends AidsToNavigation {

    // Class Variables
    private Boolean basedOnFixedMarks;

    private BigDecimal depthRangeMinimumValue;

    private BigDecimal maximumPermittedDraught;

    @Enumerated(EnumType.STRING)
    private VerticalDatumType verticalDatum;

    private Orientation orientation;

    private VerticalUncertainty verticalUncertainty;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = QualityOfVerticalMeasurementType.class)
    private Set<QualityOfVerticalMeasurementType> qualityOfVerticalMeasurement;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = TechniqueOfVerticalMeasurementType.class)
    private Set<TechniqueOfVerticalMeasurementType> techniqueOfVerticalMeasurements;

    @Enumerated(EnumType.STRING)
    private TrafficFlowType trafficFlow;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private Set<StatusType> statuses;

    /**
     * The Navigation lines.
     */
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "recommended_track_nav_lines",
            joinColumns = {@JoinColumn(name = "recommended_track_id")},
            inverseJoinColumns = {@JoinColumn(name = "navigation_line_id")}
    )
    private final Set<NavigationLine> navigationLines = new HashSet<>();

    /**
     * Gets based on fixed marks.
     *
     * @return the based on fixed marks
     */
    public Boolean getBasedOnFixedMarks() {
        return basedOnFixedMarks;
    }

    /**
     * Sets based on fixed marks.
     *
     * @param basedOnFixedMarks the based on fixed marks
     */
    public void setBasedOnFixedMarks(Boolean basedOnFixedMarks) {
        this.basedOnFixedMarks = basedOnFixedMarks;
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
     * Gets maximum permitted draught.
     *
     * @return the maximum permitted draught
     */
    public BigDecimal getMaximumPermittedDraught() {
        return maximumPermittedDraught;
    }

    /**
     * Sets maximum permitted draught.
     *
     * @param maximumPermittedDraught the maximum permitted draught
     */
    public void setMaximumPermittedDraught(BigDecimal maximumPermittedDraught) {
        this.maximumPermittedDraught = maximumPermittedDraught;
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

    /**
     * Gets orientation.
     *
     * @return the orientation
     */
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * Sets orientation.
     *
     * @param orientation the orientation
     */
    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    /**
     * Gets vertical uncertainty.
     *
     * @return the vertical uncertainty
     */
    public VerticalUncertainty getVerticalUncertainty() {
        return verticalUncertainty;
    }

    /**
     * Sets vertical uncertainty.
     *
     * @param verticalUncertainty the vertical uncertainty
     */
    public void setVerticalUncertainty(VerticalUncertainty verticalUncertainty) {
        this.verticalUncertainty = verticalUncertainty;
    }

    /**
     * Gets quality of vertical measurement.
     *
     * @return the quality of vertical measurement
     */
    public Set<QualityOfVerticalMeasurementType> getQualityOfVerticalMeasurement() {
        return qualityOfVerticalMeasurement;
    }

    /**
     * Sets quality of vertical measurement.
     *
     * @param qualityOfVerticalMeasurement the quality of vertical measurement
     */
    public void setQualityOfVerticalMeasurement(Set<QualityOfVerticalMeasurementType> qualityOfVerticalMeasurement) {
        this.qualityOfVerticalMeasurement = qualityOfVerticalMeasurement;
    }

    /**
     * Gets technique of vertical measurements.
     *
     * @return the technique of vertical measurements
     */
    public Set<TechniqueOfVerticalMeasurementType> getTechniqueOfVerticalMeasurements() {
        return techniqueOfVerticalMeasurements;
    }

    /**
     * Sets technique of vertical measurements.
     *
     * @param techniqueOfVerticalMeasurements the technique of vertical measurements
     */
    public void setTechniqueOfVerticalMeasurements(Set<TechniqueOfVerticalMeasurementType> techniqueOfVerticalMeasurements) {
        this.techniqueOfVerticalMeasurements = techniqueOfVerticalMeasurements;
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
     * Gets statuses.
     *
     * @return the statuses
     */
    public Set<StatusType> getStatuses() {
        return statuses;
    }

    /**
     * Sets statuses.
     *
     * @param statuses the statuses
     */
    public void setStatuses(Set<StatusType> statuses) {
        this.statuses = statuses;
    }

    /**
     * Gets navigation lines.
     *
     * @return the navigation lines
     */
    public Set<NavigationLine> getNavigationLines() {
        return navigationLines;
    }

    /**
     * Sets navigation lines.
     *
     * @param navigationLines the navigation lines
     */
    public void setNavigationLines(Set<NavigationLine> navigationLines) {
        this.navigationLines.clear();
        if (navigationLines != null) {
            // Set the parent correctly
            navigationLines.forEach(fn -> fn.getNavigableTracks().add(this));
            // And update the associations
            this.navigationLines.addAll(navigationLines);
        }
    }
}
