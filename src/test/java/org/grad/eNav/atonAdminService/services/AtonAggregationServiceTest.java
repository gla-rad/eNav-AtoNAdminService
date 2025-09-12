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

package org.grad.eNav.atonAdminService.services;

import _int.iho.s_201.gml.cs0._2.CategoryOfAggregationType;
import org.grad.eNav.atonAdminService.exceptions.DataNotFoundException;
import org.grad.eNav.atonAdminService.models.domain.s201.*;
import org.grad.eNav.atonAdminService.repos.AtonAggregationRepo;
import org.grad.eNav.atonAdminService.repos.AidsToNavigationRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtonAggregationServiceTest {

    /**
     * The Tested Service.
     */
    @InjectMocks
    @Spy
    AggregationService aggregationService;

    /**
     * The Aggregation Repo mock.
     */
    @Mock
    AtonAggregationRepo atonAggregationRepo;

    /**
     * The Aids to Navigation Repo mock.
     */
    @Mock
    AidsToNavigationRepo aidsToNavigationRepo;

    // Test Variables
    private AtonAggregation aggregation;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setup() {
        // Create a temp geometry factory to get a test geometries
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

        this.aggregation = new AtonAggregation();
        this.aggregation.setId(BigInteger.ONE);
        this.aggregation.setCategoryOfAggregationType(CategoryOfAggregationType.LEADING_LINE);

        // Initialise the AtoN messages list
        for(long i=0; i<5; i++) {
            AidsToNavigation aidsToNavigation = new BeaconCardinal();
            aidsToNavigation.setId(BigInteger.valueOf(i));
            aidsToNavigation.setIdCode("ID"+i);
            aidsToNavigation.setGeometry(factory.createPoint(new Coordinate(i%180, i%90)));
            // Add the feature name entries
            FeatureName featureName = new FeatureName();
            featureName.setName("Aton No" + i);
            aidsToNavigation.setFeatureNames(Collections.singleton(featureName));
            // Add the information entries
            Information information = new Information();
            information.setText("Description of AtoN No" + i);
            aidsToNavigation.setInformations(Collections.singleton(information));
            this.aggregation.getPeers().add(aidsToNavigation);
        }
    }

    /**
     * Test that we can save correctly a new or existing aggregation entry if
     * all the validation checks are successful.
     */
    @Test
    void testSave() {
        doReturn(this.aggregation).when(this.atonAggregationRepo).save(any());

        // Perform the service call
        AtonAggregation result = this.aggregationService.save(this.aggregation);

        // Test the result
        assertNotNull(result);
        assertEquals(this.aggregation.getId(), result.getId());
        assertEquals(this.aggregation.getCategoryOfAggregationType(), result.getCategoryOfAggregationType());
        assertNotNull(result.getPeers());
        assertEquals(this.aggregation.getPeers().size(), result.getPeers().size());
        assertTrue(result.getPeers().containsAll(this.aggregation.getPeers()));

        // Also, that a saving call took place in the repository
        verify(this.atonAggregationRepo, times(1)).save(this.aggregation);
    }

    /**
     * Test that we can successfully delete an existing aggregation entry.
     */
    @Test
    void testDelete() throws DataNotFoundException {
        doReturn(Optional.of(this.aggregation)).when(this.atonAggregationRepo).findById(this.aggregation.getId());
        doNothing().when(this.atonAggregationRepo).delete(this.aggregation);

        // Perform the service call
        this.aggregationService.delete(this.aggregation.getId());

        // Verify that a deletion call took place in the repository
        verify(this.atonAggregationRepo, times(1)).delete(this.aggregation);
    }

    /**
     * Test that we can update all the relevant aggregations of an AtoN based
     * on it's number. Because aggregations have an issue with the IDs coming
     * from the S-201 datasets, i.e. we cannot deterministically identify
     * which is which, we will keep the unchanged ones, but changes will already
     * create a new aggregation and delete any old versions.
     */
    @Test
    void testUpdateAidsToNavigationAggregations() {
        doReturn(Collections.emptySet()).when(this.atonAggregationRepo).findByIncludedIdCode(any());
        doAnswer((inv) ->
                this.aggregation.getPeers()
                        .stream()
                        .filter(aton -> Objects.equals(aton.getIdCode(), inv.getArgument(0)))
                        .findFirst()
        ).when(this.aidsToNavigationRepo).findByIdCode(any());
        doAnswer((inv) -> inv.getArgument(0)).when(this.aggregationService).save(any());

        // Perform the service  call
        final Set<AtonAggregation> result = this.aggregationService.updateAidsToNavigationAggregations("aton-number", Collections.singleton(this.aggregation));

        // Now make sure the response is as expected
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        // Inspect the included aggregation
        final AtonAggregation resultAggregation = result.stream().findFirst().orElse(null);
        assertNotNull(resultAggregation);
        assertEquals(this.aggregation.getId(), resultAggregation.getId());
        assertEquals(this.aggregation.getCategoryOfAggregationType(), resultAggregation.getCategoryOfAggregationType());
        assertNotNull(resultAggregation.getPeers());
        assertFalse(resultAggregation.getPeers().isEmpty());
        assertEquals(this.aggregation.getPeers().size(), resultAggregation.getPeers().size());
        assertTrue(this.aggregation.getPeers().containsAll(resultAggregation.getPeers()));
    }

}