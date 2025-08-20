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

package org.grad.eNav.atonAdminService.controllers.secom.v1;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.grad.eNav.atonAdminService.components.DomainDtoMapper;
import org.grad.eNav.atonAdminService.models.domain.secom.RemoveSubscription;
import org.grad.eNav.atonAdminService.services.secom.SecomSubscriptionService;
import org.grad.secom.core.exceptions.SecomNotFoundException;
import org.grad.secom.core.interfaces.RemoveSubscriptionSecomInterface;
import org.grad.secom.core.models.RemoveSubscriptionObject;
import org.grad.secom.core.models.RemoveSubscriptionResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.ws.rs.Path;
import java.util.Optional;
import java.util.UUID;

/**
 * The SECOM Remove Subscription Interface Controller.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Component
@Path("/")
@Validated
@Slf4j
public class RemoveSubscriptionSecomController implements RemoveSubscriptionSecomInterface {

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
     * DELETE /api/secom/v1/subscription : Subscription(s) can be removed either
     * internally by information owner, or externally by the consumer. This
     * interface shall be used by the consumer to request removal of
     * subscription.
     *
     * @param removeSubscriptionObject the remove subscription object
     * @return the remove subscription response object
     */
    @Tag(name = "SECOM")
    public RemoveSubscriptionResponseObject removeSubscription(@Valid RemoveSubscriptionObject removeSubscriptionObject) {
        final UUID subscriptionIdentifier = Optional.ofNullable(removeSubscriptionObject)
                .map(dto -> this.removeSubscriptionDomainMapper.convertTo(dto, RemoveSubscription.class))
                .map(RemoveSubscription::getSubscriptionIdentifier)
                .map(this.secomSubscriptionService::delete)
                .orElseThrow(() -> new SecomNotFoundException(removeSubscriptionObject.getSubscriptionIdentifier().toString()));

        // Create the response
        final RemoveSubscriptionResponseObject removeSubscriptionResponse = new RemoveSubscriptionResponseObject();
        removeSubscriptionResponse.setMessage(String.format("Subscription %s removed", subscriptionIdentifier));

        // Return the response
        return removeSubscriptionResponse;
    }

}
