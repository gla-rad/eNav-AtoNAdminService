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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.grad.eNav.atonAdminService.components.DomainDtoMapper;
import org.grad.eNav.atonAdminService.controllers.secom.SecomRequestHeaders;
import org.grad.eNav.atonAdminService.models.domain.secom.SubscriptionRequest;
import org.grad.eNav.atonAdminService.services.secom.v1.SecomV1SubscriptionService;
import org.grad.secom.core.exceptions.SecomNotFoundException;
import org.grad.secom.core.interfaces.SubscriptionSecomInterface;
import org.grad.secom.core.models.SubscriptionRequestObject;
import org.grad.secom.core.models.SubscriptionResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Objects;
import java.util.Optional;

/**
 * The SECOM Subscription Interface Controller.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Component
@Path("")
@Validated
@Slf4j
public class SubscriptionSecomController implements SubscriptionSecomInterface {

    /**
     * Object Mapper from SECOM v1 Subscription Request DTO to Domain.
     */
    @Autowired
    DomainDtoMapper<SubscriptionRequestObject, SubscriptionRequest> subscriptionRequestDomainMapper;

    /**
     * The SECOM v1 Service.
     */
    @Autowired
    SecomV1SubscriptionService secomV1SubscriptionService;

    /**
     * The Request Context.
     */
    @Autowired
    @Lazy
    Optional<HttpServletRequest> httpServletRequest;

    /**
     * POST /api/secom/v1/subscription : Request subscription on information,
     * either specific information according to parameters, or the information
     * accessible upon decision by the information provider.
     *
     * @param subscriptionRequestObject the subscription request object
     * @return the subscription response object
     */
    @CrossOrigin(originPatterns = "http://localhost:8768/*")
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
                .map(dto -> this.subscriptionRequestDomainMapper.convertTo(dto, SubscriptionRequest.class))
                .map(subReq -> this.secomV1SubscriptionService.save(mrn, subReq))
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
