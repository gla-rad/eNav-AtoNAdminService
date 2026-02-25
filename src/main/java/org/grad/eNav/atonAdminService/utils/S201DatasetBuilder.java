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

import _int.iho.s_201.gml.cs0._2.impl.DataCoverageImpl;
import _int.iho.s_201.s_100.gml.base._5_2.CurveProperty;
import _int.iho.s_201.s_100.gml.base._5_2.MultiPointProperty;
import _int.iho.s_201.s_100.gml.base._5_2.PointProperty;
import _int.iho.s_201.s_100.gml.base._5_2.SurfaceProperty;
import _int.iho.s_201.s_100.gml.profiles._5_2.BoundingShapeType;
import _int.iho.s_201.s_100.gml.profiles._5_2.EnvelopeType;
import _int.iho.s_201.s_100.gml.profiles._5_2.Pos;
import _int.iho.s_201.s_100.gml.profiles._5_2.impl.BoundingShapeTypeImpl;
import _int.iho.s_201.s_100.gml.profiles._5_2.impl.EnvelopeTypeImpl;
import _int.iho.s_201.s_100.gml.profiles._5_2.impl.PosImpl;
import _int.iho.s_201.gml.cs0._2.Dataset;
import _int.iho.s_201.gml.cs0._2.impl.AtonAggregationImpl;
import _int.iho.s_201.gml.cs0._2.impl.AtonAssociationImpl;
import _int.iho.s_201.gml.cs0._2.impl.DatasetImpl;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.grad.eNav.atonAdminService.models.domain.DatasetContent;
import org.grad.eNav.atonAdminService.models.domain.s201.AidsToNavigation;
import org.grad.eNav.atonAdminService.models.domain.s201.S201AtonTypes;
import org.grad.eNav.atonAdminService.models.domain.s201.S201Dataset;
import org.grad.eNav.s201.utils.S201Utils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.modelmapper.ModelMapper;

import java.math.BigInteger;
import java.util.*;

public class S201DatasetBuilder {

    /**
     * The Model Mapper
     */
    private final ModelMapper modelMapper;

    // Class Variables
    private String datasetIdPrefix;

    /**
     * Class Constructor.
     */
    public S201DatasetBuilder(ModelMapper modelMapper, String datasetIdPrefix) {
        this.modelMapper = modelMapper;
        this.datasetIdPrefix = datasetIdPrefix;
    }

    /**
     * Class Constructor with empty dataset ID prefix (uses the default).
     */
    public S201DatasetBuilder(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.datasetIdPrefix = null;
    }

