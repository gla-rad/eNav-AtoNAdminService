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

package org.grad.eNav.atonAdminService.controllers.secom.v2;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import lombok.extern.slf4j.Slf4j;
import org.grad.eNav.atonAdminService.components.DomainDtoMapper;
import org.grad.eNav.atonAdminService.models.domain.secom.RemoveSubscription;
import org.grad.eNav.atonAdminService.services.secom.SecomSubscriptionService;
import org.grad.secomv2.core.exceptions.SecomNotFoundException;
import org.grad.secomv2.core.interfaces.RemoveSubscriptionServiceInterface;
import org.grad.secomv2.core.models.RemoveSubscriptionObject;
import org.grad.secomv2.core.models.RemoveSubscriptionResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;
import java.util.UUID;

/**
 * The SECOM Remove Subscription Service Interface Controller.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Component
@Path("/")
@Validated
@Slf4j
public class RemoveSubscriptionController implements RemoveSubscriptionServiceInterface {

    /**
     * The SECOM Service.
     */
    @Autowired
    SecomSubscriptionService secomSubscriptionService;

    /**
     * Object Mapper from SECOM Remove Subscription DTO to Domain.
     */
    @Autowired
    DomainDtoMapper<RemoveSubscriptionObject, RemoveSubscription> removeSubscriptionDomainMapper;

    /**
     * DELETE /api/secom/v2/subscription : Subscription(s) can be removed either
     * internally by information owner, or externally by the consumer. This
     * interface shall be used by the consumer to request removal of
     * subscription.
     *
     * @param subscriptionIdentifier the identified of the subscription to be removed
     * @return the remove subscription response object
     */
    @Tag(name = "SECOM")
    @Transactional
    public RemoveSubscriptionResponseObject removeSubscription(@QueryParam(value="subscriptionIdentifier") UUID subscriptionIdentifier) {
        final UUID deletedSubscriptionIdentifier = Optional.ofNullable(subscriptionIdentifier)
                .map(this.secomSubscriptionService::delete)
                .orElseThrow(() -> new SecomNotFoundException(String.format("%s", subscriptionIdentifier)));

        // Create the response
        final RemoveSubscriptionResponseObject removeSubscriptionResponse = new RemoveSubscriptionResponseObject();
        removeSubscriptionResponse.setMessage(String.format("Subscription %s removed", deletedSubscriptionIdentifier));

        // Return the response
        return removeSubscriptionResponse;
    }

}
