package org.grad.eNav.atonAdminService.models.domain.s201;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * The S-201 Bridle Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Bridle type.
 * It is modelled as an entity that extends the {@link AidsToNavigation} super
 * class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.Bridle
 */
@Entity
public class Bridle extends AidsToNavigation {

    // Class Variables
    private String bridleLinkType;

    @JsonBackReference
    @OneToOne
    private GenericBuoy bridleHolds;

    @JsonManagedReference
    @OneToOne(mappedBy = "swivelHolds", cascade = CascadeType.ALL)
    private Swivel bridleHangs;

    @JsonManagedReference
    @OneToMany(mappedBy = "cableHoldsBridle", cascade = CascadeType.ALL, orphanRemoval = true)
    final private Set<CableSubmarine> bridleAttacheds = new HashSet<>();

    /**
     * Gets bridle link type.
     *
     * @return the bridle link type
     */
    public String getBridleLinkType() {
        return bridleLinkType;
    }

    /**
     * Sets bridle link type.
     *
     * @param bridleLinkType the bridle link type
     */
    public void setBridleLinkType(String bridleLinkType) {
        this.bridleLinkType = bridleLinkType;
    }

    /**
     * Gets bridle holds.
     *
     * @return the bridle holds
     */
    public GenericBuoy getBridleHolds() {
        return bridleHolds;
    }

    /**
     * Sets bridle holds.
     *
     * @param bridleHolds the bridle holds
     */
    public void setBridleHolds(GenericBuoy bridleHolds) {
        this.bridleHolds = bridleHolds;
    }

    /**
     * Gets bridle hangs.
     *
     * @return the bridle hangs
     */
    public Swivel getBridleHangs() {
        return bridleHangs;
    }

    /**
     * Sets bridle hangs.
     *
     * @param bridleHangs the bridle hangs
     */
    public void setBridleHangs(Swivel bridleHangs) {
        this.bridleHangs = bridleHangs;
    }

    /**
     * Gets bridle attacheds.
     *
     * @return the bridle attacheds
     */
    public Set<CableSubmarine> getBridleAttacheds() {
        return bridleAttacheds;
    }

    /**
     * Sets bridle attacheds.
     *
     * @param bridleAttacheds the bridle attacheds
     */
    public void setBridleAttacheds(Set<CableSubmarine> bridleAttacheds) {
        this.bridleAttacheds.clear();
        if(bridleAttacheds != null) {
            bridleAttacheds.forEach(bridleAttached -> bridleAttached.setCableHoldsBridle(this));
            this.bridleAttacheds.addAll(bridleAttacheds);
        }
    }
}
