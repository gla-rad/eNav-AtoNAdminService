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
import org.grad.eNav.atonAdminService.models.domain.s201.AidsToNavigation;
import org.grad.eNav.atonAdminService.models.dtos.datatables.DtPage;
import org.grad.eNav.atonAdminService.models.dtos.datatables.DtPagingRequest;
import org.grad.eNav.atonAdminService.models.dtos.s201.AidsToNavigationDto;
import org.grad.eNav.atonAdminService.services.AidsToNavigationService;
import org.grad.eNav.atonAdminService.services.DatasetService;
import org.grad.eNav.atonAdminService.utils.GeometryJSONConverter;
import org.grad.eNav.atonAdminService.utils.HeaderUtil;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Aids to Navigation.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@RestController
@RequestMapping("/api/atons")
@Slf4j
public class AidsToNavigationController {

    /**
     * The Aids to Navigation Service.
     */
    @Autowired
    AidsToNavigationService aidsToNavigationService;

    /**
     * The Dataset Service.
     */
    @Autowired
    DatasetService datasetService;

    /**
     * Object Mapper from Domain to DTO.
     */
    @Autowired
    DomainDtoMapper<AidsToNavigation, AidsToNavigationDto> aidsToNavigationToDtoMapper;

    /**
     * GET /api/atons/list : Returns a full list of the current Aids to
     * navigation that match the provided criteria.
     *
     * @param idCode the Aids to Navigation number
     * @param geometry the geometry for AtoN message filtering
     * @param startDate the start date for AtoN message filtering
     * @param endDate the end date for AtoN message filtering
     * @return the ResponseEntity with status 200 (OK) and the list of stations in body
     */
    @GetMapping(value="/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AidsToNavigationDto>> getListOfAidsToNavigation(@RequestParam("idCode") Optional<String> idCode,
                                                                               @RequestParam("geometry") Optional<Geometry> geometry,
                                                                               @RequestParam("startDate") Optional<LocalDateTime> startDate,
                                                                               @RequestParam("endDate") Optional<LocalDateTime> endDate) {
        log.debug("REST request to get list of Aids to Navigation");
        idCode.ifPresent(v -> log.debug("Aids to Navigation ID code specified as: {}", idCode));
        geometry.ifPresent(v -> log.debug("Aids to Navigation geometry specified as: {}", GeometryJSONConverter.convertFromGeometry(v).toString()));
        startDate.ifPresent(v -> log.debug("Aids to Navigation start date specified as: {}", startDate));
        endDate.ifPresent(v -> log.debug("Aids to Navigation end date specified as: {}", endDate));
        Page<AidsToNavigation> atonPage = this.aidsToNavigationService.findAll(
                idCode.orElse(null),
                geometry.orElse(null),
                startDate.orElse(null),
                endDate.orElse(null),
                PageRequest.of(0, Integer.MAX_VALUE)
        );
        return ResponseEntity.ok()
                .body(this.aidsToNavigationToDtoMapper.convertToList(atonPage.getContent(), AidsToNavigationDto.class));
    }

    /**
     * GET /api/atons : Returns a paged list of all current Aids to navigation.
     *
     * @param idCode the Aids to Navigation number
     * @param geometry the geometry for AtoN message filtering
     * @param startDate the start date for AtoN message filtering
     * @param endDate the end date for AtoN message filtering
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of stations in body
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AidsToNavigationDto>> getAidsToNavigation(@RequestParam("idCode") Optional<String> idCode,
                                                                         @RequestParam("geometry") Optional<Geometry> geometry,
                                                                         @RequestParam("startDate") Optional<LocalDateTime> startDate,
                                                                         @RequestParam("endDate") Optional<LocalDateTime> endDate,
                                                                         Pageable pageable) {
        log.debug("REST request to get page of Aids to Navigation");
        idCode.ifPresent(v -> log.debug("Aids to Navigation ID code specified as: {}", idCode));
        geometry.ifPresent(v -> log.debug("Aids to Navigation geometry specified as: {}", GeometryJSONConverter.convertFromGeometry(v).toString()));
        startDate.ifPresent(v -> log.debug("Aids to Navigation start date specified as: {}", startDate));
        endDate.ifPresent(v -> log.debug("Aids to Navigation end date specified as: {}", endDate));
        Page<AidsToNavigation> atonPage = this.aidsToNavigationService.findAll(
                idCode.orElse(null),
                geometry.orElse(null),
                startDate.orElse(null),
                endDate.orElse(null),
                pageable
        );
        return ResponseEntity.ok()
                .body(this.aidsToNavigationToDtoMapper.convertToPage(atonPage, AidsToNavigationDto.class));
    }

    /**
     * POST /api/atons/dt : Returns a paged list of all current Aids to
     * Navigation for the datatables front-end.
     *
     * @param dtPagingRequest the datatables paging request
     * @return the ResponseEntity with status 200 (OK) and the list of stations in body
     */
    @PostMapping(value = "/dt", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DtPage<AidsToNavigationDto>> getAidsToNavigationForDatatables(@RequestBody DtPagingRequest dtPagingRequest) {
        log.debug("REST request to get page of Aids to Navigation for datatables");
        Page<AidsToNavigation> atonPage = this.aidsToNavigationService.handleDatatablesPagingRequest(
                dtPagingRequest
        );
        return ResponseEntity.ok()
                .body(this.aidsToNavigationToDtoMapper.convertToDtPage(atonPage, dtPagingRequest, AidsToNavigationDto.class));
    }

    /**
     * DELETE /api/atons/{id} : Delete the "id" Aids to Navigation.
     *
     * @param id the ID of the Aids to Navigation to be deleted
     * @return the ResponseEntity with status 200 (OK)
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteAidsToNavigation(@PathVariable BigInteger id) {
        log.debug("REST request to delete Aids to Navigation : {}", id);

        // Delete and retrieve the aids to navigation
        final AidsToNavigation aidsToNavigation = this.aidsToNavigationService.delete(id);

        // Now we should update all datasets that are affected in this area
        Optional.ofNullable(aidsToNavigation.getGeometry())
                .map(g -> this.datasetService.findAll(null, g, null, null, Boolean.FALSE, Pageable.unpaged()))
                .orElse(Page.empty())
                .stream()
                .forEach(this.datasetService::save);

        // And return the response
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityDeletionAlert("aton", aidsToNavigation.getId().toString()))
                .build();
    }

}
