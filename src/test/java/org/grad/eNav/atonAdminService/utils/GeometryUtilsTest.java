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

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

import static org.junit.jupiter.api.Assertions.*;

class GeometryUtilsTest {


    // Test Variables
    private GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    /**
     * Test that we can join multiple geometries into a single one.
     */
    @Test
    void testJoinGeometries() {
        Geometry pointGeometry = this.geometryFactory.createPoint(new Coordinate(1, 2));
        Geometry lineGeometry = this.geometryFactory.createLineString(new Coordinate[] {
                new Coordinate(1, 2),
                new Coordinate(2, 3),
        });
        Geometry polygonGeometry = this.geometryFactory.createPolygon(new Coordinate[] {
                new Coordinate(-180, -90),
                new Coordinate(-180, 90),
                new Coordinate(180, 90),
                new Coordinate(180, -90),
                new Coordinate(-180, -90)
        });

        // Join null geometries
        assertNull(GeometryUtils.joinGeometries(null, null));
        assertNull(GeometryUtils.joinGeometries(null, null, null));

        // Join a geometry with null
        assertEquals(pointGeometry, GeometryUtils.joinGeometries(pointGeometry, null));
        assertEquals(pointGeometry, GeometryUtils.joinGeometries(null, pointGeometry));
        assertEquals(lineGeometry, GeometryUtils.joinGeometries(lineGeometry, null));
        assertEquals(lineGeometry, GeometryUtils.joinGeometries(null, lineGeometry));
        assertEquals(polygonGeometry, GeometryUtils.joinGeometries(polygonGeometry, null));
        assertEquals(polygonGeometry, GeometryUtils.joinGeometries(null, polygonGeometry));

        // Now join some geometries
        assertEquals(polygonGeometry, GeometryUtils.joinGeometries(pointGeometry, lineGeometry, polygonGeometry));
    }

}