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

package org.grad.eNav.atonAdminService.models.domain.s201;

import _int.iho.s_201.gml.cs0._2.CategoryOfAggregationType;
import _int.iho.s_201.gml.cs0._2.ChangeTypesType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtonAggregationTest {

    // Test Variables
    private AtonAggregation aggregation1;
    private AtonAggregation aggregation2;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setup() {
        // Create a temp geometry factory to get a test geometries
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

        // Construct the aggregations
        this.aggregation1 = new AtonAggregation();
        this.aggregation1.setId(BigInteger.ONE);
        this.aggregation1.setCategoryOfAggregationType(CategoryOfAggregationType.LEADING_LINE);
        this.aggregation2 = new AtonAggregation();
        this.aggregation2.setId(BigInteger.ONE);
        this.aggregation2.setCategoryOfAggregationType(CategoryOfAggregationType.LEADING_LINE);

        // Initialise the AtoN messages list
        List<AidsToNavigation> aidsToNavigationList = new ArrayList<>();
        for(long i=0; i<2; i++) {
            AidsToNavigation aidsToNavigation = new BeaconCardinal();
            aidsToNavigation.setId(BigInteger.valueOf(i));
            aidsToNavigation.setIdCode("ID"+i);
            aidsToNavigation.setGeometry(factory.createPoint(new Coordinate(i%180, i%90)));
            // Add the feature name entries
            FeatureName featureName = new FeatureName();
            featureName.setName("Aton No" + i);
            aidsToNavigation.setFeatureNames(Collections.singleton(featureName));
            // Add the information entries
            AtonStatusInformation information = new AtonStatusInformation();
            information.setText("Description of AtoN No" + i);
            information.setChangeTypes(ChangeTypesType.TEMPORARY_CHANGES);
            aidsToNavigation.setInformations(Collections.singleton(information));
            aidsToNavigationList.add(aidsToNavigation);
        }

        // Set the list as peers to the aggregations
        this.aggregation1.setPeers(new HashSet<>(aidsToNavigationList));
        Collections.reverse(aidsToNavigationList);
        this.aggregation2.setPeers(new HashSet<>(aidsToNavigationList));
    }

    /**
     * Test that although two aggregations might be constructed differently,
     * they will appear equal if their type and peers are the same.
     */
    @Test
    void testEquals() {
        assertTrue(this.aggregation1.equals(this.aggregation2));
    }

    /**
     * Test that although two aggregations might be constructed differently,
     * the produced hash-codes will be the same if the peers are the same.
     */
    @Test
    void testHashCode() {
        assertEquals(this.aggregation1.hashCode(), this.aggregation2.hashCode());
    }

}