/*
 * Copyright (c) 2025 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grad.eNav.atonAdminService.controllers.secom.v2;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.grad.eNav.atonAdminService.models.UnLoCodeMapEntry;
import org.grad.eNav.atonAdminService.models.domain.DatasetContent;
import org.grad.eNav.atonAdminService.models.domain.s201.S201Dataset;
import org.grad.eNav.atonAdminService.services.DatasetService;
import org.grad.eNav.atonAdminService.services.S100ExchangeSetService;
import org.grad.eNav.atonAdminService.services.UnLoCodeService;
import org.grad.eNav.atonAdminService.utils.GeometryUtils;
import org.grad.eNav.atonAdminService.utils.WKTUtils;
import org.grad.secomv2.core.interfaces.PostGetServiceInterface;
import org.grad.secomv2.core.models.*;
import org.grad.secomv2.core.models.enums.ContainerTypeEnum;
import org.grad.secomv2.core.models.enums.SECOM_DataProductType;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The SECOM v2 Get Service Interface Controller.
 *
 * @author Lawrence Hughes (email: Lawrence.Hughes@gla-rad.org)
 */
@Component
@Validated
@Slf4j
public class PostGetController implements PostGetServiceInterface {

    /**
     * The Dataset Service.
     */
    @Autowired
    DatasetService datasetService;

    /**
     * The SECOM Exchange Set Service.
     */
    @Autowired
    S100ExchangeSetService s100ExchangeSetService;

    /**
     * The UN/LOCODE Service.
     */
    @Autowired
    UnLoCodeService unLoCodeService;

