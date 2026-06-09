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
import lombok.extern.slf4j.Slf4j;
import org.grad.eNav.atonAdminService.models.UnLoCodeMapEntry;
import org.grad.eNav.atonAdminService.models.domain.DatasetContent;
import org.grad.eNav.atonAdminService.models.domain.s201.S201Dataset;
import org.grad.eNav.atonAdminService.services.DatasetService;
import org.grad.eNav.atonAdminService.services.UnLoCodeService;
import org.grad.eNav.atonAdminService.utils.GeometryUtils;
import org.grad.eNav.atonAdminService.utils.WKTUtils;
import org.grad.secomv2.core.interfaces.PostGetSummaryServiceInterface;
import org.grad.secomv2.core.models.*;
import org.grad.secomv2.core.models.enums.ContainerTypeEnum;
import org.grad.secomv2.core.models.enums.InfoStatusEnum;
import org.grad.secomv2.core.models.enums.SECOM_DataProductType;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The SECOM v2 Get Summary Service Interface Controller.
 *
 * @author Lawrence Hughes (email: Lawrence.Hughes@gla-rad.org)
 */
@Component
@Slf4j
public class PostGetSummaryController implements PostGetSummaryServiceInterface {

    /**
     * The Dataset Service.
     */
    @Autowired
    DatasetService datasetService;

    /**
     * The UN/LOCODE Service.
     */
    @Autowired
    UnLoCodeService unLoCodeService;

