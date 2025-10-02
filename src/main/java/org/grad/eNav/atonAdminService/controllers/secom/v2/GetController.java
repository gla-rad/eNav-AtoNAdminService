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

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
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
import org.grad.secomv2.core.interfaces.GetServiceInterface;
import org.grad.secomv2.core.models.DataResponseObject;
import org.grad.secomv2.core.models.ExchangeMetadata;
import org.grad.secomv2.core.models.GetResponseObject;
import org.grad.secomv2.core.models.PaginationObject;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * The SECOM v2 Get Service Interface Controller.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Component
@Path("/")
@Validated
@Slf4j
public class GetController implements GetServiceInterface {

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
     * GET /api/secom/v2/dataset : Returns the S-201 dataset entries as,
     * specified by the SECOM standard.
     *
     * @param dataReference the object data reference
     * @param containerType the object data container type
     * @param dataProductType the object data product type
     * @param productVersion the object data product version
     * @param geometry the object geometry
     * @param unlocode the object UNLOCODE
     * @param validFrom the object valid from time
     * @param validTo the object valid to time
     * @param page the page number to be retrieved
     * @param pageSize the maximum page size
     * @return the S-201 dataset information
     */
    @Tag(name = "SECOM")
    @Transactional
    public GetResponseObject get(@QueryParam("dataReference") @Parameter(schema = @Schema(implementation = String.class, pattern = "^[{(]?[0-9a-fA-F]{8}[-]?[0-9a-fA-F]{4}[-]?[0-9a-fA-F]{4}[-]?[0-9a-fA-F]{4}[-]?[0-9a-fA-F]{12}[)}]?$")) UUID dataReference,
                                 @QueryParam("containerType") @Parameter(schema = @Schema(description = "Data Type requested")) ContainerTypeEnum containerType,
                                 @QueryParam("dataProductType") @Parameter(schema = @Schema(description = "Data product type name See: https://registry.iho.int/productspec/list.do (column 'Product ID')")) SECOM_DataProductType dataProductType,
                                 @QueryParam("productVersion") @Parameter(schema = @Schema(description = "S-100 based Product specification version")) String productVersion,
                                 @QueryParam("geometry") @Parameter(schema = @Schema(description = "Geometry condition for geo-located information objects as WKT LineString or Polygon")) String geometry,
                                 @QueryParam("unlocode") @Parameter(schema = @Schema(description = "See UN web page")) @Pattern(regexp = "^[a-zA-Z]{2}[a-zA-Z2-9]{3}") String unlocode,
                                 @QueryParam("validFrom") @Parameter(schema = @Schema(implementation = String.class, description = "Time related to validity period start for information object")) Instant validFrom,
                                 @QueryParam("validTo") @Parameter(schema = @Schema(implementation = String.class, description = "Time related to validity period end for information object")) Instant validTo,
                                 @QueryParam("page") @Min(0) @Parameter(schema = @Schema(implementation = Integer.class, description = "Requested pagination page. Must be a positive integer >= 0..", defaultValue = "0")) Integer page,
                                 @QueryParam("pageSize") @Min(0) @Parameter(schema = @Schema(implementation = Integer.class, description = "Requested pagination page size. Must be a positive integer >= 0.", defaultValue = "100")) Integer pageSize) {
        log.debug("SECOM request to get page of Dataset");
        Optional.ofNullable(dataReference).ifPresent(v -> log.debug("Data Reference specified as: {}", dataReference));
        Optional.ofNullable(containerType).ifPresent(v -> log.debug("Container Type specified as: {}", containerType));
        Optional.ofNullable(dataProductType).ifPresent(v -> log.debug("Data Product Type specified as: {}", dataProductType));
        Optional.ofNullable(geometry).ifPresent(v -> log.debug("Geometry specified as: {}", geometry));
        Optional.ofNullable(unlocode).ifPresent(v -> log.debug("UNLOCODE specified as: {}", unlocode));
        Optional.ofNullable(validFrom).ifPresent(v -> log.debug("Valid From time specified as: {}", validFrom));
        Optional.ofNullable(validTo).ifPresent(v -> log.debug("Valid To time specified as: {}", validTo));

        // Init local variables
        Geometry jtsGeometry = null;
        Pageable pageable = Optional.ofNullable(page)
                .map(p -> PageRequest.of(p, Optional.ofNullable(pageSize).orElse(Integer.MAX_VALUE)))
                .map(Pageable.class::cast)
                .orElse(Pageable.unpaged());
        LocalDateTime validFromLdt = Optional.ofNullable(validFrom)
                .map(i -> LocalDateTime.ofInstant(i, ZoneId.systemDefault()))
                .orElse(null);
        LocalDateTime validToLdt = Optional.ofNullable(validTo)
                .map(i -> LocalDateTime.ofInstant(i, ZoneId.systemDefault()))
                .orElse(null);

        // Parse the arguments
        final ContainerTypeEnum reqContainerType = Optional.ofNullable(containerType)
                .orElse(ContainerTypeEnum.S100_DataSet);
        final SECOM_DataProductType reqDataProductType = Optional.ofNullable(dataProductType)
                .orElse(SECOM_DataProductType.S201);
        if(Objects.nonNull(geometry)) {
            try {
                jtsGeometry = GeometryUtils.joinGeometries(jtsGeometry, WKTUtils.convertWKTtoGeometry(geometry));
            } catch (ParseException ex) {
                throw new ValidationException(ex.getMessage());
            }
        }
        if(Objects.nonNull(unlocode)) {
            jtsGeometry = GeometryUtils.joinGeometries(jtsGeometry, Optional.ofNullable(unlocode)
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
                result = this.datasetService.findAll(dataReference, jtsGeometry, validFromLdt, validToLdt, Boolean.FALSE, pageable);
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
                Optional.ofNullable(pageSize).orElse(Integer.MAX_VALUE)));

        // And final return the Get Response Object
        return getResponseObject;

    }

}
