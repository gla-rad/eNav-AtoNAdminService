package org.grad.eNav.atonAdminService.models.domain.s201;

import _int.iho.s_201.gml.cs0._2.HeightLengthUnitsType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The S-201 Cable Dimensions Embeddable Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Cable
 * Dimensions type
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.CableDimensionsType
 */
@Embeddable
public class CableDimensions implements Serializable {

    // Class Variables
    private BigDecimal cableLength;

    private HeightLengthUnitsType heightLengthUnits;

    private BigDecimal diameter;

    /**
     * Gets cable length.
     *
     * @return the cable length
     */
    public BigDecimal getCableLength() {
        return cableLength;
    }

    /**
     * Sets cable length.
     *
     * @param cableLength the cable length
     */
    public void setCableLength(BigDecimal cableLength) {
        this.cableLength = cableLength;
    }

    /**
     * Gets height length units.
     *
     * @return the height length units
     */
    public HeightLengthUnitsType getHeightLengthUnits() {
        return heightLengthUnits;
    }

    /**
     * Sets height length units.
     *
     * @param heightLengthUnits the height length units
     */
    public void setHeightLengthUnits(HeightLengthUnitsType heightLengthUnits) {
        this.heightLengthUnits = heightLengthUnits;
    }

    /**
     * Gets diameter.
     *
     * @return the diameter
     */
    public BigDecimal getDiameter() {
        return diameter;
    }

    /**
     * Sets diameter.
     *
     * @param diameter the diameter
     */
    public void setDiameter(BigDecimal diameter) {
        this.diameter = diameter;
    }
}
