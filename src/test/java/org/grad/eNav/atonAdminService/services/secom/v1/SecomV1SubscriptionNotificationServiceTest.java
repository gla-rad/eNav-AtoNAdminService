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

package org.grad.eNav.atonAdminService.services.secom.v1;

import org.grad.secom.core.models.SubscriptionNotificationObject;
import org.grad.secom.core.models.SubscriptionNotificationResponseObject;
import org.grad.secom.core.models.enums.SubscriptionEventEnum;
import org.grad.secom.springboot3.components.SecomClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecomV1SubscriptionNotificationServiceTest {

    /**
     * The Tested Service.
     */
    @InjectMocks
    @Spy
    SecomV1SubscriptionNotificationService secomV1SubscriptionNotificationService;

    /**
     * The SECOM v1 Service mock.
     */
    @Mock
    SecomV1Service secomV1Service;

    // Test Variables
    UUID subscriptionIdentifier;
    SecomClient secomClient;
    SubscriptionNotificationResponseObject subscriptionNotificationResponseObject;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setUp() {
        // Create a subscription identifier
        this.subscriptionIdentifier = UUID.randomUUID();

        // Create the SubscriptionNotificationResponseObject
        this.subscriptionNotificationResponseObject = new SubscriptionNotificationResponseObject();
        this.subscriptionNotificationResponseObject.setResponseText("Subscription Notification Response");

        // And mock a SECOM client
        this.secomClient = mock(SecomClient.class);
        doReturn(Optional.of(this.subscriptionNotificationResponseObject)).when(this.secomClient).subscriptionNotification(any());
    }

    /**
     * Test that the SECOM Subscription Notification Service can send the
     * CREATED subscription notifications correctly to the clients discovered by
     * the SECOM discovery service.
     */
    @Test
    void testSendNotificationCreated() {
        // Mock the SECOM client generation
        doReturn(this.secomClient).when(this.secomV1Service).getClient(any(String.class));

        // Perform the service call
        this.secomV1SubscriptionNotificationService.sendNotification(
                "urn:mrn:org:test",
                this.subscriptionIdentifier,
                SubscriptionEventEnum.SUBSCRIPTION_CREATED
        ).whenCompleteAsync((result, error) -> {
            // Verify that we send the subscription notifications in the proper way
            ArgumentCaptor<SubscriptionNotificationObject> subscriptionNotificationObjectArgument = ArgumentCaptor.forClass(SubscriptionNotificationObject.class);
            verify(this.secomClient).subscriptionNotification(subscriptionNotificationObjectArgument.capture());
            assertNotNull(subscriptionNotificationObjectArgument.getValue());
            assertEquals(this.subscriptionIdentifier, subscriptionNotificationObjectArgument.getValue().getSubscriptionIdentifier());
            assertEquals(SubscriptionEventEnum.SUBSCRIPTION_CREATED, subscriptionNotificationObjectArgument.getValue().getEventEnum());

            // Make sure the response seems OK
            assertNotNull(result);
            assertEquals(this.subscriptionNotificationResponseObject.getResponseText(), result.getResponseText());
        });
    }

    /**
     * Test that the SECOM Subscription Notification Service can send the
     * REMOVED subscription notifications correctly to the clients discovered by
     * the SECOM discovery service.
     */
    @Test
    void testSendNotificationRemoved() {
        // Mock the SECOM client generation
        doReturn(this.secomClient).when(this.secomV1Service).getClient(any(String.class));

        // Perform the service call
        this.secomV1SubscriptionNotificationService.sendNotification(
                "urn:mrn:org:test",
                this.subscriptionIdentifier,
                SubscriptionEventEnum.SUBSCRIPTION_REMOVED
        ).whenCompleteAsync((result, error) -> {
            // Verify that we send the subscription notifications in the proper way
            ArgumentCaptor<SubscriptionNotificationObject> subscriptionNotificationObjectArgument = ArgumentCaptor.forClass(SubscriptionNotificationObject.class);
            verify(this.secomClient).subscriptionNotification(subscriptionNotificationObjectArgument.capture());
            assertNotNull(subscriptionNotificationObjectArgument.getValue());
            assertEquals(this.subscriptionIdentifier, subscriptionNotificationObjectArgument.getValue().getSubscriptionIdentifier());
            assertEquals(SubscriptionEventEnum.SUBSCRIPTION_REMOVED, subscriptionNotificationObjectArgument.getValue().getEventEnum());

            // Make sure the response seems OK
            assertNotNull(result);
            assertEquals(this.subscriptionNotificationResponseObject.getResponseText(), result.getResponseText());
        });
    }

}