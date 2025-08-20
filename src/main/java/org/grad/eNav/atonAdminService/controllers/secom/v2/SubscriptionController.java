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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.grad.eNav.atonAdminService.components.DomainDtoMapper;
import org.grad.eNav.atonAdminService.controllers.secom.SecomRequestHeaders;
import org.grad.eNav.atonAdminService.models.domain.secom.SubscriptionRequest;
import org.grad.eNav.atonAdminService.services.secom.SecomSubscriptionService;
import org.grad.secomv2.core.exceptions.SecomNotFoundException;
import org.grad.secomv2.core.interfaces.SubscriptionServiceInterface;
import org.grad.secomv2.core.models.EnvelopeSubscriptionObject;
import org.grad.secomv2.core.models.SubscriptionRequestObject;
import org.grad.secomv2.core.models.SubscriptionResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;
import java.util.Optional;

/**
 * The SECOM Subscription Service Interface Controller.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Component
@Path("")
@Validated
@Slf4j
public class SubscriptionController implements SubscriptionServiceInterface {

    /**
     * Object Mapper from SECOM Subscription Request DTO to Domain.
     */
    @Autowired
    DomainDtoMapper<EnvelopeSubscriptionObject, SubscriptionRequest> subscriptionRequestDomainMapper;

    /**
     * The SECOM Service.
     */
    @Autowired
    SecomSubscriptionService secomSubscriptionService;

    /**
     * The Request Context.
     */
    @Autowired
    @Lazy
    Optional<HttpServletRequest> httpServletRequest;

    /**
     * POST /api/secom/v2/subscription : Request subscription on information,
     * either specific information according to parameters, or the information
     * accessible upon decision by the information provider.
     *
     * @param subscriptionRequestObject the subscription request object
     * @return the subscription response object
     */
    // Kept or testing
    //@CrossOrigin(originPatterns = "http://localhost:8768/*")
    @Tag(name = "SECOM")
    public SubscriptionResponseObject subscription(@Valid SubscriptionRequestObject subscriptionRequestObject) {
        // Try to access the request header if possible...
        // These should have been forwarded by the API Gateway normally and
        // contain the SSL client certificate and extra information.
        final String mrn = this.httpServletRequest
                .map(req -> req.getHeader(SecomRequestHeaders.MRN_HEADER))
                .map(Strings::trimToNull)
                .orElse(null);
        final SubscriptionRequest subscriptionRequest = Optional.ofNullable(subscriptionRequestObject)
                .map(SubscriptionRequestObject::getEnvelope)
                .map(dto -> this.subscriptionRequestDomainMapper.convertTo(dto, SubscriptionRequest.class))
                .map(subReq -> this.secomSubscriptionService.save(mrn, subReq))
                .filter(req -> Objects.nonNull(req.getUuid()))
                .orElseThrow(() -> new SecomNotFoundException("UUID"));

        // Create the response
        final SubscriptionResponseObject subscriptionResponse = new SubscriptionResponseObject();
        subscriptionResponse.setSubscriptionIdentifier(subscriptionRequest.getUuid());
        subscriptionResponse.setMessage("Subscription successfully created");

        // Return the response
        return subscriptionResponse;
    }

}
