package org.grad.eNav.atonAdminService.models.domain.s201;

import _int.iho.s_201.gml.cs0._2.PositioningEquipmentType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * The S-201 Positioning Method Embeddable Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible
 * PositioningMethod type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.PositioningMethodType
 */
@Embeddable
public class PositioningMethod {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private PositioningEquipmentType positioningEquipment;

    private String nmeaString;

    /**
     * Gets positioning equipment.
     *
     * @return the positioning equipment
     */
    public PositioningEquipmentType getPositioningEquipment() {
        return positioningEquipment;
    }

    /**
     * Sets positioning equipment.
     *
     * @param positioningEquipment the positioning equipment
     */
    public void setPositioningEquipment(PositioningEquipmentType positioningEquipment) {
        this.positioningEquipment = positioningEquipment;
    }

    /**
     * Gets nmea string.
     *
     * @return the nmea string
     */
    public String getNmeaString() {
        return nmeaString;
    }

    /**
     * Sets nmea string.
     *
     * @param nmeaString the nmea string
     */
    public void setNmeaString(String nmeaString) {
        this.nmeaString = nmeaString;
    }
}
