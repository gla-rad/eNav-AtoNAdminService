package org.grad.eNav.atonAdminService.models.domain.s201;

import _int.iho.s_201.gml.cs0._2.NatureOfConstructionType;
import _int.iho.s_201.gml.cs0._2.ShackleTypeType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * The S-201 Mooring Shackle Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Mooring Shackle
 * type. It is modelled as an entity that extends the {@link AidsToNavigation}
 * super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.MooringShackle
 */
@Entity
public class MooringShackle extends AidsToNavigation {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private NatureOfConstructionType natureOfConstruction;

    @Enumerated(EnumType.STRING)
    private ShackleTypeType shackleType;

    private BigDecimal weight;

    @JsonBackReference
    @OneToOne(mappedBy = "shackleToBuoyConnected")
    private GenericBuoy shackleToBuoyConnectedTo;

    @JsonBackReference
    @OneToOne(mappedBy = "shackleToBridleConnected", cascade = CascadeType.ALL, orphanRemoval = true)
    private Bridle shackleToBridleConnectedTo;

    @JsonBackReference
    @OneToOne(mappedBy = "shackleToAnchorConnected", cascade = CascadeType.ALL, orphanRemoval = true)
    private SinkerAnchor shackleToAnchorConnectedTo;

    @JsonManagedReference
    @OneToMany(mappedBy = "shackleToCableConnected", cascade = CascadeType.ALL, orphanRemoval = true)
    final private Set<CableSubmarine> shackleToCableConnectedTo = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "shackleToSwivelConnected", cascade = CascadeType.ALL, orphanRemoval = true)
    final private Set<Swivel> shackleToSwivelConnectedTo = new HashSet<>();

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
     * Gets shackle type.
     *
     * @return the shackle type
     */
    public ShackleTypeType getShackleType() {
        return shackleType;
    }

    /**
     * Sets shackle type.
     *
     * @param shackleType the shackle type
     */
    public void setShackleType(ShackleTypeType shackleType) {
        this.shackleType = shackleType;
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
     * Gets shackle to buoy connected to.
     *
     * @return the shackle to buoy connected to
     */
    public GenericBuoy getShackleToBuoyConnectedTo() {
        return shackleToBuoyConnectedTo;
    }

    /**
     * Sets shackle to buoy connected to.
     *
     * @param shackleToBuoyConnectedTo the shackle to buoy connected to
     */
    public void setShackleToBuoyConnectedTo(GenericBuoy shackleToBuoyConnectedTo) {
        this.shackleToBuoyConnectedTo = shackleToBuoyConnectedTo;
    }

    /**
     * Gets shackle to bridle connected to.
     *
     * @return the shackle to bridle connected to
     */
    public Bridle getShackleToBridleConnectedTo() {
        return shackleToBridleConnectedTo;
    }

    /**
     * Sets shackle to bridle connected to.
     *
     * @param shackleToBridleConnectedTo the shackle to bridle connected to
     */
    public void setShackleToBridleConnectedTo(Bridle shackleToBridleConnectedTo) {
        this.shackleToBridleConnectedTo = shackleToBridleConnectedTo;
    }

    /**
     * Gets shackle to anchor connected to.
     *
     * @return the shackle to anchor connected to
     */
    public SinkerAnchor getShackleToAnchorConnectedTo() {
        return shackleToAnchorConnectedTo;
    }

    /**
     * Sets shackle to anchor connected to.
     *
     * @param shackleToAnchorConnectedTo the shackle to anchor connected to
     */
    public void setShackleToAnchorConnectedTo(SinkerAnchor shackleToAnchorConnectedTo) {
        this.shackleToAnchorConnectedTo = shackleToAnchorConnectedTo;
    }

    /**
     * Gets shackle to swivel connected to.
     *
     * @return the shackle to swivel connected to
     */
    public Set<Swivel> getShackleToSwivelConnectedTo() {
        return shackleToSwivelConnectedTo;
    }

    /**
     * Sets shackle to swivel connected to.
     *
     * @param shackleToSwivelConnectedTo the shackle to swivel connected to
     */
    public void setShackleToSwivelConnectedTo(Set<Swivel> shackleToSwivelConnectedTo) {
        this.shackleToSwivelConnectedTo.clear();
        if(shackleToSwivelConnectedTo != null) {
            shackleToSwivelConnectedTo.forEach(connection -> connection.setShackleToSwivelConnected(this));
            this.shackleToSwivelConnectedTo.addAll(shackleToSwivelConnectedTo);
        }
    }

    /**
     * Gets shackle to cable connected to.
     *
     * @return the shackle to cable connected to
     */
    public Set<CableSubmarine> getShackleToCableConnectedTo() {
        return shackleToCableConnectedTo;
    }

    /**
     * Sets shackle to cable connected to.
     *
     * @param shackleToCableConnectedTo the shackle to cable connected to
     */
    public void setShackleToCableConnectedTo(Set<CableSubmarine> shackleToCableConnectedTo) {
        this.shackleToSwivelConnectedTo.clear();
        if(shackleToCableConnectedTo != null) {
            shackleToCableConnectedTo.forEach(connection -> connection.setShackleToCableConnected(this));
            this.shackleToCableConnectedTo.addAll(shackleToCableConnectedTo);
        }
    }
}
