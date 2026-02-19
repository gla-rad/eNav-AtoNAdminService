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

import _int.iho.s_201.gml.cs0._2.Dataset;
import jakarta.xml.bind.DatatypeConverter;
import jakarta.xml.bind.JAXBException;
import org.grad.eNav.atonAdminService.TestFeignSecurityConfig;
import org.grad.eNav.atonAdminService.TestingConfiguration;
import org.grad.eNav.atonAdminService.components.HibernateSearchInit;
import org.grad.eNav.atonAdminService.components.SecomV1CertificateProviderImpl;
import org.grad.eNav.atonAdminService.components.SecomV1SignatureProviderImpl;
import org.grad.eNav.atonAdminService.controllers.secom.SecomRequestHeaders;
import org.grad.eNav.atonAdminService.models.domain.DatasetContent;
import org.grad.eNav.atonAdminService.models.domain.s201.S201Dataset;
import org.grad.eNav.atonAdminService.models.domain.secom.SubscriptionRequest;
import org.grad.eNav.atonAdminService.services.DatasetService;
import org.grad.eNav.atonAdminService.services.S100ExchangeSetService;
import org.grad.eNav.atonAdminService.services.UnLoCodeService;
import org.grad.eNav.atonAdminService.services.secom.v1.SecomV1SubscriptionService;
import org.grad.eNav.atonAdminService.utils.S201DatasetBuilder;
import org.grad.eNav.atonAdminService.utils.SecomUtils;
import org.grad.eNav.s201.utils.S201Utils;
import org.grad.secom.core.base.DigitalSignatureCertificate;
import org.grad.secom.core.base.SecomConstants;
import org.grad.secom.core.components.SecomSignatureFilter;
import org.grad.secom.core.exceptions.SecomValidationException;
import org.grad.secom.core.models.*;
import org.grad.secom.core.models.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.geomesa.utils.interop.WKTUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;

import static org.grad.secom.core.base.SecomConstants.SECOM_DATE_TIME_FORMAT;
import static org.grad.secom.core.base.SecomConstants.SECOM_DATE_TIME_FORMATTER;
import static org.grad.secom.core.interfaces.AcknowledgementSecomInterface.ACKNOWLEDGMENT_INTERFACE_PATH;
import static org.grad.secom.core.interfaces.CapabilitySecomInterface.CAPABILITY_INTERFACE_PATH;
import static org.grad.secom.core.interfaces.GetSecomInterface.GET_INTERFACE_PATH;
import static org.grad.secom.core.interfaces.GetSummarySecomInterface.GET_SUMMARY_INTERFACE_PATH;
import static org.grad.secom.core.interfaces.RemoveSubscriptionSecomInterface.REMOVE_SUBSCRIPTION_INTERFACE_PATH;
import static org.grad.secom.core.interfaces.SubscriptionSecomInterface.SUBSCRIPTION_INTERFACE_PATH;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
@Import({TestingConfiguration.class, TestFeignSecurityConfig.class})
class SecomV1ControllerTest {

    /**
     * The Reactive Web Test Client.
     */
    @Autowired
    WebTestClient webTestClient;

    /**
     * The Model Mapper.
     */
    @Autowired
    ModelMapper modelMapper;

    /**
     * The Dataset Service mock.
     */
    @MockitoBean
    DatasetService datasetService;

    /**
     * The SECOM Exchange Set Service.
     */
    @MockitoBean
    S100ExchangeSetService s100ExchangeSetService;

    /**
     * The UN/LOCODE Service mock.
     */
    @MockitoBean
    UnLoCodeService unLoCodeService;

    /**
     * The SECOM v1 Subscription Service mock.
     */
    @MockitoBean
    SecomV1SubscriptionService secomV1SubscriptionService;

    /**
     * The SECOM v1 Certificate Provider mock.
     */
    @MockitoBean
    SecomV1CertificateProviderImpl secomCertificateProvider;

    /**
     * The SECOM v1 Signature Provider mock.
     */
    @MockitoBean
    SecomV1SignatureProviderImpl secomSignatureProvider;

    /**
     * The SECOM v1 Signature Filter mock.
     *
     * This will block the actual SECOM signature verification process and will
     * allow out test messages to go through the signature filter without valid
     * signatures.
     */
    @MockitoBean
    SecomSignatureFilter secomSignatureFilter;

