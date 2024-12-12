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
import feign.FeignException;
import feign.Response;
import jakarta.xml.bind.JAXBException;
import org.grad.eNav.atonAdminService.feign.CKeeperClient;
import org.grad.eNav.atonAdminService.models.domain.DatasetContent;
import org.grad.eNav.atonAdminService.models.domain.DatasetContentLog;
import org.grad.eNav.atonAdminService.models.domain.s100.ServiceInformationConfig;
import org.grad.eNav.atonAdminService.models.domain.s201.S201Dataset;
import org.grad.eNav.atonAdminService.models.dtos.SignatureCertificateDto;
import org.grad.eNav.atonAdminService.models.enums.DatasetOperation;
import org.grad.eNav.atonAdminService.models.enums.DatasetType;
import org.grad.eNav.atonAdminService.utils.ZipUtilsTest;
import org.grad.eNav.s100.enums.MaintenanceFrequency;
import org.grad.eNav.s100.enums.RoleCode;
import org.grad.eNav.s100.enums.SecurityClassification;
import org.grad.eNav.s100.utils.S100ExchangeSetUtils;
import org.grad.eNav.s201.utils.GIRegistryInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S100ExchangeSetServiceTest {

    /**
     * The Tested Service.
     */
    @InjectMocks
    @Spy
    S100ExchangeSetService s100ExchangeSetService;

    /**
     * The Service Information Config mock.
     */
    @Mock
    ServiceInformationConfig serviceInformationConfig;

    /**
     * The Dataset Content Log Service mock.
     */
    @Mock
    DatasetContentLogService datasetContentLogService;

    /**
     * The CKeeper Client.
     */
    @Mock
    CKeeperClient cKeeperClient;

    // Test Variables
    private S201Dataset s201Dataset;
    private DatasetContent datasetContent;
    private List<DatasetContentLog> datasetContentLogList;
    private String testCertificatePem;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setUp() throws IOException {
        // Create a temp geometry factory to get a test geometries
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

        // Create a new S-201 dataset
        this.s201Dataset = new S201Dataset("S-201 Dataset");
        this.s201Dataset.setUuid(UUID.randomUUID());
        this.s201Dataset.setGeometry(factory.createPoint(new Coordinate(52.98, 1.28)));
        this.s201Dataset.setCreatedAt(LocalDateTime.now());
        this.s201Dataset.setLastUpdatedAt(LocalDateTime.now());
        this.datasetContent = new DatasetContent();
        this.datasetContent.setId(BigInteger.ONE);
        this.datasetContent.setContent("S-201 dataset content");
        this.datasetContent.setContentLength(BigInteger.valueOf(this.datasetContent.getContent().length()));
        this.datasetContent.setGeneratedAt(LocalDateTime.now());
        this.s201Dataset.setDatasetContent(this.datasetContent);

        // Create a list of dataset content logs
        this.datasetContentLogList = new ArrayList<>();
        for(long i=0; i<10; i++) {
            DatasetContentLog datasetContentLog = new DatasetContentLog();
            datasetContentLog.setUuid(UUID.randomUUID());
            datasetContentLog.setId(BigInteger.valueOf(i));
            datasetContentLog.setDatasetType(DatasetType.S201);
            datasetContentLog.setSequenceNo(BigInteger.ONE);
            datasetContentLog.setGeneratedAt(LocalDateTime.now());
            datasetContentLog.setGeometry(factory.createPoint(new Coordinate(i%180, i%90)));
            datasetContentLog.setOperation(i==0? DatasetOperation.CREATED:DatasetOperation.UPDATED);
            datasetContentLog.setContent("Existing Dataset Content " + i);
            datasetContentLog.setContentLength(BigInteger.valueOf(datasetContentLog.getContent().length()));
            datasetContentLog.setDelta("Existing Dataset Delta " + i);
            datasetContentLog.setDeltaLength(BigInteger.valueOf(datasetContentLog.getDelta().length()));
            this.datasetContentLogList.add(datasetContentLog);
        }

        // Read the test certificate for using in the signature generation
        final InputStream in = ClassLoader.getSystemResourceAsStream("test.pem");
        assertNotNull(in);
        this.testCertificatePem = new String(in.readAllBytes(), StandardCharsets.UTF_8)
                .replaceAll("-----BEGIN CERTIFICATE-----","")
                .replaceAll("-----END CERTIFICATE-----","")
                .replaceAll(System.lineSeparator(),"")
                .replaceAll("\r\n","")
                .replaceAll("\n","");
    }

    /**
     * Test that we can successfully package an exchange set into a zip file
     * with the correct directory structure and that users will be able to
     * use that to retrieve the included S-201 data.
     */
    @Test
    void testPackageToExchangeSet(@TempDir Path tempDir) throws CertificateException, JAXBException, IOException {
        // First set a temporary directory to generate the zip in
        this.s100ExchangeSetService.s100ExchangeSetDir = tempDir.toString();
        this.s100ExchangeSetService.s100ExchangeSetPrefix = "atonServiceTest";

        // Create the signature certificate to be used for signing
        SignatureCertificateDto signatureCertificate = new SignatureCertificateDto();
        signatureCertificate.setCertificateId(BigInteger.ONE);
        signatureCertificate.setCertificate(this.testCertificatePem);

        // Mock the further internal operations
        doReturn("XX00").when(this.serviceInformationConfig).ihoProducerCode();
        doReturn(signatureCertificate).when(cKeeperClient).getSignatureCertificate(any(), any(), any(), any());
        doReturn(this.datasetContentLogList).when(this.datasetContentLogService).findForUuidDuring(any(), any(), any());
        doReturn("catalogXMLContent").when(this.s100ExchangeSetService).generateExchangeSetContent(any(), any());
        doReturn("signature".getBytes()).when(this.s100ExchangeSetService).signContent(any(), any(), any());

        // Perform the service call
        final byte[] result = this.s100ExchangeSetService.packageToExchangeSet(Collections.singletonList(this.s201Dataset), null, null);

        // Make sure the zip file is valid
        assertNotNull(result);
        // And contains all the necessary folders/files
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byteArrayOutputStream.writeBytes(result);
            byteArrayOutputStream.close();
            ZipUtilsTest.assertZipContainsEntry(byteArrayOutputStream, new ZipEntry("S_100/"));
            ZipUtilsTest.assertZipContainsEntry(byteArrayOutputStream, new ZipEntry("S_100/S-201/"));
            ZipUtilsTest.assertZipContainsEntry(byteArrayOutputStream, new ZipEntry("S_100/S-201/DATASET_FILES/"));
            for(int i=0; i<this.datasetContentLogList.size(); i++) {
                // Guess the dataset file name
                final String datasetFilename = String.format("201XX00%s-%d.XML", this.s201Dataset.getUuid(), i);
                ZipUtilsTest.assertZipContainsEntry(byteArrayOutputStream, new ZipEntry("S_100/S-201/DATASET_FILES/" + datasetFilename));
            }
            ZipUtilsTest.assertZipContainsEntry(byteArrayOutputStream, new ZipEntry("S_100/CATALOG.XML"));
            ZipUtilsTest.assertZipContainsEntry(byteArrayOutputStream, new ZipEntry("S_100/CATALOG.SIGN"));
        }
    }

    /**
     * Test that we can successfully generate the S-100 exchange set CATALOG.XML
     * content using the imported library and all fields and populated as
     * expected.
     */
    @Test
    void testGenerateExchangeSetContent() throws CertificateException, JAXBException {
        // Create some test dataset-data
        final AtomicInteger index = new AtomicInteger(0);
        Map<String, S100ExchangeSetService.DatasetData> datasetData = this.datasetContentLogList.stream()
                .map(log -> new S100ExchangeSetService.DatasetData(
                        new File(String.format("s201-msg-%d.xml", index.getAndIncrement())),
                        BigInteger.ONE,
                        this.testCertificatePem,
                        this.s201Dataset,
                        log)
                )
                .collect(Collectors.toMap(d -> d.datasetFile().getName(), Function.identity()));

        // Create the signature certificate to be used for signing
        SignatureCertificateDto signatureCertificate = new SignatureCertificateDto();
        signatureCertificate.setCertificateId(BigInteger.ONE);
        signatureCertificate.setCertificate(this.testCertificatePem);

        // Mock the necessary service configuration and signature operations
        doReturn("XX00").when(this.serviceInformationConfig).ihoProducerCode();
        doReturn("testAtonService").when(this.serviceInformationConfig).name();
        doReturn("organization").when(this.serviceInformationConfig).organization();
        doReturn("phone").when(this.serviceInformationConfig).phone();
        doReturn("city").when(this.serviceInformationConfig).city();
        doReturn("postalCode").when(this.serviceInformationConfig).postalCode();
        doReturn("country").when(this.serviceInformationConfig).country();
        doReturn("signature".getBytes()).when(this.s100ExchangeSetService).signContent(any(), any(), any());

        // And perform the service call
        final String result = this.s100ExchangeSetService.generateExchangeSetContent(datasetData, signatureCertificate);

        // Make sure it is not empty
        assertNotNull(result);

        // Unmarshall the XML and check some of its individual fields
        final S100ExchangeCatalogue s100ExchangeCatalogue = S100ExchangeSetUtils.unmarshallS100ExchangeSetCatalogue(result);
        assertNotNull(s100ExchangeCatalogue.getIdentifier());
        assertNotNull(s100ExchangeCatalogue.getIdentifier().getIdentifier());
        assertNotNull(s100ExchangeCatalogue.getContact());
        assertNotNull(s100ExchangeCatalogue.getContact().getOrganization());
        assertNotNull(s100ExchangeCatalogue.getContact().getOrganization().getCharacterString());
        assertEquals("organization", s100ExchangeCatalogue.getContact().getOrganization().getCharacterString().getValue());
        assertNotNull(s100ExchangeCatalogue.getContact().getPhone());
        assertNotNull(s100ExchangeCatalogue.getContact().getPhone().getNumber());
        assertNotNull(s100ExchangeCatalogue.getContact().getPhone().getNumber().getCharacterString());
        assertEquals("phone", s100ExchangeCatalogue.getContact().getPhone().getNumber().getCharacterString().getValue());
        assertNotNull(s100ExchangeCatalogue.getContact().getAddress());
        assertNotNull(s100ExchangeCatalogue.getContact().getAddress().getCity());
        assertNotNull(s100ExchangeCatalogue.getContact().getAddress().getCity().getCharacterString());
        assertEquals("city", s100ExchangeCatalogue.getContact().getAddress().getCity().getCharacterString().getValue());
        assertNotNull(s100ExchangeCatalogue.getContact().getAddress());
        assertNotNull(s100ExchangeCatalogue.getContact().getAddress().getPostalCode());
        assertNotNull(s100ExchangeCatalogue.getContact().getAddress().getPostalCode().getCharacterString());
        assertEquals("postalCode", s100ExchangeCatalogue.getContact().getAddress().getPostalCode().getCharacterString().getValue());
        assertNotNull(s100ExchangeCatalogue.getContact().getAddress());
        assertNotNull(s100ExchangeCatalogue.getContact().getAddress().getCountry());
        assertNotNull(s100ExchangeCatalogue.getContact().getAddress().getCountry().getCharacterString());
        assertEquals("country", s100ExchangeCatalogue.getContact().getAddress().getCountry().getCharacterString().getValue());
        assertNotNull(s100ExchangeCatalogue.getContact().getAddress());
        assertNotNull(s100ExchangeCatalogue.getContact().getAddress().getAdministrativeArea());
        assertNotNull(s100ExchangeCatalogue.getContact().getAddress().getAdministrativeArea().getCharacterString());
        assertEquals("country", s100ExchangeCatalogue.getContact().getAddress().getAdministrativeArea().getCharacterString().getValue());
        assertNotNull(s100ExchangeCatalogue.getDatasetDiscoveryMetadata());
        assertNotNull(s100ExchangeCatalogue.getDatasetDiscoveryMetadata().getS100DatasetDiscoveryMetadatas());
        assertEquals(this.datasetContentLogList.size(), s100ExchangeCatalogue.getDatasetDiscoveryMetadata().getS100DatasetDiscoveryMetadatas().size());
        assertNotNull(s100ExchangeCatalogue.getProductSpecifications());
        assertEquals(1, s100ExchangeCatalogue.getProductSpecifications().size());
        assertEquals(GIRegistryInfo.getProductSpecification().getProductIdentifier(), s100ExchangeCatalogue.getProductSpecifications().get(0).getProductIdentifier());
        assertEquals(GIRegistryInfo.getProductSpecification().getName(), s100ExchangeCatalogue.getProductSpecifications().get(0).getName());
        assertEquals(GIRegistryInfo.getProductSpecification().getVersion(), s100ExchangeCatalogue.getProductSpecifications().get(0).getVersion());
        assertEquals(GIRegistryInfo.getProductSpecification().getDate(), s100ExchangeCatalogue.getProductSpecifications().get(0).getDate());
        assertEquals(GIRegistryInfo.getProductSpecification().getCompliancyCategory(), s100ExchangeCatalogue.getProductSpecifications().get(0).getCompliancyCategory());

        // Check each individual dataset file log
        for(int i=0; i<this.datasetContentLogList.size(); i++) {
            // get the dataset discovery metadata
            final S100DatasetDiscoveryMetadata metadata = s100ExchangeCatalogue.getDatasetDiscoveryMetadata().getS100DatasetDiscoveryMetadatas().get(0);
            assertNotNull(metadata);
            assertTrue(metadata.getFileName().startsWith("file:/s201-msg-"));
            assertTrue(metadata.getFileName().endsWith(".xml"));
            assertNotNull(metadata.getDescription());
            assertNotNull(metadata.getDescription().getCharacterString());
            assertEquals(this.s201Dataset.getDatasetIdentificationInformation().getDatasetAbstract(), metadata.getDescription().getCharacterString().getValue());
            assertFalse(metadata.isCompressionFlag());
            assertFalse(metadata.isDataProtection());
            assertEquals(S100ProtectionScheme.S_100_P_15, metadata.getProtectionScheme());
            assertTrue(metadata.isCopyright());
            assertNotNull(metadata.getClassification());
            assertNotNull(metadata.getClassification().getMDClassificationCode());
            assertEquals(SecurityClassification.UNCLASSIFIED.getValue(), metadata.getClassification().getMDClassificationCode().getValue());
            assertEquals(S100Purpose.DELTA, metadata.getPurpose());
            assertTrue(metadata.isNotForNavigation());
            assertNotNull(metadata.getSpecificUsage());
            assertNotNull(metadata.getSpecificUsage().getMDUsage());
            assertNotNull(metadata.getSpecificUsage().getMDUsage().getSpecificUsage());
            assertNotNull(metadata.getSpecificUsage().getMDUsage().getSpecificUsage().getCharacterString());
            assertEquals("testing", metadata.getSpecificUsage().getMDUsage().getSpecificUsage().getCharacterString().getValue());
            assertEquals(BigInteger.ONE, metadata.getEditionNumber());
            assertNotNull(metadata.getUpdateNumber());
            assertNotNull(metadata.getProductSpecification());
            assertEquals(GIRegistryInfo.getProductSpecification().getProductIdentifier(), metadata.getProductSpecification().getProductIdentifier());
            assertEquals(GIRegistryInfo.getProductSpecification().getName(), metadata.getProductSpecification().getName());
            assertEquals(GIRegistryInfo.getProductSpecification().getVersion(), metadata.getProductSpecification().getVersion());
            assertEquals(GIRegistryInfo.getProductSpecification().getDate(), metadata.getProductSpecification().getDate());
            assertEquals(GIRegistryInfo.getProductSpecification().getCompliancyCategory(), metadata.getProductSpecification().getCompliancyCategory());
            assertNotNull(metadata.getProducingAgency());
            assertNotNull(metadata.getProducingAgency().getCIResponsibility());
            assertNotNull(metadata.getProducingAgency().getCIResponsibility().getParties());
            assertEquals(1, metadata.getProducingAgency().getCIResponsibility().getParties().size());
            assertNotNull(metadata.getProducingAgency().getCIResponsibility().getParties().get(0));
            assertNotNull(metadata.getProducingAgency().getCIResponsibility().getParties().get(0).getAbstractCIParty());
            assertNotNull(metadata.getProducingAgency().getCIResponsibility().getParties().get(0).getAbstractCIParty().getValue());
            assertNotNull(metadata.getProducingAgency().getCIResponsibility().getParties().get(0).getAbstractCIParty().getValue().getName());
            assertNotNull(metadata.getProducingAgency().getCIResponsibility().getParties().get(0).getAbstractCIParty().getValue().getName().getCharacterString());
            assertEquals("organization", metadata.getProducingAgency().getCIResponsibility().getParties().get(0).getAbstractCIParty().getValue().getName().getCharacterString().getValue());
            assertNotNull(metadata.getProducingAgency().getCIResponsibility().getRole());
            assertNotNull(metadata.getProducingAgency().getCIResponsibility().getRole().getCIRoleCode());
            assertEquals(RoleCode.CUSTODIAN.getValue(), metadata.getProducingAgency().getCIResponsibility().getRole().getCIRoleCode().getValue());
            assertEquals("XX00", metadata.getProducerCode());
            assertNotNull(metadata.getEncodingFormat());
            assertEquals(S100EncodingFormat.GML, metadata.getEncodingFormat().getValue());
            assertNotNull(metadata.getComment());
            assertNotNull(metadata.getComment().getCharacterString());
            assertEquals("Generated for testing by the GRA Research & Development Directorate", metadata.getComment().getCharacterString().getValue());
            assertFalse(metadata.isReplacedData());
            assertNotNull(metadata.getNavigationPurposes());
            assertTrue(metadata.getNavigationPurposes().contains(S100NavigationPurpose.OVERVIEW));
            assertNotNull(metadata.getResourceMaintenance());
            assertNotNull(metadata.getResourceMaintenance().getMDMaintenanceInformation());
            assertNotNull(metadata.getResourceMaintenance().getMDMaintenanceInformation().getMaintenanceAndUpdateFrequency());
            assertNotNull(metadata.getResourceMaintenance().getMDMaintenanceInformation().getMaintenanceAndUpdateFrequency().getMDMaintenanceFrequencyCode());
            assertEquals(MaintenanceFrequency.CONTINUAL.getValue(), metadata.getResourceMaintenance().getMDMaintenanceInformation().getMaintenanceAndUpdateFrequency().getMDMaintenanceFrequencyCode().getValue());
            assertNotNull(metadata.getResourceMaintenance().getMDMaintenanceInformation().getMaintenanceDates());
            assertTrue(metadata.getResourceMaintenance().getMDMaintenanceInformation().getMaintenanceDates().isEmpty());
            assertNull(metadata.getResourceMaintenance().getMDMaintenanceInformation().getUserDefinedMaintenanceFrequency());
            assertNotNull(metadata.getDigitalSignatureReference());
            assertNotNull(metadata.getDigitalSignatureReference().getValue());
            assertEquals(S100SEDigitalSignatureReference.ECDSA_384_SHA_3.value(), metadata.getDigitalSignatureReference().getValue().value());
            assertNotNull(metadata.getDigitalSignatureValues());
            assertEquals(1, metadata.getDigitalSignatureValues().size());
            assertNotNull(metadata.getDigitalSignatureValues().get(0));
            assertNotNull(metadata.getDigitalSignatureValues().get(0).getS100SEDigitalSignature());
            assertNotNull(metadata.getDigitalSignatureValues().get(0).getS100SEDigitalSignature().getValue());
            assertEquals("signature", new String(metadata.getDigitalSignatureValues().get(0).getS100SEDigitalSignature().getValue().getValue(), StandardCharsets.UTF_8));
        }
    }

    /**
     * Test that the exchange set file names we generate conform to the
     * specifications in the S-100 Edition 5.0.0 standard. Note that this
     * does default tot the S-201 data product specification used. Any changes
     * to the data product might result in this tests requiring updates.
     */
    @Test
    void testGenerateExchangeSetFileName() {
        doReturn("XX00").when(this.serviceInformationConfig).ihoProducerCode();
        assertEquals("201XX00.XML", this.s100ExchangeSetService.generateExchangeSetFileName(null, null));
        assertEquals("201XX00.XML", this.s100ExchangeSetService.generateExchangeSetFileName("", ""));
        assertEquals("201XX00test.ext", this.s100ExchangeSetService.generateExchangeSetFileName("test", "ext"));
    }

    /**
     * Test that we can successfully sing a provided content (in bytes) no
     * matter what that is, using the provided Certificate Keeper client.
     */
    @Test
    void testSignContent() throws IOException {
        // Create a test signature
        final String signature = "signature";

        // Mock a cKeeper certificate signature response
        Response response = mock(Response.class);
        Response.Body responseBody = mock(Response.Body.class);
        doReturn(responseBody).when(response).body();
        doReturn(new ByteArrayInputStream(signature.getBytes())).when(responseBody).asInputStream();
        doReturn(response).when(this.cKeeperClient).generateCertificateSignature(any(), any(), any());

        // Perform the service call
        byte[] result = this.s100ExchangeSetService.signContent(BigInteger.ONE, "random", "content".getBytes());

        // Assert the signature looks OK
        assertEquals(signature, new String(result, StandardCharsets.UTF_8));
    }

    /**
     * Test that if we get an error while trying to sign a certain content,
     * then the signature function will throw a runtime exception to inform
     * the caller.
     */
    @Test
    void testSignContentFailed() {
        // Make sure an exception is thrown
        doThrow(FeignException.class).when(this.cKeeperClient).generateCertificateSignature(any(), any(), any());

        // Perform the service call
        assertThrows(RuntimeException.class, () ->
                this.s100ExchangeSetService.signContent(BigInteger.ONE, "random", "content".getBytes()));
    }

    /**
     * Test that we can correctly identify the dataset purspose of an exchange
     * set based on the dataset operation.
     */
    @Test
    void testGetDatasetPurpose() {
        assertEquals(S100Purpose.NEW_DATASET, this.s100ExchangeSetService.getDatasetPurpose(DatasetOperation.CREATED));
        assertEquals(S100Purpose.CANCELLATION, this.s100ExchangeSetService.getDatasetPurpose(DatasetOperation.CANCELLED));
        assertEquals(S100Purpose.CANCELLATION, this.s100ExchangeSetService.getDatasetPurpose(DatasetOperation.DELETED));
        assertEquals(S100Purpose.DELTA, this.s100ExchangeSetService.getDatasetPurpose(DatasetOperation.UPDATED));
        assertEquals(S100Purpose.DELTA, this.s100ExchangeSetService.getDatasetPurpose(DatasetOperation.AUTO));
        assertEquals(S100Purpose.DELTA, this.s100ExchangeSetService.getDatasetPurpose(DatasetOperation.OTHER));
    }

}