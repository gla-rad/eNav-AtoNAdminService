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
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * The S-201 Generic Buoy Entity Class.
 *
 * This is the basic class for implementing the S-201-compatible Generic Buoy
 * type. It is modelled as an entity class on hibernate, but it is abstract so
 * that we can extend this for each Buoy type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.GenericBuoyType
 */
@Entity
public abstract class GenericBuoy extends StructureObject {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private BuoyShapeType buoyShape;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ColourType.class)
    private Set<ColourType> colours;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ColourPatternType.class)
    private Set<ColourPatternType> colourPatterns;

    @Enumerated(EnumType.STRING)
    private MarksNavigationalSystemOfType marksNavigationalSystemOf;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = NatureOfConstructionType.class)
    private Set<NatureOfConstructionType> natureOfConstructions;

    private Boolean radarConspicuous;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private Set<StatusType> statuses;

    private String typeOfBuoy;

    private BigDecimal verticalLength;

    private BigDecimal verticalAccuracy;

    @JsonManagedReference
    @OneToMany(mappedBy = "buoyPart", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<Topmark> topmarkParts = new HashSet<>();

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL)
    private MooringShackle shackleToBuoyConnected;

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL)
    private Bridle buoyHangs;

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL)
    private CounterWeight buoyAttached;

    /**
     * Gets buoy shape.
     *
     * @return the buoy shape
     */
    public BuoyShapeType getBuoyShape() {
        return buoyShape;
    }

    /**
     * Sets buoy shape.
     *
     * @param buoyShape the buoy shape
     */
    public void setBuoyShape(BuoyShapeType buoyShape) {
        this.buoyShape = buoyShape;
    }

    /**
     * Gets colours.
     *
     * @return the colours
     */
    public Set<ColourType> getColours() {
        return colours;
    }

    /**
     * Sets colours.
     *
     * @param colours the colours
     */
    public void setColours(Set<ColourType> colours) {
        this.colours = colours;
    }

    /**
     * Gets colour patterns.
     *
     * @return the colour patterns
     */
    public Set<ColourPatternType> getColourPatterns() {
        return colourPatterns;
    }

    /**
     * Sets colour patterns.
     *
     * @param colourPatterns the colour patterns
     */
    public void setColourPatterns(Set<ColourPatternType> colourPatterns) {
        this.colourPatterns = colourPatterns;
    }

    /**
     * Gets marks navigational system of.
     *
     * @return the marks navigational system of
     */
    public MarksNavigationalSystemOfType getMarksNavigationalSystemOf() {
        return marksNavigationalSystemOf;
    }

    /**
     * Sets marks navigational system of.
     *
     * @param marksNavigationalSystemOf the marks navigational system of
     */
    public void setMarksNavigationalSystemOf(MarksNavigationalSystemOfType marksNavigationalSystemOf) {
        this.marksNavigationalSystemOf = marksNavigationalSystemOf;
    }

    /**
     * Gets nature of Constructions.
     *
     * @return the nature of Constructions
     */
    public Set<NatureOfConstructionType> getNatureOfConstructions() {
        return natureOfConstructions;
    }

    /**
     * Sets nature of Constructions.
     *
     * @param natureOfConstructions the nature of Constructions
     */
    public void setNatureOfConstructions(Set<NatureOfConstructionType> natureOfConstructions) {
        this.natureOfConstructions = natureOfConstructions;
    }

    /**
     * Gets radar conspicuous.
     *
     * @return the radar conspicuous
     */
    public Boolean getRadarConspicuous() {
        return radarConspicuous;
    }

    /**
     * Sets radar conspicuous.
     *
     * @param radarConspicuous the radar conspicuous
     */
    public void setRadarConspicuous(Boolean radarConspicuous) {
        this.radarConspicuous = radarConspicuous;
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
     * Gets type of buoy.
     *
     * @return the type of buoy
     */
    public String getTypeOfBuoy() {
        return typeOfBuoy;
    }

    /**
     * Sets type of buoy.
     *
     * @param typeOfBuoy the type of buoy
     */
    public void setTypeOfBuoy(String typeOfBuoy) {
        this.typeOfBuoy = typeOfBuoy;
    }

    /**
     * Gets vertical length.
     *
     * @return the vertical length
     */
    public BigDecimal getVerticalLength() {
        return verticalLength;
    }

    /**
     * Sets vertical length.
     *
     * @param verticalLength the vertical length
     */
    public void setVerticalLength(BigDecimal verticalLength) {
        this.verticalLength = verticalLength;
    }

    /**
     * Gets vertical accuracy.
     *
     * @return the vertical accuracy
     */
    public BigDecimal getVerticalAccuracy() {
        return verticalAccuracy;
    }

    /**
     * Sets vertical accuracy.
     *
     * @param verticalAccuracy the vertical accuracy
     */
    public void setVerticalAccuracy(BigDecimal verticalAccuracy) {
        this.verticalAccuracy = verticalAccuracy;
    }

    /**
     * Gets topmark parts.
     *
     * @return the topmark parts
     */
    public Set<Topmark> getTopmarkParts() {
        return topmarkParts;
    }

    /**
     * Sets topmark parts.
     *
     * @param topmarkParts the topmark parts
     */
    public void setTopmarkParts(Set<Topmark> topmarkParts) {
        if(topmarkParts != null) {
            topmarkParts.forEach(topmark -> topmark.setBuoyPart(this));
        }
        this.topmarkParts.clear();
        this.topmarkParts.addAll(topmarkParts);
    }

    /**
     * Gets shackle to buoy connected.
     *
     * @return the shackle to buoy connected
     */
    public MooringShackle getShackleToBuoyConnected() {
        return shackleToBuoyConnected;
    }

    /**
     * Sets shackle to buoy connected.
     *
     * @param shackleToBuoyConnected the shackle to buoy connected
     */
    public void setShackleToBuoyConnected(MooringShackle shackleToBuoyConnected) {
        this.shackleToBuoyConnected = shackleToBuoyConnected;
    }

    /**
     * Gets buoy hangs.
     *
     * @return the buoy hangs
     */
    public Bridle getBuoyHangs() {
        return buoyHangs;
    }

    /**
     * Sets buoy hangs.
     *
     * @param buoyHangs the buoy hangs
     */
    public void setBuoyHangs(Bridle buoyHangs) {
        this.buoyHangs = buoyHangs;
    }

    /**
     * Gets buoy attached.
     *
     * @return the buoy attached
     */
    public CounterWeight getBuoyAttached() {
        return buoyAttached;
    }

    /**
     * Sets buoy attached.
     *
     * @param buoyAttached the buoy attached
     */
    public void setBuoyAttached(CounterWeight buoyAttached) {
        this.buoyAttached = buoyAttached;
    }
}