    // Test Variables
    private UUID queryDataReference;
    private ContainerTypeEnum queryContainerType;
    private SECOM_DataProductType queryDataProductType;
    private String queryProductVersion;
    private String queryGeometry;
    private String queryUnlocode;
    private LocalDateTime queryValidFrom;
    private LocalDateTime queryValidTo;
    private Integer queryPage;
    private Integer queryPageSize;
    private S201Dataset s201DataSet;
    private String s201DataSetAsXml;
    private DatasetContent datasetContent;
    private SubscriptionRequestObject subscriptionRequest;
    private SubscriptionRequest savedSubscriptionRequest;
    private RemoveSubscriptionObject removeSubscriptionObject;
    private AcknowledgementObject acknowledgementObject;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setUp() throws JAXBException {
        // Setup the query arguments
        // Test Variables
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        this.queryDataReference = UUID.randomUUID();
        this.queryContainerType = ContainerTypeEnum.S100_DataSet;
        this.queryDataProductType = SECOM_DataProductType.S201;
        this.queryProductVersion = "0.0.1";
        this.queryGeometry = WKTUtils.write(geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(-180, -90),
                new Coordinate(-180, 90),
                new Coordinate(180, 90),
                new Coordinate(180, -90),
                new Coordinate(-180, -90),
        }));
        this.queryUnlocode = "ADALV";
        this.queryValidFrom = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
        this.queryValidTo = LocalDateTime.of(3000, 1, 1, 0, 0, 0);
        this.queryPage = 1;
        this.queryPageSize = Integer.MAX_VALUE;

        // Construct a test S-201 Dataset
        this.s201DataSet = new S201Dataset("S201Dataset");
        this.s201DataSet.setUuid(this.queryDataReference);
        this.s201DataSet.setGeometry(geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(-180, -90),
                new Coordinate(-180, 90),
                new Coordinate(180, 90),
                new Coordinate(180, -90),
                new Coordinate(-180, -90),
        }));

        // Marshal the dataset content
        final S201DatasetBuilder s201DatasetBuilder = new S201DatasetBuilder(this.modelMapper);
        final Dataset dataset = s201DatasetBuilder.packageToDataset(this.s201DataSet, Collections.emptyList());
        this.s201DataSetAsXml = S201Utils.marshalS201(dataset, Boolean.FALSE);
        this.datasetContent = new DatasetContent();
        this.datasetContent.setId(BigInteger.ONE);
        this.datasetContent.setGeneratedAt(LocalDateTime.now());
        this.datasetContent.setContent(this.s201DataSetAsXml);
        this.datasetContent.setContentLength(BigInteger.valueOf(this.s201DataSetAsXml.length()));
        this.s201DataSet.setDatasetContent(this.datasetContent);

        // Setup the subscription requests and responses
        this.subscriptionRequest = new SubscriptionRequestObject();
        this.subscriptionRequest.setContainerType(ContainerTypeEnum.S100_DataSet);
        this.subscriptionRequest.setDataProductType(SECOM_DataProductType.S201);
        this.savedSubscriptionRequest = new SubscriptionRequest();
        this.savedSubscriptionRequest.setUuid(UUID.randomUUID());
        this.savedSubscriptionRequest.setContainerType(SecomUtils.translateSecomContainerTypeEnum(ContainerTypeEnum.S100_DataSet));
        this.savedSubscriptionRequest.setDataProductType(SecomUtils.translateSecomDataProductTypeEnum(SECOM_DataProductType.S201));
        this.removeSubscriptionObject = new RemoveSubscriptionObject();
        this.removeSubscriptionObject.setSubscriptionIdentifier(UUID.randomUUID());

        // Setup an acknowledgement
        this.acknowledgementObject = new AcknowledgementObject();
        EnvelopeAckObject envelopeAckObject = new EnvelopeAckObject();
        envelopeAckObject.setCreatedAt(Instant.now());
        envelopeAckObject.setTransactionIdentifier(UUID.randomUUID());
        envelopeAckObject.setAckType(AckTypeEnum.DELIVERED_ACK);
        envelopeAckObject.setEnvelopeSignatureCertificate("Signature Certificate");
        envelopeAckObject.setEnvelopeRootCertificateThumbprint("Root Certificate Thumbprint");
        envelopeAckObject.setEnvelopeSignatureTime(Instant.now());
        this.acknowledgementObject.setEnvelope(envelopeAckObject);
        this.acknowledgementObject.setEnvelopeSignature("Envelope Signature");
    }

    /**
     * Test that the SECOM Capability interface is configured properly and
     * returns the expected Capability Response Object output.
     */
    @Test
    void testCapability() {
        webTestClient.get()
                .uri("/api/secom" + CAPABILITY_INTERFACE_PATH)
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CapabilityResponseObject.class)
                .consumeWith(response -> {
                    CapabilityResponseObject capabilityResponseObject = response.getResponseBody();
                    assertNotNull(capabilityResponseObject);
                    assertNotNull(capabilityResponseObject.getCapability());
                    assertFalse(capabilityResponseObject.getCapability().isEmpty());
                    assertEquals(1, capabilityResponseObject.getCapability().size());
                    assertEquals(ContainerTypeEnum.S100_DataSet, capabilityResponseObject.getCapability().getFirst().getContainerType());
                    assertEquals(SECOM_DataProductType.S201, capabilityResponseObject.getCapability().getFirst().getDataProductType());
                    assertEquals("/xsd/S201.xsd", capabilityResponseObject.getCapability().getFirst().getProductSchemaUrl().getPath());
                    assertEquals("0.0.0", capabilityResponseObject.getCapability().getFirst().getServiceVersion());
                    assertFalse(capabilityResponseObject.getCapability().getFirst().getImplementedInterfaces().getAccess());
                    assertFalse(capabilityResponseObject.getCapability().getFirst().getImplementedInterfaces().getEncryptionKey());
                    assertTrue(capabilityResponseObject.getCapability().getFirst().getImplementedInterfaces().getGet());
                    assertTrue(capabilityResponseObject.getCapability().getFirst().getImplementedInterfaces().getSubscription());
                    assertFalse(capabilityResponseObject.getCapability().getFirst().getImplementedInterfaces().getGetByLink());
                    assertTrue(capabilityResponseObject.getCapability().getFirst().getImplementedInterfaces().getGetSummary());
                    assertFalse(capabilityResponseObject.getCapability().getFirst().getImplementedInterfaces().getUpload());
                    assertFalse(capabilityResponseObject.getCapability().getFirst().getImplementedInterfaces().getUploadLink());
                });
    }

    /**
     * Test that the SECOM Capability interface will respond with an HTTP
     * Status METHOD_NOT_ALLOWED if a method other than a get is requested.
     */
    @Test
    void testCapabilityMethodNotAllowed() {
        webTestClient.post()
                .uri("/api/secom" + CAPABILITY_INTERFACE_PATH)
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Test that the SECOM Get Summary interface is configured properly
     * and returns the expected Get Summary Response Object output.
     */
    @Test
    void testGetSummary() {
        doReturn(new PageImpl<>(Collections.singletonList(this.s201DataSet), Pageable.ofSize(this.queryPageSize), 1))
                .when(this.datasetService).findAll(any(), any(), any(), any(), any(), any());

         webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/secom" + GET_SUMMARY_INTERFACE_PATH)
                        .queryParam("containerType", this.queryContainerType.getValue())
                        .queryParam("dataProductType", this.queryDataProductType)
                        .queryParam("productVersion", this.queryProductVersion)
                        .queryParam("geometry", this.queryGeometry)
                        .queryParam("unlocode", this.queryUnlocode)
                        .queryParam("validFrom", DateTimeFormatter.ofPattern(SECOM_DATE_TIME_FORMAT).format(this.queryValidFrom))
                        .queryParam("validTo", DateTimeFormatter.ofPattern(SECOM_DATE_TIME_FORMAT).format(this.queryValidTo))
                        .queryParam("page", this.queryPage)
                        .queryParam("pageSize", this.queryPageSize)
                        .build())
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isOk()
                .expectBody(GetSummaryResponseObject.class)
                .consumeWith(response -> {
                    GetSummaryResponseObject getSummaryResponseObject = response.getResponseBody();
                    assertNotNull(getSummaryResponseObject);
                    assertNotNull(getSummaryResponseObject.getSummaryObject());
                    assertEquals(1, getSummaryResponseObject.getSummaryObject().size());
                    assertEquals(ContainerTypeEnum.S100_DataSet, getSummaryResponseObject.getSummaryObject().getFirst().getContainerType());
                    assertEquals(SECOM_DataProductType.S201, getSummaryResponseObject.getSummaryObject().getFirst().getDataProductType());
                    assertEquals(Boolean.FALSE, getSummaryResponseObject.getSummaryObject().getFirst().getDataCompression());
                    assertEquals(Boolean.FALSE, getSummaryResponseObject.getSummaryObject().getFirst().getDataProtection());
                    assertEquals(this.s201DataSet.getUuid(), getSummaryResponseObject.getSummaryObject().getFirst().getDataReference());
                    assertEquals(this.s201DataSet.getDatasetIdentificationInformation().getProductEdition(), getSummaryResponseObject.getSummaryObject().getFirst().getInfo_productVersion());
                    assertEquals(this.s201DataSet.getDatasetIdentificationInformation().getDatasetFileIdentifier(), getSummaryResponseObject.getSummaryObject().getFirst().getInfo_identifier());
                    assertEquals(this.s201DataSet.getDatasetIdentificationInformation().getDatasetTitle(), getSummaryResponseObject.getSummaryObject().getFirst().getInfo_name());
                    assertEquals(InfoStatusEnum.PRESENT.getValue(), getSummaryResponseObject.getSummaryObject().getFirst().getInfo_status());
                    assertEquals(this.s201DataSet.getDatasetIdentificationInformation().getDatasetAbstract(), getSummaryResponseObject.getSummaryObject().getFirst().getInfo_description());
                    assertEquals(this.s201DataSet.getLastUpdatedAt(), getSummaryResponseObject.getSummaryObject().getFirst().getInfo_lastModifiedDate());
                    assertNotNull(getSummaryResponseObject.getPagination());
                    assertEquals(Integer.MAX_VALUE, getSummaryResponseObject.getPagination().getMaxItemsPerPage());
                    assertEquals(1, getSummaryResponseObject.getPagination().getTotalItems());
                });
    }

    /**
     * Test that the SECOM Get Summary interface will return an HTTP Status
     * BAD_REQUEST if one of the provided query parameters is not formatted
     * properly
     */
    @Test
    void testGetSummaryBadRequest() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/secom" + GET_SUMMARY_INTERFACE_PATH)
                        .queryParam("containerType", this.queryContainerType.getValue())
                        .queryParam("dataProductType", this.queryDataProductType)
                        .queryParam("productVersion", this.queryProductVersion)
                        .queryParam("geometry", this.queryGeometry)
                        .queryParam("unlocode", this.queryUnlocode)
                        .queryParam("validFrom", "Badly Formatted Date")
                        .queryParam("validTo", "Another Badly Formatted Date")
                        .queryParam("page", this.queryPage)
                        .queryParam("pageSize", this.queryPageSize)
                        .build())
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isBadRequest();
    }

    /**
     * Test that the SECOM Get Summary interface will return an HTTP Status
     * METHOD_NOT_ALLOWED if a method other than a get is requested.
     */
    @Test
    void testGetSummaryMethodNotAllowed() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/secom" + GET_SUMMARY_INTERFACE_PATH)
                        .queryParam("containerType", this.queryContainerType.getValue())
                        .queryParam("dataProductType", this.queryDataProductType)
                        .queryParam("productVersion", this.queryProductVersion)
                        .queryParam("geometry", this.queryGeometry)
                        .queryParam("unlocode", this.queryUnlocode)
                        .queryParam("validFrom", DateTimeFormatter.ofPattern(SECOM_DATE_TIME_FORMAT).format(this.queryValidFrom))
                        .queryParam("validTo", DateTimeFormatter.ofPattern(SECOM_DATE_TIME_FORMAT).format(this.queryValidTo))
                        .queryParam("page", this.queryPage)
                        .queryParam("pageSize", this.queryPageSize)
                        .build())
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Test that the SECOM Get interface is configured properly and returns the
     * expected Get Response Object output.
     */
    @Test
    void testGet() throws CertificateEncodingException, IOException {
        // Mock the SECOM library certificate and signature providers
        X509Certificate mockCertificate = mock(X509Certificate.class);
        doReturn("certificate".getBytes()).when(mockCertificate).getEncoded();
        PublicKey mockPublicKey = mock(PublicKey.class);
        doReturn(mockPublicKey).when(mockCertificate).getPublicKey();
        X509Certificate mockRootCertificate = mock(X509Certificate.class);
        doReturn("rootCertificate".getBytes()).when(mockRootCertificate).getEncoded();
        DigitalSignatureCertificate digitalSignatureCertificate = new DigitalSignatureCertificate();
        digitalSignatureCertificate.setCertificateAlias("secom");
        digitalSignatureCertificate.setCertificate(mockCertificate);
        digitalSignatureCertificate.setPublicKey(mockPublicKey);
        digitalSignatureCertificate.setRootCertificate(mockRootCertificate);
        doReturn(digitalSignatureCertificate).when(this.secomCertificateProvider).getDigitalSignatureCertificate();
        doReturn(DigitalSignatureAlgorithmEnum.SHA3_384_WITH_ECDSA).when(this.secomSignatureProvider).getSignatureAlgorithm();
        doReturn("signature".getBytes()).when(this.secomSignatureProvider).generateSignature(any(), any(), any());

        // Mock the rest
        doReturn(new PageImpl<>(Collections.singletonList(this.s201DataSet), Pageable.ofSize(this.queryPageSize), 1))
                .when(this.datasetService).findAll(any(), any(), any(), any(), any(), any());

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/secom" + GET_INTERFACE_PATH)
                        .queryParam("dataReference", this.queryDataReference)
                        .queryParam("containerType", this.queryContainerType.getValue())
                        .queryParam("dataProductType", this.queryDataProductType)
                        .queryParam("productVersion", this.queryProductVersion)
                        .queryParam("geometry", this.queryGeometry)
                        .queryParam("unlocode", this.queryUnlocode)
                        .queryParam("validFrom", DateTimeFormatter.ofPattern(SECOM_DATE_TIME_FORMAT).format(this.queryValidFrom))
                        .queryParam("validTo", DateTimeFormatter.ofPattern(SECOM_DATE_TIME_FORMAT).format(this.queryValidTo))
                        .queryParam("page", this.queryPage)
                        .queryParam("pageSize", this.queryPageSize)
                        .build())
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isOk()
                .expectBody(GetResponseObject.class)
                .consumeWith(response -> {
                    GetResponseObject getResponseObject = response.getResponseBody();
                    assertNotNull(getResponseObject);
                    assertNotNull(getResponseObject.getDataResponseObject());
                    assertNotNull(getResponseObject.getPagination());
                    assertEquals(1, getResponseObject.getDataResponseObject().size());
                    assertNotNull(getResponseObject.getDataResponseObject().getFirst());
                    assertNotNull(getResponseObject.getDataResponseObject().getFirst().getData());
                    assertNotNull(getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata());
                    assertEquals(Boolean.FALSE, getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDataProtection());
                    assertEquals(Boolean.FALSE, getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getCompressionFlag());
                    assertEquals(SecomConstants.SECOM_PROTECTION_SCHEME, getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getProtectionScheme());
                    assertEquals(DigitalSignatureAlgorithmEnum.SHA3_384_WITH_ECDSA, getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDigitalSignatureReference());
                    assertNotNull(getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDigitalSignatureValue());
                    assertEquals(DatatypeConverter.printHexBinary("signature".getBytes()), getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDigitalSignatureValue().getDigitalSignature());
                    assertEquals(Base64.getEncoder().encodeToString("certificate".getBytes()), getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDigitalSignatureValue().getPublicCertificate());
                    assertEquals("a79fd87b7e6418a5085f88c21482e017eb0ef9a6", getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDigitalSignatureValue().getPublicRootCertificateThumbprint());
                    assertEquals(Integer.MAX_VALUE, getResponseObject.getPagination().getMaxItemsPerPage());
                    assertEquals(1, getResponseObject.getPagination().getTotalItems());

                    // Try to parse the incoming data
                    String s201Xml = new String(Base64.getDecoder().decode(getResponseObject.getDataResponseObject().getFirst().getData()));
                    try {
                        Dataset result = S201Utils.unmarshallS201(s201Xml);
                        assertNotNull(result);
                        assertNotNull(result.getId());
                        assertNotNull(result.getDatasetIdentificationInformation());
                        assertEquals(this.s201DataSet.getDatasetIdentificationInformation().getDatasetTitle(), result.getDatasetIdentificationInformation().getDatasetTitle());
                        assertEquals(this.s201DataSet.getDatasetIdentificationInformation().getDatasetFileIdentifier(), result.getDatasetIdentificationInformation().getDatasetFileIdentifier());
                        assertEquals(this.s201DataSet.getDatasetIdentificationInformation().getProductEdition(), result.getDatasetIdentificationInformation().getProductEdition());
                        assertEquals(this.s201DataSet.getDatasetIdentificationInformation().getDatasetTitle(), result.getDatasetIdentificationInformation().getDatasetTitle());
                    } catch (JAXBException ex) {
                        fail(ex);
                    }
                });
    }

    /**
     * Test that the SECOM Get interface is configured properly and also
     * supports S-100 Exchange Sets if requested.
     */
    @Test
    void testGetExchangeSet() throws CertificateEncodingException, IOException, JAXBException {
        // Mock the SECOM library certificate and signature providers
        X509Certificate mockCertificate = mock(X509Certificate.class);
        doReturn("certificate".getBytes()).when(mockCertificate).getEncoded();
        PublicKey mockPublicKey = mock(PublicKey.class);
        doReturn(mockPublicKey).when(mockCertificate).getPublicKey();
        X509Certificate mockRootCertificate = mock(X509Certificate.class);
        doReturn("rootCertificate".getBytes()).when(mockRootCertificate).getEncoded();
        DigitalSignatureCertificate digitalSignatureCertificate = new DigitalSignatureCertificate();
        digitalSignatureCertificate.setCertificateAlias("secom");
        digitalSignatureCertificate.setCertificate(mockCertificate);
        digitalSignatureCertificate.setPublicKey(mockPublicKey);
        digitalSignatureCertificate.setRootCertificate(mockRootCertificate);
        doReturn(digitalSignatureCertificate).when(this.secomCertificateProvider).getDigitalSignatureCertificate();
        doReturn(DigitalSignatureAlgorithmEnum.SHA3_384_WITH_ECDSA).when(this.secomSignatureProvider).getSignatureAlgorithm();
        doReturn("signature".getBytes()).when(this.secomSignatureProvider).generateSignature(any(), any(), any());

        // Mock the rest
        doReturn(new PageImpl<>(Collections.singletonList(this.s201DataSet), Pageable.ofSize(this.queryPageSize), 1))
                .when(this.datasetService).findAll(any(), any(), any(), any(), any(), any());
        doReturn("packagedExchangeSet".getBytes()).when(this.s100ExchangeSetService).packageToExchangeSet(any(), any(), any());

        // Set the container type to Exchange Set
        this.queryContainerType = ContainerTypeEnum.S100_ExchangeSet;

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/secom" + GET_INTERFACE_PATH)
                        .queryParam("dataReference", this.queryDataReference)
                        .queryParam("containerType", this.queryContainerType.getValue())
                        .queryParam("dataProductType", this.queryDataProductType)
                        .queryParam("productVersion", this.queryProductVersion)
                        .queryParam("geometry", this.queryGeometry)
                        .queryParam("unlocode", this.queryUnlocode)
                        .queryParam("validFrom", DateTimeFormatter.ofPattern(SECOM_DATE_TIME_FORMAT).format(this.queryValidFrom))
                        .queryParam("validTo", DateTimeFormatter.ofPattern(SECOM_DATE_TIME_FORMAT).format(this.queryValidTo))
                        .queryParam("page", this.queryPage)
                        .queryParam("pageSize", this.queryPageSize)
                        .build())
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isOk()
                .expectBody(GetResponseObject.class)
                .consumeWith(response -> {
                    GetResponseObject getResponseObject = response.getResponseBody();
                    assertNotNull(getResponseObject);
                    assertNotNull(getResponseObject.getDataResponseObject());
                    assertNotNull(getResponseObject.getPagination());
                    assertEquals(1, getResponseObject.getDataResponseObject().size());
                    assertNotNull(getResponseObject.getDataResponseObject().getFirst());
                    assertNotNull(getResponseObject.getDataResponseObject().getFirst().getData());
                    assertNotNull(getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata());
                    assertEquals(Boolean.FALSE, getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDataProtection());
                    assertEquals(Boolean.TRUE, getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getCompressionFlag());
                    assertEquals(SecomConstants.SECOM_PROTECTION_SCHEME, getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getProtectionScheme());
                    assertEquals(DigitalSignatureAlgorithmEnum.SHA3_384_WITH_ECDSA, getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDigitalSignatureReference());
                    assertNotNull(getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDigitalSignatureValue());
                    assertEquals(DatatypeConverter.printHexBinary("signature".getBytes()), getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDigitalSignatureValue().getDigitalSignature());
                    assertEquals(Base64.getEncoder().encodeToString("certificate".getBytes()), getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDigitalSignatureValue().getPublicCertificate());
                    assertEquals("a79fd87b7e6418a5085f88c21482e017eb0ef9a6", getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDigitalSignatureValue().getPublicRootCertificateThumbprint());
                    assertEquals(Integer.MAX_VALUE, getResponseObject.getPagination().getMaxItemsPerPage());
                    assertEquals(1, getResponseObject.getPagination().getTotalItems());

                    // Try to parse the incoming data
                    String exchangeSetBytes = new String(Base64.getDecoder().decode(getResponseObject.getDataResponseObject().getFirst().getData()));
                    assertEquals("packagedExchangeSet", exchangeSetBytes);
                });
    }

    /**
     * Test that the SECOM Get interface will return an HTTP Status BAD_REQUEST
     * if one of the provided query parameters is not formatted properly
     */
    @Test
    void testGetBadRequest() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/secom" + GET_INTERFACE_PATH)
                        .queryParam("dataReference", this.queryDataReference)
                        .queryParam("containerType", this.queryContainerType.getValue())
                        .queryParam("dataProductType", this.queryDataProductType)
                        .queryParam("productVersion", this.queryProductVersion)
                        .queryParam("geometry", this.queryGeometry)
                        .queryParam("unlocode", this.queryUnlocode)
                        .queryParam("validFrom", "Badly Formatted Date")
                        .queryParam("validTo", "Another Badly Formatted Date")
                        .queryParam("page", this.queryPage)
                        .queryParam("pageSize", this.queryPageSize)
                        .build())
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isBadRequest();
    }

    /**
     * Test that the SECOM Get interface will return an HTTP Status
     * METHOD_NOT_ALLOWED if a method other than a GET is requested.
     */
    @Test
    void testGetMethodNotAllowed() {
        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/secom" + GET_INTERFACE_PATH)
                        .queryParam("dataReference", this.queryDataReference)
                        .queryParam("containerType", this.queryContainerType.getValue())
                        .queryParam("dataProductType", this.queryDataProductType)
                        .queryParam("productVersion", this.queryProductVersion)
                        .queryParam("geometry", this.queryGeometry)
                        .queryParam("unlocode", this.queryUnlocode)
                        .queryParam("validFrom", SECOM_DATE_TIME_FORMATTER.format(this.queryValidFrom))
                        .queryParam("validTo", SECOM_DATE_TIME_FORMATTER.format(this.queryValidTo))
                        .queryParam("page", this.queryPage)
                        .queryParam("pageSize", this.queryPageSize)
                        .build())
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Test that the SECOM Subscription interface is configured properly and
     * returns the expected Subscription Response Object output.
     */
    @Test
    void testSubscription() {
        doReturn(savedSubscriptionRequest).when(this.secomV1SubscriptionService).save(any(), any());

        webTestClient.post()
                .uri("/api/secom" + SUBSCRIPTION_INTERFACE_PATH)
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just(subscriptionRequest), SubscriptionRequestObject.class))
                .exchange()
                .expectStatus().isOk()
                .expectBody(SubscriptionResponseObject.class)
                .consumeWith(response -> {
                    SubscriptionResponseObject subscriptionResponseObject = response.getResponseBody();
                    assertNotNull(subscriptionResponseObject);
                    assertEquals(savedSubscriptionRequest.getUuid(), subscriptionResponseObject.getSubscriptionIdentifier());
                    assertEquals("Subscription successfully created", subscriptionResponseObject.getMessage());
                });
    }

    /**
     * Test that the SECOM Subscription interface will return an HTTP Status
     * BAD_REQUEST if a validation error occurs.
     */
    @Test
    void testSubscriptionBadRequest() {
        doThrow(SecomValidationException.class).when(this.secomV1SubscriptionService).save(any(), any());

        webTestClient.post()
                .uri("/api/secom" + SUBSCRIPTION_INTERFACE_PATH)
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just(subscriptionRequest), SubscriptionRequestObject.class))
                .exchange()
                .expectStatus().isBadRequest();
    }

    /**
     * Test that the SECOM Subscription interface will return an HTTP Status
     * METHOD_NOT_ALLOWED if a method other than a get is requested.
     */
    @Test
    void testSubscriptionMethodNotAllowed() {
        doThrow(SecomValidationException.class).when(this.secomV1SubscriptionService).save(any(), any());

        webTestClient.get()
                .uri("/api/secom" + SUBSCRIPTION_INTERFACE_PATH)
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Test that the SECOM Remove Subscription interface is configured properly
     * and returns the expected Remove Subscription Response Object output.
     */
    @Test
    void testRemoveSubscription() {
        doReturn(removeSubscriptionObject.getSubscriptionIdentifier()).when(this.secomV1SubscriptionService).delete(any());

        webTestClient.method(HttpMethod.DELETE)
                .uri("/api/secom" + REMOVE_SUBSCRIPTION_INTERFACE_PATH)
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just(removeSubscriptionObject), RemoveSubscriptionObject.class))
                .exchange()
                .expectStatus().isOk()
                .expectBody(RemoveSubscriptionResponseObject.class)
                .consumeWith(response -> {
                    RemoveSubscriptionResponseObject removeSubscriptionResponseObject = response.getResponseBody();
                    assertNotNull(removeSubscriptionResponseObject);
                    assertEquals(String.format("Subscription %s removed", removeSubscriptionObject.getSubscriptionIdentifier()), removeSubscriptionResponseObject.getMessage());
                });
    }

    /**
     * Test that the SECOM Remove Subscription interface will return an HTTP
     * Status BAD_REQUEST if a validation error occurs.
     */
    @Test
    void testRemoveSubscriptionBadRequest() {
        doThrow(SecomValidationException.class).when(this.secomV1SubscriptionService).save(any(), any());

        webTestClient.post()
                .uri("/api/secom" + SUBSCRIPTION_INTERFACE_PATH)
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just(removeSubscriptionObject), RemoveSubscriptionObject.class))
                .exchange()
                .expectStatus().isBadRequest();
    }

    /**
     * Test that the SECOM Remove Subscription interface will return an HTTP
     * Status METHOD_NOT_ALLOWED if a method other than a get is requested.
     */
    @Test
    void testRemoveSubscriptionMethodNotAllowed() {
        doThrow(SecomValidationException.class).when(this.secomV1SubscriptionService).save(any(), any());

        webTestClient.get()
                .uri("/api/secom" + SUBSCRIPTION_INTERFACE_PATH)
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Test that the SECOM Acknowledgement interface is configured properly
     * and returns the expected Acknowledgement Response Object output.
     */
    @Test
    void testAcknowledgement() {
        webTestClient.method(HttpMethod.POST)
                .uri("/api/secom" + ACKNOWLEDGMENT_INTERFACE_PATH)
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just(acknowledgementObject), AcknowledgementObject.class))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AcknowledgementResponseObject.class)
                .consumeWith(response -> {
                    AcknowledgementResponseObject acknowledgementResponseObject = response.getResponseBody();
                    assertNotNull(acknowledgementResponseObject);
                    assertEquals(String.format("Successfully received ACK for %s", acknowledgementObject.getEnvelope().getTransactionIdentifier()), acknowledgementResponseObject.getMessage());
                });
    }

    /**
     * Test that the SECOM Acknowledgement interface will return an HTTP
     * Status BAD_REQUEST if a validation error occurs.
     */
    @Test
    void testAcknowledgementBadRequest() {
        webTestClient.post()
                .uri("/api/secom" + ACKNOWLEDGMENT_INTERFACE_PATH)
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just("Invalid acknowledgement object"), String.class))
                .exchange()
                .expectStatus().isBadRequest();
    }

    /**
     * Test that the SECOM Acknowledgement interface will return an HTTP
     * Status METHOD_NOT_ALLOWED if a method other than a get is requested.
     */
    @Test
    void testAcknowledgementNotAllowed() {
        webTestClient.get()
                .uri("/api/secom" + ACKNOWLEDGMENT_INTERFACE_PATH)
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }
}