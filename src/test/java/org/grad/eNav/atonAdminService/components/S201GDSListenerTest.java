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

package org.grad.eNav.atonAdminService.components;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.IOUtils;
import org.geotools.api.data.DataStore;
import org.geotools.api.data.FeatureEvent;
import org.geotools.api.data.FeatureListener;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.filter.FidFilterImpl;
import org.geotools.filter.text.cql2.CQLException;
import org.grad.eNav.atonAdminService.config.GlobalConfig;
import org.grad.eNav.atonAdminService.models.GeomesaS201;
import org.grad.eNav.atonAdminService.models.domain.s201.*;
import org.grad.eNav.atonAdminService.models.dtos.S201Node;
import org.grad.eNav.atonAdminService.services.AidsToNavigationService;
import org.grad.eNav.atonAdminService.services.DatasetService;
import org.grad.eNav.atonAdminService.utils.GeoJSONUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.geomesa.kafka.utils.KafkaFeatureEvent;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.Message;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S201GDSListenerTest {

    /**
     * The Tested Component.
     */
    @InjectMocks
    @Spy
    S201GDSListener s201GDSListener;

    /**
     * The Model Mapper.
     */
    @Spy
    ModelMapper modelMapper;

    /**
     * The Aids to Navigation Service mock.
     */
    @Mock
    AidsToNavigationService aidsToNavigationService;

    /**
     * The Dataset Service mock.
     */
    @Mock
    DatasetService datasetService;

    /**
     * The AtoN Information Channel to publish the published data to.
     */
    @Mock
    PublishSubscribeChannel atonPublicationChannel;

    /**
     * The AtoN Information Channel to publish the deleted data to.
     */
    @Mock
    PublishSubscribeChannel atonDeletionChannel;

    // Test Variables
    private Geometry geometry;
    private S201Node s201Node;
    private S201Dataset s201DataSet;

    // Geomesa Variables
    private GeometryFactory geometryFactory;
    private GeomesaS201 geomesaData;
    private SimpleFeatureSource featureSource;
    private DataStore consumer;
    private Set<FeatureListener> featureListeners;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setup() throws IOException, CQLException {
        // Create a temp geometry factory to get a test geometries
        this.geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

        // Create a station with an ID
        this.geometry = geometryFactory.createPolygon(new Coordinate[] {
                new Coordinate(-180, -90),
                new Coordinate(-180, 90),
                new Coordinate(180, 90),
                new Coordinate(180, -90),
                new Coordinate(-180, -90)
        });

        // Read a valid S-201 content to generate the S201Node message for.
        InputStream in = new ClassPathResource("s201-msg.xml").getInputStream();
        String xml = IOUtils.toString(in, StandardCharsets.UTF_8);

        // Also create a GeoJSON point geometry for our S-201 message
        JsonNode point = GeoJSONUtils.createGeoJSON(53.61, 1.594);

        // Now create the S201Node object and populate the data
        this.s201Node = new S201Node("test_aton", point, xml);
        this.geomesaData = new GeomesaS201(this.geometry);
        this.s201DataSet = new S201Dataset("test_aton_dataset");

        // Also mock the GeoMesa DataStore data and consumer
        this.featureSource = mock(SimpleFeatureSource.class);
        this.consumer = mock(DataStore.class);

        // Specify the mock behaviour
        doReturn(this.featureSource).when(this.consumer).getFeatureSource("S201");

        // Keep a local record of all added feature listeners
        featureListeners = new HashSet<>();
        doAnswer((inv) -> {
            this.featureListeners.add(inv.getArgument(0));
            return null;
        }).when(this.featureSource).addFeatureListener(any(FeatureListener.class));
    }

    /**
     * Test that the S-201 Geomesa DataStore Listener can initialise correctly.
     */
    @Test
    void testInit() throws IOException {
        // Init the component
        this.s201GDSListener.init(this.consumer, this.geomesaData, this.geometry);

        // Make sure the initialisation was successful
        assertEquals(this.s201GDSListener.consumer, this.consumer);
        assertEquals(this.s201GDSListener.geomesaData, this.geomesaData);
        assertEquals(this.s201GDSListener.geometry, this.geometry);
        assertNotNull(this.s201GDSListener.modelMapper);
        assertEquals(1, this.featureListeners.size());
    }

    /**
     * Test that when the S-201 Geomesa DataStore Listener gets destroyed, then
     * the feature listeners are cleared from the object's feature source
     */
    @Test
    void testDestroy() throws IOException {
       // Don't forget the removal of the listener from the local list
        doAnswer((inv) -> {
            this.featureListeners.remove(inv.getArgument(0));
            return null;
        }).when(this.featureSource).removeFeatureListener(any(FeatureListener.class));

        // Init and perform the component call
        this.s201GDSListener.init(this.consumer, this.geomesaData, this.geometry);
        this.s201GDSListener.destroy();

        // Assert that the feature listeners list is empty
        assertEquals(0, this.featureListeners.size());
    }

    /**
     * Test that the S-201 Geomesa Listener can correctly handle the incoming
     * S-201 Geomesa change events, and if so, it will save the S-201 message
     * entries in the database and send them to the AtoN publish-subscribe
     * channel.
     */
    @Test
    void testListenToEventsChangedSendAndSaved() throws IOException {
        // Translate our S201Node to a feature list
        List<SimpleFeature> simpleFeatureList = this.geomesaData.getFeatureData(Collections.singletonList(this.s201Node));

        // Mock the service calls
        doAnswer((inv) -> inv.getArgument(0)).when(this.aidsToNavigationService).save(any());

        // Mock a new event
        KafkaFeatureEvent.KafkaFeatureChanged featureEvent = mock(KafkaFeatureEvent.KafkaFeatureChanged.class);
        doReturn(FeatureEvent.Type.CHANGED).when(featureEvent).getType();
        doReturn(simpleFeatureList.stream().findFirst().orElse(null)).when(featureEvent).feature();

        // Add a matching dataset
        doReturn(new PageImpl<>(Collections.singletonList(this.s201DataSet), Pageable.ofSize(1), 1))
                .when(this.datasetService).findAll(isNull(), any(), isNull(), isNull(), any(), any());

        // We need to use the actual Spring model mapper to pick up the type-maps
        this.s201GDSListener.modelMapper = new GlobalConfig().modelMapper();

        // Init and perform the component call
        this.s201GDSListener.init(this.consumer, this.geomesaData, this.geometry);
        this.s201GDSListener.changed(featureEvent);

        // Verify that our message was saved and sent
        verify(this.atonPublicationChannel, times(1)).send(any(Message.class));
        verify(this.datasetService, times(1)).findAll(isNull(), any(), isNull(), isNull(), any(), any());
        verify(this.datasetService, times(1)).requestDatasetContentUpdate(eq(this.s201DataSet.getUuid()));
    }

    /**
     * Test that the S-201 Geomesa Listener can correctly handle the incoming
     * S-201 Geomesa change events, but it will not act on them if the fall
     * outside the listeners's coverage area.
     */
    @Test
    void testListenToEventsChangedOutsideListenerArea() throws IOException {
        // Change the stations coverage area
        this.geometry = geometryFactory.createPolygon(new Coordinate[] {
                new Coordinate(-0, -0),
                new Coordinate(-0, 0),
                new Coordinate(0, 0),
        });
        this.geomesaData = new GeomesaS201(this.geometry);

        // Translate our S201Node to a feature list
        List<SimpleFeature> simpleFeatureList = this.geomesaData.getFeatureData(Collections.singletonList(this.s201Node));

        // Mock a new event
        KafkaFeatureEvent.KafkaFeatureChanged featureEvent = mock(KafkaFeatureEvent.KafkaFeatureChanged.class);
        doReturn(FeatureEvent.Type.CHANGED).when(featureEvent).getType();
        doReturn(simpleFeatureList.stream().findFirst().orElse(null)).when(featureEvent).feature();

        // We need to use the actual Spring model mapper to pick up the type-maps
        this.s201GDSListener.modelMapper = new GlobalConfig().modelMapper();

        // Init and perform the component call
        this.s201GDSListener.init(this.consumer, this.geomesaData, this.geometry);
        this.s201GDSListener.changed(featureEvent);

        // Verify that our message was not saved or sent
        verify(this.atonPublicationChannel, never()).send(any(Message.class));
        verify(this.datasetService, never()).findAll(any(), any(), any(), any(), any(), any());
        verify(this.datasetService, never()).requestDatasetContentUpdate(any());

    }

    /**
     * Test that the S-201 Geomesa Listener, if initialised correctly as a
     * deletion handler, it can correctly handle the incoming S-201 Geomesa
     * delete events, regardless of the coverage area and will delete the
     * applicable S-201 station nodes.
     */
    @Test
    void testListenToEventsRemoved() throws IOException {
        AidsToNavigation aidsToNavigation = new BeaconCardinal();
        aidsToNavigation.setGeometry(this.geometryFactory.createPoint(new Coordinate(0, 0)));

        // Mock the service calls
        doReturn(Optional.of(aidsToNavigation)).when(this.aidsToNavigationService).findByIdCode(any());
        doReturn(aidsToNavigation).when(this.aidsToNavigationService).delete(any());

        // Mock a new event
        FidFilterImpl filter = mock(FidFilterImpl.class);
        doReturn(Collections.singleton(this.s201Node.getAtonUID())).when(filter).getFidsSet();
        KafkaFeatureEvent.KafkaFeatureRemoved featureEvent = mock(KafkaFeatureEvent.KafkaFeatureRemoved.class);
        doReturn(FeatureEvent.Type.REMOVED).when(featureEvent).getType();
        doReturn(filter).when(featureEvent).getFilter();

        // Add a matching dataset
        doReturn(new PageImpl<>(Collections.singletonList(this.s201DataSet), Pageable.ofSize(1), 1))
                .when(this.datasetService).findAll(isNull(), any(), isNull(), isNull(), any(), any());

        // Init and perform the component call
        this.s201GDSListener.init(this.consumer, this.geomesaData, this.geometry);
        this.s201GDSListener.changed(featureEvent);

        // Make sure the evaluation works
        verify(this.atonDeletionChannel, times(1)).send(any(Message.class));
        verify(this.datasetService, times(1)).findAll(isNull(), any(), isNull(), isNull(), any(), any());
        verify(this.datasetService, times(1)).requestDatasetContentUpdate(eq(this.s201DataSet.getUuid()));
    }

    /**
     * Test that we can correctly parse the S-201 datasets including aggregation
     * and association links.
     */
    @Test
    void testParseS201Dataset() throws IOException {
        // Init and perform the component call
        this.s201GDSListener.init(this.consumer, this.geomesaData, this.geometry);
        this.s201GDSListener.modelMapper = new GlobalConfig().modelMapper();

        // Parse the S-201 dataset
        List<? extends AidsToNavigation> aidsToNavigation = this.s201GDSListener.parseS201Dataset(this.s201Node)
                .toList();

        // Make sure it looks fine
        assertNotNull(aidsToNavigation);
        assertFalse(aidsToNavigation.isEmpty());
        assertEquals(1, aidsToNavigation.size());
        assertEquals(VirtualAISAidToNavigation.class, aidsToNavigation.getFirst().getClass());
        assertNotNull(aidsToNavigation.getFirst().getPeerAtonAggregations());
        assertFalse(aidsToNavigation.getFirst().getPeerAtonAggregations().isEmpty());
        for(AtonAggregation aggr : aidsToNavigation.getFirst().getPeerAtonAggregations()) {
            System.out.println("Aggregation Info: "+ aggr.getId() + aggr.getCategoryOfAggregationType());
        }
        assertEquals(1, aidsToNavigation.getFirst().getPeerAtonAggregations().size());
        assertNotNull(aidsToNavigation.getFirst().getPeerAtonAggregations().stream().findFirst().map(AtonAggregation::getAtonAggregationBies).orElse(null));
        assertEquals(1, aidsToNavigation.getFirst().getPeerAtonAggregations().stream().findFirst().map(AtonAggregation::getAtonAggregationBies).stream().count());
        assertNotNull(aidsToNavigation.getFirst().getPeerAtonAssociations());
        assertFalse(aidsToNavigation.getFirst().getPeerAtonAggregations().isEmpty());
        assertEquals(1, aidsToNavigation.getFirst().getPeerAtonAggregations().size());
        assertNotNull(aidsToNavigation.getFirst().getPeerAtonAggregations().stream().findFirst().map(AtonAggregation::getAtonAggregationBies).orElse(null));
        assertEquals(1, aidsToNavigation.getFirst().getPeerAtonAggregations().stream().findFirst().map(AtonAggregation::getAtonAggregationBies).stream().count());
    }
}