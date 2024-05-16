/*
 * Copyright (c) 2024 GLA Research and Development Directorate
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

package org.grad.eNav.atonAdminService.models;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.util.GeometricShapeFactory;

/**
 * The type Un lo code map entry.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class UnLoCodeMapEntry {

    // Class Variables
    private double latitude;
    private double longitude;
    private String status;

    /**
     * Instantiates a new Un lo code map entry.
     */
    public UnLoCodeMapEntry() {

    }

    /**
     * Gets latitude.
     *
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets latitude.
     *
     * @param latitude the latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets longitude.
     *
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets longitude.
     *
     * @param longitude the longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the JTS geometry to the UN/LOCODE entry.
     *
     * @return the JTS geometry to the UN/LOCODE entry
     */
    public Geometry getGeometry() {
        final double diameterInMeters = 1000d; //1km
        final GeometricShapeFactory geometricShapeFactory = new GeometricShapeFactory(new GeometryFactory(new PrecisionModel(), 4326));
        geometricShapeFactory.setNumPoints(64);
        geometricShapeFactory.setCentre(new Coordinate(this.longitude, this.latitude));
        // Length in meters of 1° of latitude = always 111.32 km
        geometricShapeFactory.setWidth(diameterInMeters/111320d);
        // Length in meters of 1° of longitude = 40075 km * cos( latitude ) / 360
        geometricShapeFactory.setHeight(diameterInMeters / (40075000 * Math.cos(Math.toRadians(latitude)) / 360));
        return geometricShapeFactory.createEllipse();
    }
}