    // Class Variables
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(),4326);

    /**
     * POST /api/secom/v2/object/search : Returns the S-201 dataset entries as,
     * specified by the SECOM standard.
     *
     * @param getFilterObject the filter options
     * @return the S-201 dataset information
     */
    @Tag(name = "SECOM")
    @Override
    public ResponseEntity<GetResponseObject> get(@Valid GetFilterObject getFilterObject) {
        log.debug("SECOM request to get page of Dataset");
        EnvelopeGetFilterObject envelopeGetFilterObject = getFilterObject.getEnvelope();
        Optional.ofNullable(envelopeGetFilterObject.getDataReference()).ifPresent(v -> log.debug("Data Reference specified as: {}", envelopeGetFilterObject.getDataReference()));
        Optional.ofNullable(envelopeGetFilterObject.getContainerType()).ifPresent(v -> log.debug("Container Type specified as: {}", envelopeGetFilterObject.getContainerType()));
        Optional.ofNullable(envelopeGetFilterObject.getDataProductType()).ifPresent(v -> log.debug("Data Product Type specified as: {}", envelopeGetFilterObject.getDataProductType()));
        Optional.ofNullable(envelopeGetFilterObject.getGeometry()).ifPresent(v -> log.debug("Geometry specified as: {}", envelopeGetFilterObject.getGeometry()));
        Optional.ofNullable(envelopeGetFilterObject.getUnlocode()).ifPresent(v -> log.debug("UNLOCODE specified as: {}", envelopeGetFilterObject.getUnlocode()));
        Optional.ofNullable(envelopeGetFilterObject.getValidFrom()).ifPresent(v -> log.debug("Valid From time specified as: {}", envelopeGetFilterObject.getValidFrom()));
        Optional.ofNullable(envelopeGetFilterObject.getValidTo()).ifPresent(v -> log.debug("Valid To time specified as: {}", envelopeGetFilterObject.getValidTo()));

        // Init local variables
        Geometry jtsGeometry = null;
        Pageable pageable = Optional.ofNullable(envelopeGetFilterObject.getPage())
                .map(p -> PageRequest.of(p - 1, Optional.ofNullable(envelopeGetFilterObject.getPageSize()).orElse(Integer.MAX_VALUE)))
                .map(Pageable.class::cast)
                .orElse(Pageable.unpaged());
        LocalDateTime validFromLdt = Optional.ofNullable(envelopeGetFilterObject.getValidFrom())
                .map(i -> LocalDateTime.ofInstant(i, ZoneId.systemDefault()))
                .orElse(null);
        LocalDateTime validToLdt = Optional.ofNullable(envelopeGetFilterObject.getValidTo())
                .map(i -> LocalDateTime.ofInstant(i, ZoneId.systemDefault()))
                .orElse(null);

        // Parse the arguments
        final ContainerTypeEnum reqContainerType = Optional.ofNullable(envelopeGetFilterObject.getContainerType())
                .orElse(ContainerTypeEnum.S100_DataSet);
        final SECOM_DataProductType reqDataProductType = Optional.ofNullable(envelopeGetFilterObject.getDataProductType())
                .orElse(SECOM_DataProductType.S201);
        if(Objects.nonNull(envelopeGetFilterObject.getGeometry())) {
            try {
                jtsGeometry = GeometryUtils.joinGeometries(jtsGeometry, WKTUtils.convertWKTtoGeometry(envelopeGetFilterObject.getGeometry()));
            } catch (ParseException ex) {
                throw new ValidationException(ex.getMessage());
            }
        }
        if(Objects.nonNull(envelopeGetFilterObject.getUnlocode())) {
            jtsGeometry = GeometryUtils.joinGeometries(jtsGeometry, Optional.ofNullable(envelopeGetFilterObject.getUnlocode())
                    .map(this.unLoCodeService::getUnLoCodeMapEntry)
                    .map(UnLoCodeMapEntry::getGeometry)
                    .orElseGet(() -> this.geometryFactory.createEmpty(0)));
        }

        // Initialise the data response object list
        final List<DataResponseObject> dataResponseObjectList = new ArrayList<>();

        // We only support specifically S-201 Datasets
        if(reqDataProductType == SECOM_DataProductType.S201) {
            // Retrieve all matching datasets
            Page<S201Dataset> result;
            try {
                result = this.datasetService.findAll(envelopeGetFilterObject.getDataReference(), jtsGeometry, validFromLdt, validToLdt, Boolean.FALSE, pageable);
            } catch (Exception ex) {
                log.error("Error while retrieving the dataset query results: {} ", ex.getMessage());
                throw new ValidationException(ex.getMessage());
            }

            // Package as S100 Datasets
            if(reqContainerType == ContainerTypeEnum.S100_DataSet) {
                result.stream()
                        .map(S201Dataset::getDatasetContent)
                        .filter(Objects::nonNull)
                        .map(DatasetContent::getContent)
                        .map(String::getBytes)
                        .map(bytes -> {
                            // Create and populate the data response object
                            final DataResponseObject dataResponseObject = new DataResponseObject();
                            dataResponseObject.setData(bytes);

                            // And return the data response object
                            return dataResponseObject;
                        })
                        .forEach(dataResponseObjectList::add);

            }
            // Package as S100 Exchange Sets
            else if(reqContainerType == ContainerTypeEnum.S100_ExchangeSet) {
                // Create and populate the data response object
                final DataResponseObject dataResponseObject = new DataResponseObject();
                try {
                    dataResponseObject.setData(this.s100ExchangeSetService.packageToExchangeSet(result.getContent(), validFromLdt, validToLdt));
                } catch (IOException | JAXBException ex) {
                    log.error("Error while packaging the exchange set response: {} ", ex.getMessage());
                    throw new ValidationException(ex.getMessage());
                }

                // Flag that this is compressed in the exchange metadata
                dataResponseObject.setExchangeMetadata(new ExchangeMetadata());
                dataResponseObject.getExchangeMetadata().setCompressionFlag(Boolean.TRUE);

                // And add it to the data response list
                dataResponseObjectList.add(dataResponseObject);
            }
        }

        // Generate the Get Response Object
        final GetResponseObject getResponseObject = new GetResponseObject();
        getResponseObject.setDataResponseObject(dataResponseObjectList);
        getResponseObject.setPagination(new PaginationObject(
                dataResponseObjectList.size(),
                Optional.ofNullable(envelopeGetFilterObject.getPageSize()).orElse(Integer.MAX_VALUE)));

        // And final return the Get Response Object
        return ResponseEntity.ok(getResponseObject);
    }
}
