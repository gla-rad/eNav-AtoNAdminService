package org.grad.eNav.atonAdminService.models.domain.s201;

import _int.iho.s_201.gml.cs0._2.CategoryOfCableType;
import _int.iho.s_201.gml.cs0._2.StatusType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.Set;

/**
 * The S-201 Cable Submarine Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Cable
 * Submarine type. It is modelled as an entity that extends the
 * {@link AidsToNavigation} super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.CableSubmarine
 */
@Entity
public class CableSubmarine extends AidsToNavigation {

    // Class Variables
    private CableDimensions cableDimensions;

    @Enumerated(EnumType.STRING)
    private CategoryOfCableType categoryOfCable;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private Set<StatusType> statuses;

    @JsonBackReference
    @ManyToOne
    private Swivel cableHoldsSwivel;

    @JsonBackReference
    @ManyToOne
    private Bridle cableHoldsBridle;

    @JsonBackReference
    @ManyToOne
    private MooringShackle shackleToCableConnected;

    /**
     * Gets cable dimensions.
     *
     * @return the cable dimensions
     */
    public CableDimensions getCableDimensions() {
        return cableDimensions;
    }

    /**
     * Sets cable dimensions.
     *
     * @param cableDimensions the cable dimensions
     */
    public void setCableDimensions(CableDimensions cableDimensions) {
        this.cableDimensions = cableDimensions;
    }

    /**
     * Gets category of cable.
     *
     * @return the category of cable
     */
    public CategoryOfCableType getCategoryOfCable() {
        return categoryOfCable;
    }

    /**
     * Sets category of cable.
     *
     * @param categoryOfCable the category of cable
     */
    public void setCategoryOfCable(CategoryOfCableType categoryOfCable) {
        this.categoryOfCable = categoryOfCable;
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
     * Gets cable holds swivel.
     *
     * @return the cable holds swivel
     */
    public Swivel getCableHoldsSwivel() {
        return cableHoldsSwivel;
    }

    /**
     * Sets cable holds swivel.
     *
     * @param cableHoldsSwivel the cable holds swivel
     */
    public void setCableHoldsSwivel(Swivel cableHoldsSwivel) {
        this.cableHoldsSwivel = cableHoldsSwivel;
    }

    /**
     * Gets cable holds bridle.
     *
     * @return the cable holds bridle
     */
    public Bridle getCableHoldsBridle() {
        return cableHoldsBridle;
    }

    /**
     * Sets cable holds bridle.
     *
     * @param cableHoldsBridle the cable holds bridle
     */
    public void setCableHoldsBridle(Bridle cableHoldsBridle) {
        this.cableHoldsBridle = cableHoldsBridle;
    }

    /**
     * Gets shackle to cable connected.
     *
     * @return the shackle to cable connected
     */
    public MooringShackle getShackleToCableConnected() {
        return shackleToCableConnected;
    }

    /**
     * Sets shackle to cable connected.
     *
     * @param shackleToCableConnected the shackle to cable connected
     */
    public void setShackleToCableConnected(MooringShackle shackleToCableConnected) {
        this.shackleToCableConnected = shackleToCableConnected;
    }
}
