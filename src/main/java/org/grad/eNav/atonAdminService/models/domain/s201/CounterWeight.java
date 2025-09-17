package org.grad.eNav.atonAdminService.models.domain.s201;

import _int.iho.s_201.gml.cs0._2.NatureOfConstructionType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * The S-201 Counter Weight Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Counter Weight
 * type. It is modelled as an entity that extends the {@link AidsToNavigation}
 * super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.CounterWeight
 */
@Entity
public class CounterWeight extends AidsToNavigation {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private NatureOfConstructionType natureOfConstruction;

    private BigDecimal weight;

    private String counterWeightType;

    @JsonBackReference
    @OneToOne(mappedBy = "buoyAttached", cascade = CascadeType.ALL)
    private GenericBuoy counterWeightHolds;

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
     * Gets counter weight type.
     *
     * @return the counter weight type
     */
    public String getCounterWeightType() {
        return counterWeightType;
    }

    /**
     * Sets counter weight type.
     *
     * @param counterWeightType the counter weight type
     */
    public void setCounterWeightType(String counterWeightType) {
        this.counterWeightType = counterWeightType;
    }

    /**
     * Gets counter weight holds.
     *
     * @return the counter weight holds
     */
    public GenericBuoy getCounterWeightHolds() {
        return counterWeightHolds;
    }

    /**
     * Sets counter weight holds.
     *
     * @param counterWeightHolds the counter weight holds
     */
    public void setCounterWeightHolds(GenericBuoy counterWeightHolds) {
        this.counterWeightHolds = counterWeightHolds;
    }
}
