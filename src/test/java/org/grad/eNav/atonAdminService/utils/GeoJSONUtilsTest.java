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
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GeoJSONUtilsTest {

    /**
     * Test that we can create the GeoJSON point definitions correctly for the
     * any given x and y coordinates.
     */
    @Test
    public void testCreateGeoJSONPoint() {
        JsonNode point00 = GeoJSONUtils.createGeoJSONPoint(0, 0);
        assertNotNull(point00);
        assertEquals("Point", point00.get("type").textValue());
        assertEquals("[0.0,0.0]", point00.get("coordinates").toString());
        assertEquals("EPSG:4326", point00.get("crs").get("properties").get("name").textValue());

        JsonNode point12 = GeoJSONUtils.createGeoJSONPoint(1, 2);
        assertNotNull(point12);
        assertEquals("Point", point12.get("type").textValue());
        assertEquals("[1,2]", point12.get("coordinates").toString());
        assertEquals("EPSG:4326", point12.get("crs").get("properties").get("name").textValue());

        JsonNode point18090 = GeoJSONUtils.createGeoJSONPoint(180, 90);
        assertNotNull(point18090);
        assertEquals("Point", point18090.get("type").textValue());
        assertEquals("[180,90]", point18090.get("coordinates").toString());
        assertEquals("EPSG:4326", point18090.get("crs").get("properties").get("name").textValue());

        JsonNode point18090CRS = GeoJSONUtils.createGeoJSONPoint(180, 90, 2810);
        assertNotNull(point18090CRS);
        assertEquals("Point", point18090CRS.get("type").textValue());
        assertEquals("[180,90]", point18090CRS.get("coordinates").toString());
        assertEquals("EPSG:2810", point18090CRS.get("crs").get("properties").get("name").textValue());
    }

    /**
     * Test that we can translate the GeoJSON points correctly to their ECQL
     * descriptions.
     */
    @Test
    public void testGeoJSONPointToECQL() {
        String pointNull = GeoJSONUtils.geoJSONPointToECQL(null);
        assertEquals("", pointNull);

        String pointEmpty = GeoJSONUtils.geoJSONPointToECQL(new ObjectMapper().createObjectNode());
        assertEquals("", pointEmpty);

        JsonNode point00 = GeoJSONUtils.createGeoJSONPoint(0, 0, null);
        String point00ECQL = GeoJSONUtils.geoJSONPointToECQL(point00);
        assertEquals("POINT (0.0 0.0)", point00ECQL);

        JsonNode point12 = GeoJSONUtils.createGeoJSONPoint(1, 2, null);
        String point12ECQL = GeoJSONUtils.geoJSONPointToECQL(point12);
        assertEquals("POINT (1 2)", point12ECQL);

        JsonNode point18090 = GeoJSONUtils.createGeoJSONPoint(180, 90, null);
        String point18090ECQL = GeoJSONUtils.geoJSONPointToECQL(point18090);
        assertEquals("POINT (180 90)", point18090ECQL);
    }

}