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

package org.grad.eNav.atonAdminService.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.geojson.GeoJsonWriter;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * The GeoJSON Utils Class.
 *
 * This is a utility class that allows the easy manipulation of the GeoJSON
 * data. For example, we want to easily create GeoJSON point from x and y
 * coordinates.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class GeoJSONUtils {

    /**
     * A helper function that will return a GeoJSON Point object based on the
     * provided x and y coordinates.
     *
     * @param x the x value of the coordinate
     * @param y the y value of the coordinate
     * @return the GeoJSON point object
     */
    public static JsonNode createGeoJSONPoint(double x, double y) {
        return createGeoJSONPoint(x, y, null);
    }

    /**
     * A helper function that will return a GeoJSON Point object based on the
     * provided x and y coordinates. This extended version also allows users
     * to define the coordinate reference system through the SRID parameter.
     *
     * @param x the x value of the coordinate
     * @param y the y value of the coordinate
     * @param srid the default coordinate reference system ID - defaults to EPSG:4326
     * @return the GeoJSON point object
     */
    public static JsonNode createGeoJSONPoint(double x, double y, Integer srid) {
        // First create a com.locationtech.jts Point geometry;
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), Optional.ofNullable(srid).orElse(4326));
        Point point = factory.createPoint(new Coordinate(x, y));

        // Now convert into a JSON node
        ObjectMapper om = new ObjectMapper();
        try {
            return om.readTree(new GeoJsonWriter().write(point));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * To easily translate a GeoJSON point to an ECQL point description, this
     * utility function reads the json node and if a valid point
     *class WKTUtilTest {

}
     * @param point the GeoJSON point to be translated
     * @return The ECQL description of the point
     */
    public static String geoJSONPointToECQL(JsonNode point) {
        // Sanity check
        if(Objects.isNull(point)) {
            return "";
        }
        JsonNode type = point.get("type");
        JsonNode coordinates = point.get("coordinates");
        if(Objects.nonNull(type) && type.asText().equals("Point") && Objects.nonNull(coordinates) && coordinates.isArray()) {
            return String.format("POINT (%s %s)", coordinates.get(0).toString(), coordinates.get(1).toString());
        }
        else {
            return "";
        }
    }

}
