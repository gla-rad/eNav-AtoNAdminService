package org.grad.eNav.atonAdminService.models.domain.s201;

import _int.iho.s_201.gml.cs0._2.HorizontalDatumType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * The S-201 AtoN Fixing Method Entity Class.
 * <p/>
 * This class implements the AtoN Fixing Method type of the S-201 Aids to
 * Navigation objects. It is modelled as an entity that extends the
 * {@link Information} super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.AtoNFixingMethod
 */
@Entity
public class AtonFixingMethod extends Information {

    // Class Variables
    private String referencePoint;

    @Enumerated(EnumType.STRING)
    private HorizontalDatumType horizontalDatum;

    private LocalDate sourceDate;

    private String positioningProcedure;

    @JsonBackReference
    @ManyToOne
    private StructureObject fixes;

    /**
     * Gets reference point.
     *
     * @return the reference point
     */
    public String getReferencePoint() {
        return referencePoint;
    }

    /**
     * Sets reference point.
     *
     * @param referencePoint the reference point
     */
    public void setReferencePoint(String referencePoint) {
        this.referencePoint = referencePoint;
    }

    /**
     * Gets horizontal datum.
     *
     * @return the horizontal datum
     */
    public HorizontalDatumType getHorizontalDatum() {
        return horizontalDatum;
    }

    /**
     * Sets horizontal datum.
     *
     * @param horizontalDatum the horizontal datum
     */
    public void setHorizontalDatum(HorizontalDatumType horizontalDatum) {
        this.horizontalDatum = horizontalDatum;
    }

    /**
     * Gets source date.
     *
     * @return the source date
     */
    public LocalDate getSourceDate() {
        return sourceDate;
    }

    /**
     * Sets source date.
     *
     * @param sourceDate the source date
     */
    public void setSourceDate(LocalDate sourceDate) {
        this.sourceDate = sourceDate;
    }

    /**
     * Gets positioning procedure.
     *
     * @return the positioning procedure
     */
    public String getPositioningProcedure() {
        return positioningProcedure;
    }

    /**
     * Sets positioning procedure.
     *
     * @param positioningProcedure the positioning procedure
     */
    public void setPositioningProcedure(String positioningProcedure) {
        this.positioningProcedure = positioningProcedure;
    }

    /**
     * Gets fixes.
     *
     * @return the fixes
     */
    public StructureObject getFixes() {
        return fixes;
    }

    /**
     * Sets fixes.
     *
     * @param fixes the fixes
     */
    public void setFixes(StructureObject fixes) {
        this.fixes = fixes;
    }
}
