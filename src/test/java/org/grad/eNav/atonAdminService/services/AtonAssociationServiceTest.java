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

import _int.iho.s_201.gml.cs0._2.CategoryOfAssociationType;
import _int.iho.s_201.gml.cs0._2.ChangeTypesType;
import org.grad.eNav.atonAdminService.exceptions.DataNotFoundException;
import org.grad.eNav.atonAdminService.models.domain.s201.*;
import org.grad.eNav.atonAdminService.repos.AidsToNavigationRepo;
import org.grad.eNav.atonAdminService.repos.AtonAssociationRepo;
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
class AtonAssociationServiceTest {

    /**
     * The Tested Service.
     */
    @InjectMocks
    @Spy
    AssociationService associationService;

    /**
     * The Association Repo mock.
     */
    @Mock
    AtonAssociationRepo atonAssociationRepo;

    /**
     * The Aids to Navigation Repo mock.
     */
    @Mock
    AidsToNavigationRepo aidsToNavigationRepo;

    // Test Variables
    private AtonAssociation association;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setup() {
        // Create a temp geometry factory to get a test geometries
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

        this.association = new AtonAssociation();
        this.association.setId(BigInteger.ONE);
        this.association.setCategoryOfAssociation(CategoryOfAssociationType.DANGER_MARKINGS);

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
            AtonStatusInformation information = new AtonStatusInformation();
            information.setText("Description of AtoN No" + i);
            information.setChangeTypes(ChangeTypesType.TEMPORARY_CHANGES);
            aidsToNavigation.setInformations(Collections.singleton(information));
            this.association.getAtonAssociationBies().add(aidsToNavigation);
        }
    }

    /**
     * Test that we can save correctly a new or existing association entry if
     * all the validation checks are successful.
     */
    @Test
    void testSave() {
        doReturn(this.association).when(this.atonAssociationRepo).save(any());

        // Perform the service call
        AtonAssociation result = this.associationService.save(this.association);

        // Test the result
        assertNotNull(result);
        assertEquals(this.association.getId(), result.getId());
        assertEquals(this.association.getCategoryOfAssociation(), result.getCategoryOfAssociation());
        assertNotNull(result.getAtonAssociationBies());
        assertEquals(this.association.getAtonAssociationBies().size(), result.getAtonAssociationBies().size());
        assertTrue(result.getAtonAssociationBies().containsAll(this.association.getAtonAssociationBies()));

        // Also, that a saving call took place in the repository
        verify(this.atonAssociationRepo, times(1)).save(this.association);
    }

    /**
     * Test that we can successfully delete an existing association entry.
     */
    @Test
    void testDelete() throws DataNotFoundException {
        doReturn(Optional.of(this.association)).when(this.atonAssociationRepo).findById(this.association.getId());
        doNothing().when(this.atonAssociationRepo).delete(this.association);

        // Perform the service call
        this.associationService.delete(this.association.getId());

        // Verify that a deletion call took place in the repository
        verify(this.atonAssociationRepo, times(1)).delete(this.association);
    }

    /**
     * Test that we can update all the relevant associations of an AtoN based
     * on it's number. Because associations have an issue with the IDs coming
     * from the S-201 datasets, i.e. we cannot deterministically identify
     * which is which, we will keep the unchanged ones, but changes will already
     * create a new association and delete any old versions.
     */
    @Test
    void testUpdateAidsToNavigationAssociations() {
        doReturn(Collections.emptySet()).when(this.atonAssociationRepo).findByIncludedIdCode(any());
        doAnswer((inv) ->
                this.association.getAtonAssociationBies()
                        .stream()
                        .filter(aton -> Objects.equals(aton.getIdCode(), inv.getArgument(0)))
                        .findFirst()
        ).when(this.aidsToNavigationRepo).findByIdCode(any());
        doAnswer((inv) -> inv.getArgument(0)).when(this.associationService).save(any());

        // Perform the service  call
        final Set<AtonAssociation> result = this.associationService.updateAidsToNavigationAssociations("aton-number", Collections.singleton(this.association));

        // Now make sure the response is as expected
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        // Inspect the included aggregation
        final AtonAssociation resultAssociation = result.stream().findFirst().orElse(null);
        assertNotNull(resultAssociation);
        assertEquals(this.association.getId(), resultAssociation.getId());
        assertEquals(this.association.getCategoryOfAssociation(), resultAssociation.getCategoryOfAssociation());
        assertNotNull(resultAssociation.getAtonAssociationBies());
        assertFalse(resultAssociation.getAtonAssociationBies().isEmpty());
        assertEquals(this.association.getAtonAssociationBies().size(), resultAssociation.getAtonAssociationBies().size());
        assertTrue(this.association.getAtonAssociationBies().containsAll(resultAssociation.getAtonAssociationBies()));
    }

}