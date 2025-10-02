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

package org.grad.eNav.atonAdminService.services.secom.v2;

import lombok.extern.slf4j.Slf4j;
import org.grad.secomv2.core.models.SubscriptionNotificationObject;
import org.grad.secomv2.core.models.SubscriptionNotificationResponseObject;
import org.grad.secomv2.core.models.enums.SubscriptionEventEnum;
import org.grad.secomv2.springboot3.components.SecomClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The SECOM v2 Subscription Notification Service Class.
 * <p/>
 * A service handles the subscription notifications in an asynchronous way so
 * that the responses can go back to the clients regardless of the respective
 * subscription requests.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Service
@Slf4j
public class SecomV2SubscriptionNotificationService {

    /**
     * The SECOM v2 Service.
     */
    @Autowired
    SecomV2Service secomV2Service;

    /**
     * Sends a SECOM subscription notification response object to the SECOM
     * client specified by the provided MRN. The response is generted for the
     * UUID and subscription event type provided in the input parameterr.
     *
     * @param mrn                       the client MRN to be informed
     * @param subscriptionIdentifier    the subscription identifier UUID
     * @param subscriptionEventEnum     the subscription event that took place
     * @return the received subscription notification response object
     */
    @Async("taskExecutor")
    public CompletableFuture<SubscriptionNotificationResponseObject> sendNotification(String mrn, UUID subscriptionIdentifier, SubscriptionEventEnum subscriptionEventEnum) {
        log.debug("Sending notification to client {} for subscription {} to mark {} event", mrn, subscriptionIdentifier, subscriptionEventEnum);

        // Get the SECOM client matching the provided MRN
        final SecomClient secomClient = this.secomV2Service.getClient(mrn);

        // Create the subscription notification response object
        SubscriptionNotificationObject subscriptionNotificationObject = new SubscriptionNotificationObject();
        subscriptionNotificationObject.setSubscriptionIdentifier(subscriptionIdentifier);
        subscriptionNotificationObject.setEventEnum(subscriptionEventEnum);

        // Send the object the return the response
        return CompletableFuture.completedFuture(secomClient.subscriptionNotification(subscriptionNotificationObject)
                .orElse(null));
    }

    /**
     * Sends a SECOM subscription notification response object to the SECOM
     * client specified by the specified callback endpoint URL. The response
     * is generated for the subscription UUID and event type provided in the
     * input parameter.
     *
     * @param callbackEndpoint          the client callback endpoint (URL) to be informed
     * @param subscriptionIdentifier    the subscription identifier UUID
     * @param subscriptionEventEnum     the subscription event that took place
     * @return the received subscription notification response object
     */
    @Async("taskExecutor")
    public CompletableFuture<SubscriptionNotificationResponseObject> sendNotification(URL callbackEndpoint, UUID subscriptionIdentifier, SubscriptionEventEnum subscriptionEventEnum) {
        log.debug("Sending notification to client URL {} for subscription {} to mark {} event", callbackEndpoint, subscriptionIdentifier, subscriptionEventEnum);

        // Get the SECOM client matching the provided MRN
        final SecomClient secomClient = this.secomV2Service.getClient(callbackEndpoint);

        // Create the subscription notification response object
        SubscriptionNotificationObject subscriptionNotificationObject = new SubscriptionNotificationObject();
        subscriptionNotificationObject.setSubscriptionIdentifier(subscriptionIdentifier);
        subscriptionNotificationObject.setEventEnum(subscriptionEventEnum);

        // Send the object the return the response
        return CompletableFuture.completedFuture(secomClient.subscriptionNotification(subscriptionNotificationObject)
                .orElse(null));
    }

}
