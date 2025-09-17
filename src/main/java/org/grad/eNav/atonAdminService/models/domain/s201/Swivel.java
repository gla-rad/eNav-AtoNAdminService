package org.grad.eNav.atonAdminService.models.domain.s201;

import _int.iho.s_201.gml.cs0._2.CategoryOfCableType;
import _int.iho.s_201.gml.cs0._2.NatureOfConstructionType;
import _int.iho.s_201.gml.cs0._2.StatusType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * The S-201 Swivel Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Swivel
 * type. It is modelled as an entity that extends the
 * {@link AidsToNavigation} super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.Swivel
 */
@Entity
public class Swivel extends AidsToNavigation {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private NatureOfConstructionType natureOfConstruction;

    private BigDecimal weight;

    private String swivelType;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private Set<StatusType> statuses;

    @JsonBackReference
    @OneToOne(mappedBy = "bridleHangs")
    private Bridle swivelHolds;

    @JsonBackReference
    @ManyToOne
    private MooringShackle shackleToSwivelConnected;

    @JsonManagedReference
    @OneToMany(mappedBy = "cableHoldsSwivel", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<CableSubmarine> swivelAttacheds = new HashSet<>();

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
     * Gets swivel type.
     *
     * @return the swivel type
     */
    public String getSwivelType() {
        return swivelType;
    }

    /**
     * Sets swivel type.
     *
     * @param swivelType the swivel type
     */
    public void setSwivelType(String swivelType) {
        this.swivelType = swivelType;
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
     * Gets swivel holds.
     *
     * @return the swivel holds
     */
    public Bridle getSwivelHolds() {
        return swivelHolds;
    }

    /**
     * Sets swivel holds.
     *
     * @param swivelHolds the swivel holds
     */
    public void setSwivelHolds(Bridle swivelHolds) {
        this.swivelHolds = swivelHolds;
    }

    /**
     * Gets shackle to swivel connected.
     *
     * @return the shackle to swivel connected
     */
    public MooringShackle getShackleToSwivelConnected() {
        return shackleToSwivelConnected;
    }

    /**
     * Sets shackle to swivel connected.
     *
     * @param shackleToSwivelConnected the shackle to swivel connected
     */
    public void setShackleToSwivelConnected(MooringShackle shackleToSwivelConnected) {
        this.shackleToSwivelConnected = shackleToSwivelConnected;
    }

    /**
     * Gets swivel attacheds.
     *
     * @return the swivel attacheds
     */
    public Set<CableSubmarine> getSwivelAttacheds() {
        return swivelAttacheds;
    }

    /**
     * Sets swivel attacheds.
     *
     * @param swivelAttacheds the swivel attacheds
     */
    public void setSwivelAttacheds(Set<CableSubmarine> swivelAttacheds) {
        if(swivelAttacheds != null) {
            swivelAttacheds.forEach(swivel -> swivel.setCableHoldsSwivel(this));
        }
        this.swivelAttacheds.clear();
        this.swivelAttacheds.addAll(swivelAttacheds);
    }
}
