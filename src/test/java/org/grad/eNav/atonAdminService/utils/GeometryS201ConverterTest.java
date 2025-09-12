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


import _int.iho.s_201.s_100.gml.base._5_2.CurveProperty;
import _int.iho.s_201.s_100.gml.base._5_2.PointProperty;
import _int.iho.s_201.s_100.gml.base._5_2.S100SpatialAttributeType;
import _int.iho.s_201.s_100.gml.base._5_2.SurfaceProperty;
import _int.iho.s_201.s_100.gml.profiles._5_2.*;
import _int.iho.s_201.s_100.gml.profiles._5_2.impl.PosListImpl;
import _int.iho.s_201.gml.cs0._2.Dataset;
import _int.iho.s_201.gml.cs0._2.impl.VirtualAISAidToNavigationImpl;
import jakarta.xml.bind.JAXBException;
import org.apache.commons.io.IOUtils;
import org.grad.eNav.atonAdminService.models.domain.s201.VirtualAISAidToNavigation;
import org.grad.eNav.s201.utils.S201Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.*;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GeometryS201ConverterTest {

    // Test Variables
    private GeometryS201Converter geometryS201Converter;
    private GeometryFactory factory;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setup() throws IOException {
        // First create the converter
        geometryS201Converter = new GeometryS201Converter();

        // Create a temp geometry factory to get a test geometries
        this.factory = new GeometryFactory(new PrecisionModel(), 4326);
    }

    /**
     * Test that we can directly convert an S-201 dataset Aids to Navigation
     * member entry to its respective JTS geometry, with the respective type
     * that is matching.
     */
    @Test
    void testConvertFromGeometry() throws IOException, JAXBException {
        // Read a valid S-201 content to generate the S201Node message for.
        InputStream in = new ClassPathResource("s201-msg.xml").getInputStream();
        String xml = IOUtils.toString(in, StandardCharsets.UTF_8);

        // Unmarshall it to a G1128 service instance object
        Dataset dataset = S201Utils.unmarshallS201(xml);

        // Assert the S-201 AidsToNavigation feature information is populated
        assertNotNull(dataset.getMembers());
        assertEquals(4, S201Utils.getDatasetMembers(dataset).size());
        VirtualAISAidToNavigationImpl resultMember = S201Utils.getDatasetMembers(dataset)
                .stream()
                .filter(VirtualAISAidToNavigationImpl.class::isInstance)
                .map(VirtualAISAidToNavigationImpl.class::cast)
                .findAny()
                .orElse(null);

        // Convert to JTS geometry
        Geometry result = this.geometryS201Converter.convertToGeometry(resultMember);

        // Make sure the result looks OK
        assertNotNull(result);
        assertEquals(Point.class, result.getClass());
        assertEquals(51.8916667, ((Point)result).getX());
        assertEquals(1.4233333, ((Point)result).getY());
    }

    /**
     * Test that we can directly convert the JTS geometry from an Aids to
     * Navigation object into an S-201 PointCurveSurfaceProperty object
     * populated as required.
     */
    @Test
    void testConvertToGeometry() {
        // Create the point
        List<Double> coords = Stream.of(51.98, 1.28).toList();
        Point point = this.factory.createPoint(new Coordinate(coords.get(0), coords.get(1)));

        // Create a typical Aids to Navigation
        VirtualAISAidToNavigation virtualAISAidToNavigation = new VirtualAISAidToNavigation();
        virtualAISAidToNavigation.setGeometry(point);

        // Convert to S-201 geometry
        List<?> result = this.geometryS201Converter.convertFromGeometry(virtualAISAidToNavigation);

        // Make sure the result looks OK
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.getFirst() instanceof VirtualAISAidToNavigationImpl.Geometry);

        // Make sure the point property looks OK
        PointProperty pointProperty = ((VirtualAISAidToNavigationImpl.Geometry) result.getFirst()).getPointProperty();
        assertNotNull(pointProperty.getPoint());
        assertNotNull(pointProperty.getPoint().getPos());
        assertNotNull(pointProperty.getPoint().getPos().getValue());
        assertEquals(coords.size(), pointProperty.getPoint().getPos().getValue().length);
        for(int i=0; i< coords.size(); i++) {
            assertEquals(coords.get(i), pointProperty.getPoint().getPos().getValue()[i]);
        }
    }

    /**
     * Test that we can translate correctly a geometry object to an S-201
     * point/curve/surface geometry description. This specific test looks
     * at the surface translation.
     */
    @Test
    void testGeometryToS201PointCurveSurfaceToGeometryForSurface() {
        // Create the polygon
        List<Double> coords = Stream.of(51.98, 1.28, 51.98, 2.28, 52.98, 2.28, 52.98, 1.28, 51.98, 1.28).toList();
        Polygon polygon = this.factory.createPolygon(new Coordinate[]{
                new Coordinate(coords.get(0), coords.get(1)),
                new Coordinate(coords.get(2), coords.get(3)),
                new Coordinate(coords.get(4), coords.get(5)),
                new Coordinate(coords.get(6), coords.get(7)),
                new Coordinate(coords.get(8), coords.get(9))}
        );

        // Translate to S-201 geometry
        List<S100SpatialAttributeType> result = this.geometryS201Converter.geometryToS201PointCurveSurfaceGeometry(polygon);

        // Make sure the result looks OK
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        // Make sure the surface property looks OK
        assertNotNull(result.getFirst());
        assertTrue(result.getFirst() instanceof SurfaceProperty);

        // Make sure the surface property looks OK
        SurfaceProperty surfaceProperty = (SurfaceProperty) result.get(0);
        assertNotNull(surfaceProperty.getSurface());
        assertNotNull(surfaceProperty.getSurface().getPatches());
        assertNotNull(surfaceProperty.getSurface().getPatches().getAbstractSurfacePatches());
        assertEquals(1, surfaceProperty.getSurface().getPatches().getAbstractSurfacePatches().size());
        assertTrue(surfaceProperty.getSurface().getPatches().getAbstractSurfacePatches().getFirst().getValue() instanceof PolygonPatchType);

        // Make sure the polygon patches looks OK
        PolygonPatchType polygonPatchType = (PolygonPatchType)surfaceProperty.getSurface().getPatches().getAbstractSurfacePatches().getFirst().getValue();
        assertNotNull(polygonPatchType);
        assertNotNull(polygonPatchType.getExterior());
        assertNotNull(polygonPatchType.getExterior().getAbstractRing());
        assertNotNull(polygonPatchType.getExterior().getAbstractRing().getValue());
        assertTrue(polygonPatchType.getExterior().getAbstractRing().getValue() instanceof LinearRingType);

        // Make sure the Linear Right look OK
        assertNotNull(((LinearRingType)polygonPatchType.getExterior().getAbstractRing().getValue()).getPosList());
        assertNotNull(((LinearRingType)polygonPatchType.getExterior().getAbstractRing().getValue()).getPosList().getValue());
        assertEquals(10, ((LinearRingType)polygonPatchType.getExterior().getAbstractRing().getValue()).getPosList().getValue().length);
        for(int i=0; i< coords.size(); i++) {
            assertEquals(coords.get(i), ((LinearRingType)polygonPatchType.getExterior().getAbstractRing().getValue()).getPosList().getValue()[i]);
        }
    }

    /**
     * Test that we can translate correctly a geometry object to an S-201
     * point/curve/surface geometry description. This specific test looks
     * at the curve translation.
     */
    @Test
    void testGeometryToS201PointCurveSurfaceToGeometryForCurve() {
        // Create the line
        List<Double> coords = Stream.of(51.98, 1.28, 52.98, 2.28).toList();
        LineString lineString = this.factory.createLineString(new Coordinate[]{new Coordinate(coords.get(0), coords.get(1)), new Coordinate(coords.get(2), coords.get(3))});

        // Translate to S-201 geometry
        List<S100SpatialAttributeType> result = this.geometryS201Converter.geometryToS201PointCurveSurfaceGeometry(lineString);

        // Make sure the result looks OK
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        // Make sure the result looks OK
        assertNotNull(result.getFirst());
        assertTrue(result.getFirst() instanceof CurveProperty);

        // Make sure the curve property looks OK
        CurveProperty curveProperty = (CurveProperty) result.getFirst();
        assertNotNull(curveProperty.getCurve().getSegments());
        assertNotNull(curveProperty.getCurve().getSegments().getAbstractCurveSegments());
        assertEquals(1, curveProperty.getCurve().getSegments().getAbstractCurveSegments().size());
        assertNotNull(curveProperty.getCurve().getSegments().getAbstractCurveSegments().get(0).getValue());
        assertTrue(curveProperty.getCurve().getSegments().getAbstractCurveSegments().getFirst().getValue() instanceof  LineStringSegmentType);

        // Make sure the curve segments looks OK
        LineStringSegmentType lineStringSegmentType = (LineStringSegmentType) curveProperty.getCurve().getSegments().getAbstractCurveSegments().getFirst().getValue();
        assertNotNull(lineStringSegmentType.getPosList());
        assertNotNull(lineStringSegmentType.getPosList().getValue());
        assertEquals(4,lineStringSegmentType.getPosList().getValue().length);
        for(int i=0; i< coords.size(); i++) {
            assertEquals(coords.get(i),lineStringSegmentType.getPosList().getValue()[i]);
        }
    }

    /**
     * Test that we can translate correctly a geometry object to an S-201
     * point/curve/surface geometry description. This specific test looks
     * at the point translation.
     */
    @Test
    void testGeometryToS201PointCurveSurfaceToGeometryForPoint() {
        // Create the point
        List<Double> coords = Stream.of(51.98, 1.28).toList();
        Point point = this.factory.createPoint(new Coordinate(coords.get(0), coords.get(1)));

        // Translate to S-201 geometry
        List<S100SpatialAttributeType> result = this.geometryS201Converter.geometryToS201PointCurveSurfaceGeometry(point);

        // Make sure the result looks OK
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        // Make sure the result looks OK
        assertNotNull(result.getFirst());
        assertTrue(result.getFirst() instanceof PointProperty);

        // Make sure the point property looks OK
        PointProperty pointProperty = (PointProperty) result.getFirst();
        assertNotNull(pointProperty.getPoint());
        assertNotNull(pointProperty.getPoint().getPos());
        assertNotNull(pointProperty.getPoint().getPos().getValue());
        assertEquals(coords.size(), pointProperty.getPoint().getPos().getValue().length);
        for(int i=0; i< coords.size(); i++) {
            assertEquals(coords.get(i), pointProperty.getPoint().getPos().getValue()[i]);
        }
    }

    /**
     * Test that we can correctly create a new S-201 GML SurfaceProperty Patch
     * based on a set of coordinates.
     */
    @Test
    void testGenerateSurfacePropertyPatch() {
        // Create the coordinates
        List<Double> coords = Stream.of(51.98, 1.28, 51.98, 2.28, 52.98, 2.28, 52.98, 1.28, 51.98, 1.28).toList();

        // Generate the curve property patch
        PolygonPatchType result = this.geometryS201Converter.generateSurfacePropertyPatch(coords.toArray(Double[]::new));

        // Make sure the polygon patches look OK
        assertNotNull(result);
        assertNotNull(result.getExterior());
        assertNotNull(result.getExterior().getAbstractRing());
        assertNotNull(result.getExterior().getAbstractRing().getValue());
        assertTrue(result.getExterior().getAbstractRing().getValue() instanceof LinearRingType);
        assertNotNull(((LinearRingType)result.getExterior().getAbstractRing().getValue()).getPosList());
        assertNotNull(((LinearRingType)result.getExterior().getAbstractRing().getValue()).getPosList().getValue());
        assertEquals(coords.size(), ((LinearRingType)result.getExterior().getAbstractRing().getValue()).getPosList().getValue().length);
        for(int i=0; i< coords.size(); i++) {
            assertEquals(coords.get(i), ((LinearRingType)result.getExterior().getAbstractRing().getValue()).getPosList().getValue()[i]);
        }
    }

    /**
     * Test that we can correctly create a new S-201 GML CurveProperty Segment
     * based on a set of coordinates.
     */
    @Test
    void testGenerateCurvePropertySegment() {
        // Create the coordinates
        List<Double> coords = Stream.of(51.98, 1.28, 52.98, 2.28).toList();

        // Generate the curve property segment
        LineStringSegmentType result = this.geometryS201Converter.generateCurvePropertySegment(coords.toArray(Double[]::new));

        // Make sure the curve segments look OK
        assertNotNull(result);
        assertNotNull(result.getPosList());
        assertNotNull(result.getPosList().getValue());
        assertEquals(coords.size(), result.getPosList().getValue().length);
        for(int i=0; i< coords.size(); i++) {
            assertEquals(coords.get(i),result.getPosList().getValue()[i]);
        }
    }

    /**
     * Test that we can correctly create a new S-201 GML PointProperty Position
     * based on a set of coordinates.
     */
    @Test
    void testGeneratePointPropertyPosition() {
        // Create the coordinates
        List<Double> coords = Stream.of(51.98, 1.28).toList();

        // Generate the point property position
        Pos result = this.geometryS201Converter.generatePointPropertyPosition(coords.toArray(Double[]::new));

        // Make sure the point property looks OK
        assertNotNull(result);
        assertEquals(coords.size(), result.getValue().length);
        for(int i=0; i< coords.size(); i++) {
            assertEquals(coords.get(i), result.getValue()[i]);
        }
    }

    /**
     * Test that we can correctly initialise a new S-201 GML SurfaceProperty.
     */
    @Test
    void testInitialiseSurfaceProperty() {
        // Generate the curve property
        SurfaceProperty result = this.geometryS201Converter.initialiseSurfaceProperty();

        // Make sure the surface property looks OK
        assertNotNull(result);
        assertNotNull(result.getSurface());
        assertNotNull(result.getSurface().getPatches());
        assertNotNull(result.getSurface().getPatches().getAbstractSurfacePatches());
        assertEquals(0, result.getSurface().getPatches().getAbstractSurfacePatches().size());
    }

    /**
     * Test that we can correctly initialise a new S-201 GML CurveProperty.
     */
    @Test
    void testInitialiseCurveProperty() {
        // Generate the curve property
        CurveProperty result = this.geometryS201Converter.initialiseCurveProperty();

        // Make sure the curve property looks OK
        assertNotNull(result);
        assertNotNull(result.getCurve());
        assertNotNull(result.getCurve().getSegments());
        assertNotNull(result.getCurve().getSegments().getAbstractCurveSegments());
        assertEquals(0, result.getCurve().getSegments().getAbstractCurveSegments().size());
    }

    /**
     * Test that we can correctly initialise a new S-201 GML PointProperty.
     */
    @Test
    void tesInitialisePointProperty() {
        // Generate the point property
        PointProperty result = this.geometryS201Converter.initPointProperty();

        // Make sure the point property looks OK
        assertNotNull(result);
        assertNotNull(result.getPoint());
    }

    /**
     * Test that we can correctly translate an S-201 GML position list into a
     * array of coordinates.
     */
    @Test
    void testPosListToCoordinate() {
        // Initialise the position list
        PosList posList = new PosListImpl();
        posList.setValue(Stream.of(1.0, 2.0, 3.0, 4.0).toArray(Double[]::new));

        // Translate to coordinates
        Coordinate[] result = this.geometryS201Converter.gmlPosListToCoordinates(posList);

        // Make sure the translation looks OK
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals(posList.getValue()[0], result[0].getX());
        assertEquals(posList.getValue()[1], result[0].getY());
        assertEquals(posList.getValue()[2], result[1].getX());
        assertEquals(posList.getValue()[3], result[1].getY());
    }

    /**
     * Test that we can correctly translate an array of coordinates into a
     * single S-201 GML position list.
     */
    @Test
    void testCoordinatesToGmlPosList() {
        // Initialise some coordinates
        Coordinate[] coordinates = new Coordinate[]{new Coordinate(1.0, 2.0, 0), new Coordinate(3.0, 4.0, 0)};

        // Translate to a position list
        PosList result = this.geometryS201Converter.coordinatesToGmlPosList(coordinates);

        // Make sure the translation looks OK
        assertNotNull(result);
        assertNotNull(result.getValue());
        assertEquals(4, result.getValue().length);
        assertEquals(coordinates[0].getX(), result.getValue()[0]);
        assertEquals(coordinates[0].getY(), result.getValue()[1]);
        assertEquals(coordinates[1].getX(), result.getValue()[2]);
        assertEquals(coordinates[1].getY(), result.getValue()[3]);
    }
}