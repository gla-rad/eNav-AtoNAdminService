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

import _int.iho.s100.catalog._5_0.*;
import feign.Response;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.logging.log4j.util.Strings;
import org.grad.eNav.atonAdminService.exceptions.ValidationException;
import org.grad.eNav.atonAdminService.feign.CKeeperClient;
import org.grad.eNav.atonAdminService.models.domain.DatasetContentLog;
import org.grad.eNav.atonAdminService.models.domain.s100.ServiceInformationConfig;
import org.grad.eNav.atonAdminService.models.domain.s201.S201Dataset;
import org.grad.eNav.atonAdminService.models.dtos.McpEntityType;
import org.grad.eNav.atonAdminService.models.dtos.SignatureCertificateDto;
import org.grad.eNav.atonAdminService.models.enums.DatasetOperation;
import org.grad.eNav.atonAdminService.utils.S201DatasetBuilder;
import org.grad.eNav.atonAdminService.utils.ZipUtils;
import org.grad.eNav.atonAdminService.utils.FileActionUtils;
import org.grad.eNav.s100.enums.MaintenanceFrequency;
import org.grad.eNav.s100.enums.RoleCode;
import org.grad.eNav.s100.enums.SecurityClassification;
import org.grad.eNav.s100.enums.TelephoneType;
import org.grad.eNav.s100.utils.S100ExchangeCatalogueBuilder;
import org.grad.eNav.s100.utils.S100ExchangeSetUtils;
import org.grad.eNav.s201.utils.GIRegistryInfo;
import org.grad.secom.core.models.enums.DigitalSignatureAlgorithmEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

