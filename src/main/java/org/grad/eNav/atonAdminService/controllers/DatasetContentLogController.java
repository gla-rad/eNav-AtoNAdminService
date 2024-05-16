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
import org.grad.eNav.atonAdminService.models.domain.DatasetContentLog;
import org.grad.eNav.atonAdminService.models.dtos.DatasetContentLogDto;
import org.grad.eNav.atonAdminService.models.dtos.datatables.DtPage;
import org.grad.eNav.atonAdminService.models.dtos.datatables.DtPagingRequest;
import org.grad.eNav.atonAdminService.services.DatasetContentLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

/**
 * REST controller for managing Dataset Content Logs.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@RestController
@RequestMapping("/api/datasetcontentlog")
@Slf4j
public class DatasetContentLogController {

    /**
     * The Dataset Content Log Service.
     */
    @Autowired
    DatasetContentLogService datasetContentLogService;

    /**
     * Object Mapper from Domain to DTO.
     */
    @Autowired
    DomainDtoMapper<DatasetContentLog, DatasetContentLogDto> datasetContentLogDtoMapper;

    /**
     * GET /api/datasetcontentlog/{datasetContentLogId}/data : Returns the
     * content of a dataset content log entry if an existing ID is provided.
     *
     * @param datasetContentLogId the ID of the dataset content log entry
     * @return the ResponseEntity with status 200 (OK) and the list of dataset content log content in body
     */
    @GetMapping(value = "/{datasetContentLogId}/data", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getDatasetContentLogData(@PathVariable BigInteger datasetContentLogId) {
        log.debug("REST request to get the data of the Dataset Content Log with ID {}", datasetContentLogId);
        return ResponseEntity.ok()
                .body(this.datasetContentLogService
                        .findOne(datasetContentLogId)
                        .getContent()
                );
    }

    /**
     * GET /api/datasetcontentlog/{datasetContentLogId}/delta : Returns the diff
     * of a dataset content log entry if an existing ID is provided.
     *
     * @param datasetContentLogId the ID of the dataset content log entry
     * @return the ResponseEntity with status 200 (OK) and the list of dataset content log diff in body
     */
    @GetMapping(value = "/{datasetContentLogId}/delta", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getDatasetContentLogDelta(@PathVariable BigInteger datasetContentLogId) {
        log.debug("REST request to get the data of the Dataset Content Log with ID {}", datasetContentLogId);
        return ResponseEntity.ok()
                .body(this.datasetContentLogService
                        .findOne(datasetContentLogId)
                        .getDelta()
                );
    }

    /**
     * GET /api/datasetcontentlog : Returns a paged list of all current dataset
     * content logs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of dataset content logs in body
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<DatasetContentLogDto>> getDatasetContentLogs(Pageable pageable) {
        log.debug("REST request to get page of Dataset Content Logs");
        return ResponseEntity.ok()
                .body(this.datasetContentLogDtoMapper.convertToPage(
                        this.datasetContentLogService.findAll(pageable),
                        DatasetContentLogDto.class)
                );
    }

    /**
     * POST /api/datasetcontentlog/dt : Returns a paged list of all current
     * dataset content logs for the datatables front-end.
     *
     * @param dtPagingRequest the datatables paging request
     * @return the ResponseEntity with status 200 (OK) and the list of dataset content logs in body
     */
    @PostMapping(value = "/dt", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DtPage<DatasetContentLogDto>> getDatasetContentLogsForDatatables(@RequestBody DtPagingRequest dtPagingRequest) {
        log.debug("REST request to get page of Dataset for datatables");
        return ResponseEntity.ok()
                .body(this.datasetContentLogDtoMapper.convertToDtPage(
                        this.datasetContentLogService.handleDatatablesPagingRequest(dtPagingRequest),
                        dtPagingRequest,
                        DatasetContentLogDto.class)
                );
    }

}