    // Class Variables
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(),4326);

    /**
     * POST /api/secom/v2/object/search/summary : Returns the S-125 dataset summary
     * information, as specified by the SECOM standard.
     *
     * @param getSummaryFilterObject the summary filter object
     *
     * @return the S-125 dataset summary information
     */
    @Tag(name = "SECOM")
    @Override
    public ResponseEntity<GetSummaryResponseObject> getSummary(@Valid GetSummaryFilterObject getSummaryFilterObject) {
        EnvelopeGetSummaryFilterObject envelopeGetSummaryFilterObject = getSummaryFilterObject.getEnvelope();

        log.debug("SECOM request to get page of Dataset Summary");
        Optional.ofNullable(envelopeGetSummaryFilterObject.getContainerType()).ifPresent(v -> log.debug("Container Type specified as: {}", envelopeGetSummaryFilterObject.getContainerType()));
        Optional.ofNullable(envelopeGetSummaryFilterObject.getDataProductType()).ifPresent(v -> log.debug("Data Product Type specified as: {}", envelopeGetSummaryFilterObject.getDataProductType()));
        Optional.ofNullable(envelopeGetSummaryFilterObject.getGeometry()).ifPresent(v -> log.debug("Geometry specified as: {}", envelopeGetSummaryFilterObject.getGeometry()));
        Optional.ofNullable(envelopeGetSummaryFilterObject.getUnlocode()).ifPresent(v -> log.debug("UNLOCODE specified as: {}", envelopeGetSummaryFilterObject.getUnlocode()));
        Optional.ofNullable(envelopeGetSummaryFilterObject.getValidFrom()).ifPresent(v -> log.debug("Valid From time specified as: {}", envelopeGetSummaryFilterObject.getValidFrom()));
        Optional.ofNullable(envelopeGetSummaryFilterObject.getValidTo()).ifPresent(v -> log.debug("Valid To time specified as: {}", envelopeGetSummaryFilterObject.getValidTo()));

        // Init local variables
        Geometry jtsGeometry = null;
        Pageable pageable = Optional.ofNullable(envelopeGetSummaryFilterObject.getPage())
                .map(p -> PageRequest.of(p - 1, Optional.ofNullable(envelopeGetSummaryFilterObject.getPageSize()).orElse(Integer.MAX_VALUE)))
                .map(Pageable.class::cast)
                .orElse(Pageable.unpaged());
        LocalDateTime validFromLdt = Optional.ofNullable(envelopeGetSummaryFilterObject.getValidFrom())
                .map(i -> LocalDateTime.ofInstant(i, ZoneId.systemDefault()))
                .orElse(null);
        LocalDateTime validToLdt = Optional.ofNullable(envelopeGetSummaryFilterObject.getValidTo())
                .map(i -> LocalDateTime.ofInstant(i, ZoneId.systemDefault()))
                .orElse(null);

        // Parse the arguments
        final ContainerTypeEnum reqContainerType = Optional.ofNullable(envelopeGetSummaryFilterObject.getContainerType())
                .orElse(ContainerTypeEnum.S100_DataSet);
        final SECOM_DataProductType reqDataProductType = Optional.ofNullable(envelopeGetSummaryFilterObject.getDataProductType())
                .orElse(SECOM_DataProductType.S201);
        if(Objects.nonNull(envelopeGetSummaryFilterObject.getGeometry())) {
            try {
                jtsGeometry = WKTUtils.convertWKTtoGeometry(envelopeGetSummaryFilterObject.getGeometry());
            } catch (ParseException ex) {
                throw new ValidationException(ex.getMessage());
            }
        }
        if(Objects.nonNull(envelopeGetSummaryFilterObject.getUnlocode())) {
            jtsGeometry = GeometryUtils.joinGeometries(jtsGeometry, Optional.ofNullable(envelopeGetSummaryFilterObject.getUnlocode())
                    .map(this.unLoCodeService::getUnLoCodeMapEntry)
                    .map(UnLoCodeMapEntry::getGeometry)
                    .orElseGet(() -> this.geometryFactory.createEmpty(0)));
        }

        // We only support S-100 Datasets here
        final List<SummaryObject> summaryObjectList = new ArrayList<>();
        if(reqContainerType == ContainerTypeEnum.S100_DataSet) {
            // We only support specifically S-201 Datasets
            if (reqDataProductType == SECOM_DataProductType.S201) {
                this.datasetService.findAll(null, jtsGeometry, validFromLdt, validToLdt, Boolean.FALSE, pageable)
                        .stream()
                        .map(dataset -> {
                            // Create and populate the summary object
                            SummaryObject summaryObject = new SummaryObject();
                            summaryObject.setDataReference(dataset.getUuid());
                            summaryObject.setDataProtection(Boolean.FALSE);
                            summaryObject.setDataCompression(Boolean.FALSE);
                            summaryObject.setContainerType(reqContainerType);
                            summaryObject.setDataProductType(reqDataProductType);
                            summaryObject.setInfo_productVersion(dataset.getDatasetIdentificationInformation().getProductEdition());
                            summaryObject.setInfo_identifier(dataset.getDatasetIdentificationInformation().getDatasetFileIdentifier());
                            summaryObject.setInfo_name(dataset.getDatasetIdentificationInformation().getDatasetTitle());
                            summaryObject.setInfo_status(InfoStatusEnum.PRESENT.getValue());
                            summaryObject.setInfo_description(dataset.getDatasetIdentificationInformation().getDatasetAbstract());
                            summaryObject.setInfo_lastModifiedDate(Optional.ofNullable(dataset.getLastUpdatedAt()).map(ldt-> ldt.atZone(ZoneId.systemDefault())).map(ZonedDateTime::toInstant).orElse(null));
                            summaryObject.setInfo_size(Optional.of(dataset)
                                    .map(S201Dataset::getDatasetContent)
                                    .map(DatasetContent::getContentLength)
                                    .map(BigInteger::longValue)
                                    .orElse(BigInteger.ZERO.longValue()));

                            // And return the summary object
                            return summaryObject;
                        })
                        .forEach(summaryObjectList::add);
            }
        }

        // Start building the response
        final GetSummaryResponseObject getSummaryResponseObject = new GetSummaryResponseObject();
        getSummaryResponseObject.setSummaryObject(summaryObjectList);
        getSummaryResponseObject.setPagination(new PaginationObject(
                summaryObjectList.size(),
                Optional.ofNullable(envelopeGetSummaryFilterObject.getPageSize()).orElse(Integer.MAX_VALUE)));

        // And return the Get Summary Response Object
        return ResponseEntity.ok(getSummaryResponseObject);
    }


}