/**
 * The S-100 Exchange Set Service
 * <p/>
 * A service implementation to assist with the exchange set packaging
 * operations. These operations are based on the S-100 Universal Hydrographic
 * Data Model Edition 5.0.0 document. In particular the exchange set operations
 * are covered in Part 17 of the standards.
 * <p/>
 * To assist with the S-100 exchange set generation procedure, the S-100
 * exchange set XML schema specification was parsed using JAXB and loaded as
 * a separate library, included in the S-201 data model POM dependency. This
 * library includes a set of builder functions for generating the exchange sets
 * which are utilised in this class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Slf4j
@Service
public class S100ExchangeSetService {

    /**
     * The General Exchange Set Directory Prefix.
     */
    @Value("${gla.rad.service.s100.exchangeSet.dir:/tmp}")
    String s100ExchangeSetDir;

    /**
     * The General Exchange Set File Prefix.
     */
    @Value("${gla.rad.service.s100.exchangeSet.prefix:S201ExchangeSet}")
    String s100ExchangeSetPrefix;

    /**
     * The Service Information Config.
     */
    @Autowired
    ServiceInformationConfig serviceInformationConfig;

    /**
     * The Dataset Content Log Service.
     */
    @Autowired
    DatasetContentLogService datasetContentLogService;

    /**
     * The CKeeper Client.
     */
    @Autowired
    @Lazy
    CKeeperClient cKeeperClient;

    /**
     * The MRN prefix to be used for identifying the S-201 datasets generated
     */
    @Value("${gla.rad.aton-service.datasetMrnPrefix:urn:mrn:test:s201}")
    String datasetMrnPrefix;

    /**
     * The main packaging function to an exchange set. This operation will
     * accept a specific dataset as an input and will use to retrieve the
     * respective dataset files and deltas and will package everything nicely
     * in a ZIP file.
     * </p>
     * Note that at this point we do not care much about validating the dataset.
     * The service will just package everything it is given, even if it is
     * invalid at the time, so be careful!
     *
     * @param s201Datasets the S-201 datasets to be packaged
     * @param validFrom the local date time the requests data should be valid from
     * @param validTo the local date time the requests data should be valid to
     * @return the packaged S-201 exchange set
     */
    @Transactional(readOnly = true)
    public byte[] packageToExchangeSet(List<S201Dataset> s201Datasets, LocalDateTime validFrom, LocalDateTime validTo) throws IOException, JAXBException {
        // First get the latest certificate for this service
        final SignatureCertificateDto signatureCertificate = this.cKeeperClient.getSignatureCertificate(
                this.serviceInformationConfig.name(),
                this.serviceInformationConfig.version(),
                null,
                McpEntityType.SERVICE.getValue()
        );
        // Get the certificate into an X.509 format
        final BigInteger certificateId = signatureCertificate.getCertificateId();
        final String certificatePem = signatureCertificate.getCertificate();

        // Create a temporary directory for constructing the exchange set
        final File tmpExchangeSetDir = Files.createTempDirectory(
                Paths.get(this.s100ExchangeSetDir),
                String.format("%s%s", this.s100ExchangeSetPrefix, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        ).toFile();

        // -------------------------------------------------------------------//
        //                  Start adding the folder structure                 //
        // -------------------------------------------------------------------//
        // S_100
        // | --> S-201
        //       | --> DATASET_FILES
        //       | --> CATALOGUES
        //       | --> SUPPORT_FILES
        // -------------------------------------------------------------------//
        final File esRootFolder = FileActionUtils.createDirectory(tmpExchangeSetDir,"S_100");
        final File esS201Folder = FileActionUtils.createDirectory(esRootFolder,"S-201");
        final File esS201FolderDatasetFiles = FileActionUtils.createDirectory(esS201Folder, "DATASET_FILES");
        final File esS201FolderCatalogues = FileActionUtils.createDirectory(esS201Folder, "CATALOGUES");
        final File esS201FolderSupportFiles = FileActionUtils.createDirectory(esS201Folder, "SUPPORT_FILES");
        // -------------------------------------------------------------------//

        // Create a map for the dataset internal data to be used for later
        final Map<String, DatasetData> datasetDataMap = new HashMap<>();

        // Add the dataset files
        for(S201Dataset dataset : s201Datasets) {
            // Get the deltas for each dataset and sort by time to be safe
            final List<DatasetContentLog> deltas = this.datasetContentLogService.findForUuidDuring(dataset.getUuid(), validFrom, validTo);

            // TODO: Don't trust the sequence numbers for now - use an index
            AtomicInteger index = new AtomicInteger(0);

            // Generate the exchange set file for thia dataset/delta pair
            for(DatasetContentLog delta : deltas) {
                final String fileName = this.generateExchangeSetFileName(String.format("%s-%d", dataset.getUuid(), index.getAndIncrement()),"XML");
                final File datasetFile = FileActionUtils.createFile(esS201FolderDatasetFiles, fileName);
                Files.write(datasetFile.toPath(), Optional.of(delta).map(DatasetContentLog::getDelta).orElse("").getBytes());

                // Add the dataset file to the map for later
                datasetDataMap.put(
                        datasetFile.getName(),
                        new DatasetData(datasetFile,
                                certificateId,
                                certificatePem,
                                dataset,
                                delta)
                );
            }
        }

        // Add the CATALOG.XML
        final File esCatalog = FileActionUtils.createFile(esRootFolder, "CATALOG.XML");
        final String catalogueXml;
        try {
            catalogueXml = this.generateExchangeSetContent(datasetDataMap, signatureCertificate);
            FileUtils.writeStringToFile(esCatalog, catalogueXml, Charset.defaultCharset());
        } catch (CertificateException ex) {
            log.error(ex.getMessage());
            throw new ValidationException(ex.getMessage());
        }

        // Add the CATALOG.SIGN and add the CATALOG.XML signature
        final File esSignature = FileActionUtils.createFile(esRootFolder, "CATALOG.SIGN");
        final byte[] signature;
        try {
            signature = this.signContent(signatureCertificate.getCertificateId(), DigitalSignatureAlgorithmEnum.SHA3_384_WITH_ECDSA.getValue(), catalogueXml.getBytes());
            FileUtils.writeStringToFile(esSignature, new String(signature, StandardCharsets.UTF_8), Charset.defaultCharset());
        } catch (IOException ex) {
            log.error("Error while generating the exchange set signature: {}", ex.getMessage());
            throw new ValidationException(ex.getMessage());
        }

        // Put everything into a zip file
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipUtils.zipFile(esRootFolder, "S_100", zos);
        } catch(IOException ex) {
            log.error("Error while generating the compressed S-100 data: {}", ex.getMessage());
            throw new ValidationException(ex.getMessage());
        }

        // And return the zipped byte array
        return baos.toByteArray();
    }

    /**
     * This helper function will generate on the fly the contents of the
     * exchange set catalogue file and return them as a marshalled XML string.
     * This operation  might throw various errors which should be propagated
     * upwards, since we need to make sure that empty exchange sets won't
     * be generated when error occur.
     *
     * @param datasetDataMap the map of the dataset files with the associate data to be packaged
     * @return the generated exchange set catalogue
     */
    protected String generateExchangeSetContent(Map<String, DatasetData> datasetDataMap, SignatureCertificateDto signatureCertificate) throws CertificateException {
        // Use a distinct counter for the signature IDs
        final AtomicInteger signatureCounter = new AtomicInteger(1);

        // Initialise the S-100 exchange set builder
        final S100ExchangeCatalogueBuilder s100ExchangeCatalogueBuilder = new S100ExchangeCatalogueBuilder(
                (id, algorithm, payload) -> {
                    // The object ID is usually the file name, but we added the
                    // "file:/" prefix, so we need to remove it to match the data
                    DatasetData data = datasetDataMap.get(id.toString().replaceFirst("file:/",""));
                    S100SEDigitalSignature s100SEDigitalSignature = new S100SEDigitalSignature();
                    s100SEDigitalSignature.setId(String.format("sig%d", signatureCounter.getAndIncrement()));
                    s100SEDigitalSignature.setCertificateRef(String.format("cer%d", data.certificateId()));
                    s100SEDigitalSignature.setValue(this.signContent(data.certificateId, DigitalSignatureAlgorithmEnum.SHA3_384_WITH_ECDSA.getValue(), payload));
                    return s100SEDigitalSignature;
                })
                .setIdentifier(S201DatasetBuilder.generateDatasetId(this.datasetMrnPrefix + ":exchangeset", UUID.randomUUID()))
                .setDataServerIdentifier(UUID.nameUUIDFromBytes(this.serviceInformationConfig.name().getBytes()).toString())
                .setOrganization(this.serviceInformationConfig.organization())
                .setElectronicMailAddresses(this.serviceInformationConfig.electronicMailAddresses())
                .setPhone(this.serviceInformationConfig.phone())
                .setPhoneType(TelephoneType.VOICE)
                .setCity(this.serviceInformationConfig.city())
                .setPostalCode(this.serviceInformationConfig.postalCode())
                .setCountry(this.serviceInformationConfig.country())
                .setLocales(this.serviceInformationConfig.locales()
                        .stream()
                        .map(LocaleUtils::toLocale)
                        .collect(Collectors.toList()))
                .setAdministrativeArea(this.serviceInformationConfig.country())
                .setDescription(String.format("%s AtoN Service S-100 Exchange Set generated for SECOM at %s.",
                        this.serviceInformationConfig.organization(),
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                ))
                .setComment("This exchange set has been generated exclusively for testing. Please do not use in production!!!")
                .setProductSpecification(Collections.singletonList(GIRegistryInfo.getProductSpecification()))
                .setCertificatesByPem(Collections.singletonMap(String.format("cer%d", signatureCertificate.getCertificateId()), signatureCertificate.getCertificate()));

        // Now append all the metadata for all the involved dataset/delta pairs
        for(final DatasetData data : datasetDataMap.values()) {
            // Extract the basic information from the data
            final S201Dataset dataset = data.dataset;
            final DatasetContentLog delta = data.delta;

            // And add to the builder
            s100ExchangeCatalogueBuilder.addDatasetMetadata(builder -> builder
                    .setFileName("file:/" + data.datasetFile.getName())
                    .setDatasetID(S201DatasetBuilder.generateDatasetId(this.datasetMrnPrefix, dataset.getUuid()))
                    .setDescription(dataset.getDatasetIdentificationInformation().getDatasetAbstract())
                    .setCompressionFlag(false)
                    .setDataProtection(false)
                    .setProtectionScheme(S100ProtectionScheme.S_100_P_15)
                    .setCopyright(true)
                    .setClassification(SecurityClassification.UNCLASSIFIED)
                    .setPurpose(this.getDatasetPurpose(delta.getOperation()))
                    .setNotForNavigation(true)
                    .setSpecificUsage("testing")
                    .setEditionNumber(BigInteger.ONE)
                    .setUpdateNumber(delta.getSequenceNo())
                    .setUpdateApplicationDate(null)
                    .setIssueDate(delta.getGeneratedAt().toLocalDate())
                    .setIssueTime(delta.getGeneratedAt().toLocalTime())
                    .setBoundingBox(delta.getGeometry())
                    .setProductSpecification(GIRegistryInfo.getProductSpecification())
                    .setProducingAgency(this.serviceInformationConfig.organization())
                    .setProducingAgencyRole(RoleCode.CUSTODIAN)
                    .setProducerCode(this.serviceInformationConfig.ihoProducerCode())
                    .setEncodingFormat(S100EncodingFormat.GML)
                    .setDataCoverages(delta.getGeometry())
                    .setComment("Generated for testing by the GRA Research & Development Directorate")
                    .setMetadataDateStamp(LocalDate.now())
                    .setReplacedData(Optional.of(dataset).map(S201Dataset::getCancelled).orElse(false))
                    .setNavigationPurposes(Collections.singletonList(S100NavigationPurpose.OVERVIEW))
                    .setMaintenanceFrequency(MaintenanceFrequency.CONTINUAL)
                    .setDigitalSignatureReference(S100SEDigitalSignatureReference.ECDSA_384_SHA_3)
                    .build(delta.getContent().getBytes()));
        }

        // Generate the exchange set catalogue
        // Careful with any potential errors, we don't want to allow them
        final String exchangeCatalogueContent;
        try {
            exchangeCatalogueContent = S100ExchangeSetUtils.marshalS100ExchangeSetCatalogue(
                    s100ExchangeCatalogueBuilder.build());
        } catch (JAXBException | CertificateEncodingException ex) {
            log.error(ex.getMessage());
            throw new ValidationException(ex.getMessage());
        }

        // And return the outcome
        return exchangeCatalogueContent;
    }

    /**
     * This small helper function will generate the appropriate S-100 exchange
     * set file name according to the S-100 Edition 5.0.0 standard.
     *
     * @param uniqueId the file unique identifier
     * @param extension the file extension (e.g. XML);
     * @return the generated file name
     */
    protected String generateExchangeSetFileName(String uniqueId, String extension) {
        // Get the product code number
        final String productCodeNo = Optional.of(GIRegistryInfo.getProductSpecification())
                .map(S100ProductSpecification::getProductIdentifier)
                .filter(c -> c.indexOf("-") > 0)
                .filter(c -> c.indexOf("-") + 1 < c.length())
                .map(c -> c.substring(c.indexOf("-") + 1))
                .orElse(GIRegistryInfo.getProductSpecification().getProductIdentifier());
        // And construct the exchange set file name
        return String.format("%s%s%s.%s",
                productCodeNo,
                this.serviceInformationConfig.ihoProducerCode(),
                Optional.ofNullable(uniqueId).filter(Strings::isNotEmpty).orElse(""),
                Optional.ofNullable(extension).filter(Strings::isNotEmpty).orElse("XML"));
    }

    /**
     * This small helper function will use the cKeeper facility to acquire the
     * appropriate certificate for this service and sign the provided payload
     * using the selected signature algorithm.
     *
     * @param algorithm the algorithm to be used for the signature generation
     * @param content the content to be signed
     * @return the generated signature
     */
    protected byte[] signContent(BigInteger certificateId, String algorithm, byte[] content) {
        // Sign using the acquired certificate
        final Response signResponse = this.cKeeperClient.generateCertificateSignature(certificateId,
                algorithm,
                content
        );

        // Extract and return the output
        try {
            return signResponse.body()
                    .asInputStream()
                    .readAllBytes();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Maps the dataset content operations to the S-100 Purpose enumeration
     * options.
     *
     * @param operation the dataset content operation
     * @return the S-100 purpose
     */
    protected S100Purpose getDatasetPurpose(DatasetOperation operation) {
        switch (operation) {
            case CREATED -> {
                return S100Purpose.NEW_DATASET;
            }
            case CANCELLED, DELETED -> {
                return S100Purpose.CANCELLATION;
            }
            default -> {
                return S100Purpose.DELTA;
            }
        }
    }

    /**
     * The Internal Dataset Data Class.
     * <p/>
     * A small internal class to group the dataset information for the S-100
     * exchange set generation.
     *
     * @param datasetFile the dataset file to be packaged
     * @param certificateId the ID of the certificate to be used for signing
     * @param certificatePem the PEM of the certificate to be used for signing
     * @param dataset the S-201 dataset to be packaged
     * @param delta the delta information to be packaged
     * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
     */
    protected record DatasetData(File datasetFile,
                               BigInteger certificateId,
                               String certificatePem,
                               S201Dataset dataset,
                               DatasetContentLog delta) {}

}
