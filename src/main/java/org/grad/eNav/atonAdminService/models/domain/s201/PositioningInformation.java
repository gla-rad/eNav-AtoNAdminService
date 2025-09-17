package org.grad.eNav.atonAdminService.models.domain.s201;

import _int.iho.s_201.gml.cs0._2.HorizontalDatumType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * The S-201 Positioning Information Entity Class.
 * <p/>
 * This class implements the APositioning Information type of the S-201 Aids to
 * Navigation objects. It is modelled as an entity that extends the
 * {@link Information} super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.PositioningInformation
 */
@Entity
public class PositioningInformation extends Information {

    // Class Variables
    private String positioningDevice;

    private PositioningMethod positioningMethod;

    @JsonBackReference
    @ManyToOne
    private StructureObject positions;

    /**
     * Gets positioning device.
     *
     * @return the positioning device
     */
    public String getPositioningDevice() {
        return positioningDevice;
    }

    /**
     * Sets positioning device.
     *
     * @param positioningDevice the positioning device
     */
    public void setPositioningDevice(String positioningDevice) {
        this.positioningDevice = positioningDevice;
    }

    /**
     * Gets positioning method.
     *
     * @return the positioning method
     */
    public PositioningMethod getPositioningMethod() {
        return positioningMethod;
    }

    /**
     * Sets positioning method.
     *
     * @param positioningMethod the positioning method
     */
    public void setPositioningMethod(PositioningMethod positioningMethod) {
        this.positioningMethod = positioningMethod;
    }

    /**
     * Gets positions.
     *
     * @return the positions
     */
    public StructureObject getPositions() {
        return positions;
    }

    /**
     * Sets positions.
     *
     * @param positions the positions
     */
    public void setPositions(StructureObject positions) {
        this.positions = positions;
    }
}
