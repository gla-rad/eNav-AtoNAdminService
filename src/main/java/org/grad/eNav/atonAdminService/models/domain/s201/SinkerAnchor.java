package org.grad.eNav.atonAdminService.models.domain.s201;

import _int.iho.s_201.gml.cs0._2.NatureOfConstructionType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * The S-201 Sicker Anchor Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Sicker Anchor
 * type. It is modelled as an entity that extends the {@link AidsToNavigation}
 * super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.SinkerAnchor
 */
@Entity
public class SinkerAnchor extends AidsToNavigation {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private NatureOfConstructionType natureOfConstruction;

    private SinkerDimensions sinkerDimensions;

    private BigDecimal weight;

    private String sinkerType;

    @JsonBackReference
    @OneToOne
    private MooringShackle shackleToAnchorConnected;

    /**
     * Gets nature of construction.
     *
     * @return the nature of construction
     */
    public NatureOfConstructionType getNatureOfConstruction() {
        return natureOfConstruction;
    }

    /**
     * Sets nature of construction.
     *
     * @param natureOfConstruction the nature of construction
     */
    public void setNatureOfConstruction(NatureOfConstructionType natureOfConstruction) {
        this.natureOfConstruction = natureOfConstruction;
    }

    /**
     * Gets sinker dimensions.
     *
     * @return the sinker dimensions
     */
    public SinkerDimensions getSinkerDimensions() {
        return sinkerDimensions;
    }

    /**
     * Sets sinker dimensions.
     *
     * @param sinkerDimensions the sinker dimensions
     */
    public void setSinkerDimensions(SinkerDimensions sinkerDimensions) {
        this.sinkerDimensions = sinkerDimensions;
    }

    /**
     * Gets weight.
     *
     * @return the weight
     */
    public BigDecimal getWeight() {
        return weight;
    }

    /**
     * Sets weight.
     *
     * @param weight the weight
     */
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    /**
     * Gets sinker type.
     *
     * @return the sinker type
     */
    public String getSinkerType() {
        return sinkerType;
    }

    /**
     * Sets sinker type.
     *
     * @param sinkerType the sinker type
     */
    public void setSinkerType(String sinkerType) {
        this.sinkerType = sinkerType;
    }

    /**
     * Gets shackle to anchor connected.
     *
     * @return the shackle to anchor connected
     */
    public MooringShackle getShackleToAnchorConnected() {
        return shackleToAnchorConnected;
    }

    /**
     * Sets shackle to anchor connected.
     *
     * @param shackleToAnchorConnected the shackle to anchor connected
     */
    public void setShackleToAnchorConnected(MooringShackle shackleToAnchorConnected) {
        this.shackleToAnchorConnected = shackleToAnchorConnected;
    }
}
