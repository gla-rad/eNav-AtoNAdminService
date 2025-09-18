/*
 * Copyright (c) 2025 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grad.eNav.atonAdminService.models.domain.s201;

import _int.iho.s_201.gml.cs0._2.HeightLengthUnitsType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The S-201 Sinker Dimensions Embeddable Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Sinker
 * Dimensions type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.SinkerDimensionsType
 */
@Embeddable
public class SinkerDimensions implements Serializable {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private HeightLengthUnitsType heightLengthUnits;

    private BigDecimal horizontalLength;

    private BigDecimal horizontalWidth;

    private BigDecimal verticalLength;

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
     * Gets horizontal length.
     *
     * @return the horizontal length
     */
    public BigDecimal getHorizontalLength() {
        return horizontalLength;
    }

    /**
     * Sets horizontal length.
     *
     * @param horizontalLength the horizontal length
     */
    public void setHorizontalLength(BigDecimal horizontalLength) {
        this.horizontalLength = horizontalLength;
    }

    /**
     * Gets horizontal width.
     *
     * @return the horizontal width
     */
    public BigDecimal getHorizontalWidth() {
        return horizontalWidth;
    }

    /**
     * Sets horizontal width.
     *
     * @param horizontalWidth the horizontal width
     */
    public void setHorizontalWidth(BigDecimal horizontalWidth) {
        this.horizontalWidth = horizontalWidth;
    }

    /**
     * Gets vertical length.
     *
     * @return the vertical length
     */
    public BigDecimal getVerticalLength() {
        return verticalLength;
    }

    /**
     * Sets vertical length.
     *
     * @param verticalLength the vertical length
     */
    public void setVerticalLength(BigDecimal verticalLength) {
        this.verticalLength = verticalLength;
    }
}
