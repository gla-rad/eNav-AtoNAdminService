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

package org.grad.eNav.atonAdminService.controllers;

import lombok.extern.slf4j.Slf4j;
import org.grad.eNav.atonAdminService.components.DomainDtoMapper;
import org.grad.eNav.atonAdminService.models.domain.secom.SubscriptionRequest;
import org.grad.eNav.atonAdminService.models.dtos.datatables.DtPage;
import org.grad.eNav.atonAdminService.models.dtos.datatables.DtPagingRequest;
import org.grad.eNav.atonAdminService.models.dtos.secom.SubscriptionRequestDto;
import org.grad.eNav.atonAdminService.services.secom.SecomSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing subscriptions.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@RestController
@RequestMapping("/api/subscriptions")
@Slf4j
public class SubscriptionController {

    /**
     * The Dataset Service.
     */
    @Autowired
    SecomSubscriptionService secomSubscriptionService;

    /**
     * Object Mapper from Domain to DTO.
     */
    @Autowired
    DomainDtoMapper<SubscriptionRequest, SubscriptionRequestDto> subscriptionDtoMapper;

    /**
     * Object Mapper from DTO to Domain.
     */
    @Autowired
    DomainDtoMapper<SubscriptionRequestDto, SubscriptionRequest> subscriptionDomainMapper;

    /**
     * POST /api/dataset/dt : Returns a paged list of all current datasets
     * for the datatables front-end.
     *
     * @param dtPagingRequest the datatables paging request
     * @return the ResponseEntity with status 200 (OK) and the list of datasets in body
     */
    @PostMapping(value = "/dt", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DtPage<SubscriptionRequestDto>> getSubscriptionsForDatatables(@RequestBody DtPagingRequest dtPagingRequest) {
        log.debug("REST request to get page of Subscriptions for datatables");
        final Page<SubscriptionRequest> subscriptionPage = this.secomSubscriptionService.handleDatatablesPagingRequest(dtPagingRequest);
        return ResponseEntity.ok()
                .body(this.subscriptionDtoMapper.convertToDtPage(subscriptionPage, dtPagingRequest, SubscriptionRequestDto.class));
    }

}
