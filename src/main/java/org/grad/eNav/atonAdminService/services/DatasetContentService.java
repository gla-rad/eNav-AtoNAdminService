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

package org.grad.eNav.atonAdminService.services;

import _int.iho.s_201.gml.cs0._2.AidsToNavigationType;
import _int.iho.s_201.gml.cs0._2.Dataset;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.grad.eNav.atonAdminService.aspects.LogDataset;
import org.grad.eNav.atonAdminService.exceptions.DeletedAtoNsInDatasetContentGenerationException;
import org.grad.eNav.atonAdminService.exceptions.SavingFailedException;
import org.grad.eNav.atonAdminService.models.domain.DatasetContent;
import org.grad.eNav.atonAdminService.models.domain.s201.AidsToNavigation;
import org.grad.eNav.atonAdminService.models.domain.s201.S201Dataset;
import org.grad.eNav.atonAdminService.repos.DatasetContentRepo;
import org.grad.eNav.atonAdminService.utils.S201DatasetBuilder;
import org.grad.eNav.s201.utils.S201Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.geotools.filter.function.StaticGeometry.not;

/**
 * The S-201 Dataset Content Service.
 *
 * Service Implementation for managing the S-201 Dataset Content objects.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Service
@Slf4j
public class DatasetContentService {

    /**
     * The Model Mapper.
     */
    @Autowired
    ModelMapper modelMapper;

    /**
     * The Aids to Navigation Service.
     */
    @Autowired
    AidsToNavigationService aidsToNavigationService;

    @Lazy
    @Autowired
    DatasetService datasetService;

    /**
     * The Dataset Content Repo.
     */
    @Autowired
    DatasetContentRepo datasetContentRepo;

    /**
     * The MRN prefix to be used for identifying the S-201 datasets generated
     */
    @Value("${gla.rad.aton-service.datasetMrnPrefix:urn:mrn:test:s201}")
    String datasetMrnPrefix;

    /**
     * The saving operation that persists the dataset content in the database
     * using the respective repository.
     *updated
     * @param datasetContent the dataset content entity to be saved
     * @return the saved dataset content entity
     */
    @Transactional
    public DatasetContent save(@NotNull DatasetContent datasetContent) {
        log.debug("Request to save Dataset Content : {}", datasetContent);

        // Sanity Check
        final S201Dataset s201Dataset = Optional.of(datasetContent)
                .map(DatasetContent::getDataset)
                .orElseThrow(() -> new SavingFailedException("Cannot save a " +
                        "dataset content entity without it being linked to an " +
                        "actual dataset"));

        // Make sure the content is assign to the dataset as well
        if(Objects.isNull(s201Dataset.getDatasetContent())) {
            s201Dataset.setDatasetContent(datasetContent);
        }

        // Return the new/updated dataset content
        return this.datasetContentRepo.saveAndFlush(datasetContent);
    }

    /**
     * Provided a valid dataset this function will build the respective
     * dataset content and populate it with all entries that match its
     * geographical boundaries. The resulting object will then be marshalled
     * into an XML string and returned.
     *
     * @param uuid the dataset of the dataset to generate the content for
     * @return the dataset with the newly generated dataset content object
     */
    @LogDataset
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<S201Dataset> generateDatasetContent(@NotNull UUID uuid) {
        log.debug("Request to generate the content for Dataset with UUID: {}", uuid);

        // Make sure we have a valid dataset content entry to populate
        final S201Dataset s201Dataset = this.datasetService.findOne(uuid);
        final DatasetContent datasetContent = Optional.of(s201Dataset)
                .map(S201Dataset::getDatasetContent)
                .orElseGet(DatasetContent::new);

        // Get all the previously matching Aids to Navigation - if we have the old content
        final List<AidsToNavigationType> origAtonList = Optional.of(s201Dataset)
                .map(S201Dataset::getDatasetContent)
                .map(DatasetContent::getContent)
                .map(xml -> {
                    try { return S201Utils.getDatasetMembers(xml); }
                    catch (JAXBException ex) { return null; }
                })
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(AidsToNavigationType.class::isInstance)
                .map(AidsToNavigationType.class::cast)
                .toList();
        final Set<String> origIDCodes = origAtonList.stream()
                .map(AidsToNavigationType::getIDCode)
                .collect(Collectors.toSet());

        // Get all the new matching Aids to Navigation - if we have a geometry
        final List<AidsToNavigation> atonList = Optional.of(s201Dataset)
                .map(S201Dataset::getGeometry)
                .map(geometry ->
                        this.aidsToNavigationService.findAll(null, geometry, null, null, Pageable.unpaged())
                )
                .orElseGet(Page::empty)
                .getContent();
        final Set<String> idCodes = atonList.stream()
                .map(AidsToNavigation::getIdCode)
                .collect(Collectors.toSet());

        // ================================================================== //
        //                    IMPORTANT VALIDATION STEP                       //
        // ================================================================== //
        // In cases where any of the original AtoNs is not found in the current
        // list, this means that a new content will be invalid since there has
        // been a removal. Therefore, a ValidationException should be thrown.
        if(!idCodes.containsAll(origIDCodes)) {
            // Create a response that something went wrong
            CompletableFuture<S201Dataset> exFuture = CompletableFuture.failedFuture(new DeletedAtoNsInDatasetContentGenerationException(
                    String.format("Deleted AtoNs detected during the generation " +
                            "of the content of dataset with UUID %s. This " +
                            "dataset must be cancelled and replaced to " +
                            "continue...", s201Dataset.getUuid())
            ));
            // Stop the execution and inform the calling component on what happened
            return exFuture;
        }
        // ================================================================== //

        // Filter the new/updated Aids to Navigation entries - CAREFUL keel only
        // the unique items cause some might be included in both cases.
        final List<AidsToNavigation> newAtonList = atonList.stream()
                .filter(aton -> Objects.nonNull(aton.getIdCode()))
                .filter(aton -> not(origIDCodes.contains(aton.getIdCode())))
                .toList();
        final List<AidsToNavigation> updatedAtonList = atonList.stream()
                .filter(aton -> Objects.nonNull(aton.getLastModifiedAt()))
                .filter(aton -> aton.getLastModifiedAt().isAfter(Optional.of(s201Dataset)
                        .map(S201Dataset::getDatasetContent)
                        .map(DatasetContent::getGeneratedAt)
                        .orElse(LocalDateTime.MIN)))
                .toList();
        final List<AidsToNavigation> deltaAtonList = Stream
                .concat(newAtonList.stream(), updatedAtonList.stream())
                .collect(Collectors.toSet()) // To keep only the unique items
                .stream()
                .toList();

        // Now try to marshal the dataset into an XML string and update the content/delta
        final S201DatasetBuilder s201DatasetBuilder = new S201DatasetBuilder(this.modelMapper, this.datasetMrnPrefix);
        try {
            // Build the dataset contents, if any
            final Dataset dataset = s201DatasetBuilder.packageToDataset(s201Dataset, atonList);
            final Dataset delta = s201DatasetBuilder.packageToDataset(s201Dataset, deltaAtonList);

            // Marshall the contents into XML
            final String datasetXML = S201Utils.marshalS201(dataset, Boolean.TRUE);
            // Marshall the delta into XML - but only if it's not cancelled/deleted
            final String deltaXML = S201Utils.marshalS201(delta, Boolean.TRUE);

            // Populate the dataset content/delta
            datasetContent.setDataset(this.datasetService.findOne(s201Dataset.getUuid()));
            datasetContent.setContent(datasetXML);
            datasetContent.setContentLength(Optional.ofNullable(datasetXML)
                    .map(String::length)
                    .map(BigInteger::valueOf)
                    .orElse(BigInteger.ZERO));
            datasetContent.setDelta(deltaXML);
            datasetContent.setDeltaLength(Optional.ofNullable(deltaXML)
                    .map(String::length)
                    .map(BigInteger::valueOf)
                    .orElse(BigInteger.ZERO));

            // And finally perform the saving operation
            s201Dataset.setDatasetContent(this.save(datasetContent));
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return CompletableFuture.failedFuture(ex);
        }

        // Now return the update dataset content
        return CompletableFuture.completedFuture(s201Dataset);
    }

}
