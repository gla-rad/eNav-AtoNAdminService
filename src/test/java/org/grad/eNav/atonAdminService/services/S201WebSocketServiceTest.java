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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.grad.eNav.atonAdminService.models.domain.s201.AidsToNavigation;
import org.grad.eNav.atonAdminService.models.domain.s201.BeaconCardinal;
import org.grad.eNav.atonAdminService.models.domain.s201.FeatureName;
import org.grad.eNav.atonAdminService.models.domain.s201.Information;
import org.grad.eNav.atonAdminService.models.enums.DatasetOperation;
import org.grad.secom.core.models.enums.SECOM_DataProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S201WebSocketServiceTest {

    /**
     * The Tested Service.
     */
    @InjectMocks
    @Spy
    S201WebSocketService s201WebSocketService;

    /**
     * The AtoN Information Publish Subscribe Channel mock.
     */
    @Mock
    PublishSubscribeChannel atonPublicationChannel;

    /**
     * The Web Socket mock.
     */
    @Mock
    SimpMessagingTemplate webSocket;

    // Test Variables
    private AidsToNavigation aidsToNavigation;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setup() throws IOException {
        // Create a temp geometry factory to get a test geometries
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

        // Create a new AtoN message
        this.aidsToNavigation = new BeaconCardinal();
        this.aidsToNavigation.setId(BigInteger.valueOf(1));
        this.aidsToNavigation.setIdCode("ID001");
        this.aidsToNavigation.setGeometry(factory.createPoint(new Coordinate(53.61, 1.594)));
        // Add the feature name entries
        FeatureName featureName = new FeatureName();
        featureName.setName("Aton No1 ");
        aidsToNavigation.setFeatureNames(Collections.singleton(featureName));
        // Add the information entries
        Information information = new Information();
        information.setText("Description of AtoN No 1");
        aidsToNavigation.setInformations(Collections.singleton(information));

        // Also set the web-socket service topic prefix
        this.s201WebSocketService.prefix = "topic";
        this.s201WebSocketService.objectMapper = new ObjectMapper();
    }

    /**
     * Test that the S-201 web-socket service gets initialised correctly,
     * and it subscribes to the AtoN publish subscribe channel.
     */
    @Test
    void testInit() {
        // Perform the service call
        this.s201WebSocketService.init();

        verify(this.atonPublicationChannel, times(1)).subscribe(this.s201WebSocketService);
    }

    /**
     * Test that the S201 web-socket service gets destroyed correctly,
     * and it un-subscribes from the S-201 publish subscribe channel.
     */
    @Test
    void testDestroy() {
        // Perform the service call
        this.s201WebSocketService.destroy();

        verify(this.atonPublicationChannel, times(1)).destroy();
    }

    /**
     * Test that the Web-Socket controlling service can process correctly the
     * Aids to Navigation messages published in the S-201 publish-subscribe channel.
     */
    @Test
    void testHandleAidsToNavigationMessage() throws IOException {
        // Create a message to be handled
        Message<?> message = Optional.of(this.aidsToNavigation).map(MessageBuilder::withPayload)
                .map(builder -> builder.setHeader(MessageHeaders.CONTENT_TYPE, SECOM_DataProductType.S201))
                .map(builder -> builder.setHeader("operation", DatasetOperation.CREATED))
                .map(MessageBuilder::build)
                .orElse(null);

        // Perform the service call
        this.s201WebSocketService.handleMessage(message);

        // Verify that we send a packet to the web-socket and get that message
        ArgumentCaptor<String> topicArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<AidsToNavigation> payLoadArgument = ArgumentCaptor.forClass(AidsToNavigation.class);
        verify(this.webSocket, times(1)).convertAndSend(topicArgument.capture(), payLoadArgument.capture());

        // Verify the message
        assertEquals("/topic/S201", topicArgument.getValue());
        assertNotNull(payLoadArgument.getValue());
        assertEquals(this.aidsToNavigation.getId(), payLoadArgument.getValue().getId());
        assertEquals(this.aidsToNavigation.getIdCode(), payLoadArgument.getValue().getIdCode());
        assertEquals(this.aidsToNavigation.getInformations().size(), payLoadArgument.getValue().getInformations().size());
        assertEquals(this.aidsToNavigation.getInformations().stream().findFirst().map(Information::getFileLocator).orElse(null),
                payLoadArgument.getValue().getInformations().stream().findFirst().map(Information::getFileLocator).orElse(null));
        assertEquals(this.aidsToNavigation.getInformations().stream().findFirst().map(Information::getFileReference).orElse(null),
                payLoadArgument.getValue().getInformations().stream().findFirst().map(Information::getFileReference).orElse(null));
        assertEquals(this.aidsToNavigation.getInformations().stream().findFirst().map(Information::getHeadline).orElse(null),
                payLoadArgument.getValue().getInformations().stream().findFirst().map(Information::getHeadline).orElse(null));
        assertEquals(this.aidsToNavigation.getInformations().stream().findFirst().map(Information::getLanguage).orElse(null),
                payLoadArgument.getValue().getInformations().stream().findFirst().map(Information::getLanguage).orElse(null));
        assertEquals(this.aidsToNavigation.getInformations().stream().findFirst().map(Information::getText).orElse(null),
                payLoadArgument.getValue().getInformations().stream().findFirst().map(Information::getText).orElse(null));
        assertEquals(this.aidsToNavigation.getFeatureNames().size(), payLoadArgument.getValue().getFeatureNames().size());
        assertEquals(this.aidsToNavigation.getFeatureNames().stream().findFirst().map(FeatureName::getName).orElse(null),
                payLoadArgument.getValue().getFeatureNames().stream().findFirst().map(FeatureName::getName).orElse(null));
        assertEquals(this.aidsToNavigation.getFeatureNames().stream().findFirst().map(FeatureName::getDisplayName).orElse(null),
                payLoadArgument.getValue().getFeatureNames().stream().findFirst().map(FeatureName::getDisplayName).orElse(null));
        assertEquals(this.aidsToNavigation.getFeatureNames().stream().findFirst().map(FeatureName::getLanguage).orElse(null),
                payLoadArgument.getValue().getFeatureNames().stream().findFirst().map(FeatureName::getLanguage).orElse(null));
    }

    /**
     * Test that the Web-Socket controlling service can process correctly the
     * simple string messages published in the AtoN publish-subscribe channel.
     */
    @Test
    void testHandleStringMessage() throws IOException {
        /// Create a message to be handled
        Message<?> message = Optional.of(this.aidsToNavigation).map(MessageBuilder::withPayload)
                .map(builder -> builder.setHeader(MessageHeaders.CONTENT_TYPE, SECOM_DataProductType.S201))
                .map(builder -> builder.setHeader("operation", DatasetOperation.DELETED))
                .map(MessageBuilder::build)
                .orElse(null);

        // Perform the service call
        this.s201WebSocketService.handleMessage(message);

        // Verify that we send a packet to the VDES port and get that packet
        ArgumentCaptor<String> topicArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<AidsToNavigation> payLoadArgument = ArgumentCaptor.forClass(AidsToNavigation.class);
        verify(this.webSocket, times(1)).convertAndSend(topicArgument.capture(), payLoadArgument.capture());

        // Verify the packet
        assertEquals("/topic/deletions/S201", topicArgument.getValue());
        assertNotNull(payLoadArgument.getValue());
        assertEquals(this.aidsToNavigation.getId(), payLoadArgument.getValue().getId());
        assertEquals(this.aidsToNavigation.getIdCode(), payLoadArgument.getValue().getIdCode());
        assertEquals(this.aidsToNavigation.getInformations().size(), payLoadArgument.getValue().getInformations().size());
        assertEquals(this.aidsToNavigation.getInformations().stream().findFirst().map(Information::getFileLocator).orElse(null),
                payLoadArgument.getValue().getInformations().stream().findFirst().map(Information::getFileLocator).orElse(null));
        assertEquals(this.aidsToNavigation.getInformations().stream().findFirst().map(Information::getFileReference).orElse(null),
                payLoadArgument.getValue().getInformations().stream().findFirst().map(Information::getFileReference).orElse(null));
        assertEquals(this.aidsToNavigation.getInformations().stream().findFirst().map(Information::getHeadline).orElse(null),
                payLoadArgument.getValue().getInformations().stream().findFirst().map(Information::getHeadline).orElse(null));
        assertEquals(this.aidsToNavigation.getInformations().stream().findFirst().map(Information::getLanguage).orElse(null),
                payLoadArgument.getValue().getInformations().stream().findFirst().map(Information::getLanguage).orElse(null));
        assertEquals(this.aidsToNavigation.getInformations().stream().findFirst().map(Information::getText).orElse(null),
                payLoadArgument.getValue().getInformations().stream().findFirst().map(Information::getText).orElse(null));
        assertEquals(this.aidsToNavigation.getFeatureNames().size(), payLoadArgument.getValue().getFeatureNames().size());
        assertEquals(this.aidsToNavigation.getFeatureNames().stream().findFirst().map(FeatureName::getName).orElse(null),
                payLoadArgument.getValue().getFeatureNames().stream().findFirst().map(FeatureName::getName).orElse(null));
        assertEquals(this.aidsToNavigation.getFeatureNames().stream().findFirst().map(FeatureName::getDisplayName).orElse(null),
                payLoadArgument.getValue().getFeatureNames().stream().findFirst().map(FeatureName::getDisplayName).orElse(null));
        assertEquals(this.aidsToNavigation.getFeatureNames().stream().findFirst().map(FeatureName::getLanguage).orElse(null),
                payLoadArgument.getValue().getFeatureNames().stream().findFirst().map(FeatureName::getLanguage).orElse(null));
    }

    /**
     * Test that we can only send S-201 messages down to the web-socket.
     */
    @Test
    void testHandleMessageWrongPayload() throws IOException {
        // Change the message content type to something else
        Message<?> message = Optional.of(Integer.MAX_VALUE).map(MessageBuilder::withPayload)
                .map(builder -> builder.setHeader(MessageHeaders.CONTENT_TYPE, SECOM_DataProductType.S201))
                .map(MessageBuilder::build)
                .orElse(null);

        // Perform the service call
        this.s201WebSocketService.handleMessage(message);

        // Verify that we didn't send any packets to the VDES port
        verify(this.webSocket, never()).convertAndSend(any(String.class), any(Object.class));
    }

}