    /**
     * This is the main   the provided list of AtoN nodes into an S-201 dataset
     * as dictated by the NIPWG S-201 data product specification.
     *
     * @param s201Dataset   The S-201 local dataset object
     * @param atons         The list of S-201 local AtoN object list
     */
    public Dataset packageToDataset(@NotNull S201Dataset s201Dataset, List<AidsToNavigation> atons) {
        // Initialise the dataset
        Dataset dataset = this.modelMapper.map(s201Dataset, DatasetImpl.class);

        // Always use a UUID for building the ID
        if(Objects.isNull(dataset.getId())) {
            dataset.setId(S201DatasetBuilder.generateDatasetId(this.datasetIdPrefix, s201Dataset.getUuid()));
        }

        // Add the content update sequence to the dataset identification information
        Optional.of(s201Dataset)
                .map(S201Dataset::getDatasetContent)
                .map(DatasetContent::getSequenceNo)
                .ifPresent(dataset.getDatasetIdentificationInformation()::setUpdateNumber);
        //====================================================================//
        //                       BOUNDED BY SECTION                           //
        //====================================================================//
        dataset.setBoundedBy(this.generateBoundingShape(atons));
        dataset.getPointsAndMultiPointsAndCurves()
                .addAll(
                    Optional.of(s201Dataset)
                    .map(d -> new GeometryS201Converter().geometryToS201PointCurveSurfaceGeometry(d.getGeometry()))
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(attr -> {
                        if(attr instanceof PointProperty) {
                            return ((PointProperty)attr).getPoint();
                        } else if(attr instanceof MultiPointProperty) {
                            return ((MultiPointProperty)attr).getMultiPoint();
                        } else if(attr instanceof CurveProperty) {
                            return ((CurveProperty)attr).getCurve();
                        } else if(attr instanceof SurfaceProperty) {
                            return ((SurfaceProperty)attr).getSurface();
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .toList()
                );

        //====================================================================//
        //                          DATA COVERAGE                             //
        //====================================================================//
        DataCoverageImpl dataCoverage = new DataCoverageImpl();
        dataCoverage.setMaximumDisplayScale(BigInteger.ZERO);
        dataCoverage.setMaximumDisplayScale(BigInteger.TEN);
        dataCoverage.setGeometries(new GeometryS201Converter().convertFromGeometry(s201Dataset));

        //====================================================================//
        //                      DATASET MEMBERS SECTION                       //
        //====================================================================//
        // Add the AtoN members
        S201Utils.addDatasetMembers(dataset, Optional.ofNullable(atons)
                .orElse(Collections.emptyList())
                .stream()
                .map(aton -> this.modelMapper.map(aton, S201AtonTypes.fromLocalClass(aton.getClass()).getS201Class()))
                .toList());

        // Append the aggregations
        S201Utils.addDatasetMembers(dataset, Optional.ofNullable(atons)
                .orElse(Collections.emptyList())
                .stream()
                .map(AidsToNavigation::getPeerAtonAggregations)
                .flatMap(Set::stream)
                .distinct()
                .map(agg -> this.modelMapper.map(agg, AtonAggregationImpl.class))
                .toList());

        // Append the associations
        S201Utils.addDatasetMembers(dataset,Optional.ofNullable(atons)
                .orElse(Collections.emptyList())
                .stream()
                .map(AidsToNavigation::getPeerAtonAssociations)
                .flatMap(Set::stream)
                .distinct()
                .map(ass -> this.modelMapper.map(ass, AtonAssociationImpl.class))
                .toList());

        // Return the dataset
        return dataset;
    }

    /**
     * For easy generation of the bounding shapes for the dataset or individual
     * features, we are using this function.
     *
     * @param atonNodes     The AtoN nodes to generate the bounding shape from
     * @return the bounding shape
     */
    protected BoundingShapeType generateBoundingShape(Collection<AidsToNavigation> atonNodes) {
        // Calculate the bounding by envelope
        final Envelope envelope = new Envelope();
        atonNodes.stream()
                .map(AidsToNavigation::getGeometry)
                .forEach(g -> this.enclosingEnvelopeFromGeometry(envelope, g));

        Pos lowerCorner = new PosImpl();
        lowerCorner.setValue(new Double[]{envelope.getMinX(), envelope.getMinY()});
        Pos upperCorner = new PosImpl();
        upperCorner.setValue(new Double[]{envelope.getMaxX(), envelope.getMaxY()});

        // And create the bounding by envelope
        BoundingShapeType boundingShapeType = new BoundingShapeTypeImpl();
        EnvelopeType envelopeType = new EnvelopeTypeImpl();
        envelopeType.setSrsName("http://www.opengis.net/def/crs/EPSG/0/4326");
        envelopeType.setSrsDimension(BigInteger.valueOf(2));
        envelopeType.setLowerCorner(lowerCorner);
        envelopeType.setUpperCorner(upperCorner);
        boundingShapeType.setEnvelope(envelopeType);

        // Finally, return the result
        return boundingShapeType;
    }

    /**
     * Adds the enclosing geometry boundaries to the provided envelop.
     *
     * @param envelope      The envelope to be updated
     * @param geometry      The geometry to update the envelope boundaries with
     * @return the updates envelope
     */
    protected Envelope enclosingEnvelopeFromGeometry(Envelope envelope, Geometry geometry) {
        final Geometry enclosingGeometry = geometry.getEnvelope();
        final Coordinate[] enclosingCoordinates = enclosingGeometry.getCoordinates();
        for (Coordinate c : enclosingCoordinates) {
            envelope.expandToInclude(c);
        }
        return envelope;
    }

    /**
     * A helpful utility to generate the dataset ID in a single place based on
     * the MRN prefix provided and the dataset UUID.
     * <p/>
     * The resulting ID should follow an MRN structure. Therefore, an MRN prefix
     * will be used and the UUID identified for this dataset will be appended in
     * the end. If no MRN prefix is provided, then a standard one to make sure
     * it conforms to something like an MRN will be used:
     * <p/>
     * e.g. "urn:mrn:test:s201:dataset"
     *
     * @param prefix the MRN prefix to be used
     * @param uuid the UUID of the dataset
     * @return the combine dataset ID as an MRN that includes the dataset UUID
     */
    public static String generateDatasetId(String prefix, UUID uuid) {
        return Optional.ofNullable(prefix)
                .filter(StringUtils::isNotBlank)
                .map(p -> p.endsWith(":") ? p : p+":")
                .orElse("urn:mrn:test:201:") + Optional.ofNullable(uuid).orElse(UUID.randomUUID());
    }

}
