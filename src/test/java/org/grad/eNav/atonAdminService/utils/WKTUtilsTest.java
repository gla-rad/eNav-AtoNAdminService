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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class WKTUtilsTest {

    // Test Variables
    private Geometry point;
    private Geometry lineString;
    private Geometry polygon;
    private JsonNode pointJson;
    private JsonNode lineStringJson;
    private JsonNode polygonJson;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setUp() {
        // Create a temp geometry factory to get various geometries for testing
        GeometryFactory factory = new GeometryFactory();
        this.point = factory.createPoint(new Coordinate(52.001, 1.002));
        this.lineString = factory.createLineString(new Coordinate[]{
                new Coordinate(52.001, 1.002),
                new Coordinate(53.001, 2.002)
        });
        this.polygon = factory.createPolygon(new Coordinate[]{
                new Coordinate(52.001, 1.002),
                new Coordinate(53.001, 1.002),
                new Coordinate(53.001, 2.002),
                new Coordinate(52.001, 2.002),
                new Coordinate(52.001, 1.002),
        });

        // Translate the geometries into Geo JSON
        this.pointJson = GeometryJSONConverter.convertFromGeometry(this.point);
        this.lineStringJson = GeometryJSONConverter.convertFromGeometry(this.lineString);
        this.polygonJson = GeometryJSONConverter.convertFromGeometry(this.polygon);
    }

    /**
     * Test that we can correctly convert a WKT string into a Geometry object
     *
     * @throws ParseException when the WKT is invalid
     */
    @Test
    void testConvertWKTtoGeometry() throws ParseException {
        assertEquals(this.point, WKTUtils.convertWKTtoGeometry("POINT (1.002 52.001)"));
        assertEquals(this.lineString, WKTUtils.convertWKTtoGeometry("LINESTRING (1.002 52.001, 2.002 53.001)"));
        assertEquals(this.polygon, WKTUtils.convertWKTtoGeometry("POLYGON ((1.002 52.001, 1.002 53.001, 2.002 53.001, 2.002 52.001, 1.002 52.001))"));
    }

    /**
     * Test that a ParseException will be thrown if an invalid WKT string is
     * provided
     */
    @Test
    void testConvertWKTtoGeometryInvalid() {
        assertThrows(ParseException.class, () -> WKTUtils.convertWKTtoGeometry("This doesn't make sense!"));
    }

    /**
     * Test that we can correctly convert a WKT string into a GeoJSON object
     *
     * @throws ParseException when the WKT is invalid
     */
    @Test
    void testConvertWKTtoGeoJson() throws ParseException {
        assertEquals(this.pointJson, WKTUtils.convertWKTtoGeoJson("POINT (1.002 52.001)"));
        assertEquals(this.lineStringJson, WKTUtils.convertWKTtoGeoJson(("LINESTRING (1.002 52.001, 2.002 53.001)")));
        assertEquals(this.polygonJson, WKTUtils.convertWKTtoGeoJson(("POLYGON ((1.002 52.001, 1.002 53.001, 2.002 53.001, 2.002 52.001, 1.002 52.001))")));
    }

    /**
     * Test that a ParseException will be thrown if an invalid WKT string is
     * provided
     */
    @Test
    void testConvertWKTtoGeoJsonInvalid() {
        assertThrows(ParseException.class, () -> WKTUtils.convertWKTtoGeoJson("This doesn't make sense!"));
    }

}