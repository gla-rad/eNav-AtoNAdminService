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

package org.grad.eNav.atonAdminService.pacts.secomV2;

import au.com.dius.pact.provider.junitsupport.State;
import feign.Response;
import org.grad.eNav.atonAdminService.feign.CKeeperClient;
import org.grad.eNav.atonAdminService.models.domain.DatasetContent;
import org.grad.eNav.atonAdminService.models.domain.s201.S201Dataset;
import org.grad.eNav.atonAdminService.models.dtos.SignatureCertificateDto;
import org.grad.eNav.atonAdminService.services.DatasetService;
import org.grad.eNav.atonAdminService.services.UnLoCodeService;
import org.grad.secom.core.utils.SecomPemUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * The interface for testing the SECOM Get controller using the Pacts
 * consumer driver contracts.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public interface GetSecomControllerTestInterface {

    /**
     * Provides a geometry factory to setup test geometries.
     *
     * @return the test geometry factory
     */
    GeometryFactory getGeometryFactory();

    /**
     * Provides the mocked cKeeper client to the tests.
     *
     * @return the mocked cKeeper client
     */
    CKeeperClient getCKeeperClient();

    /**
     * Provides the mocked dataset service to the tests.
     *
     * @return the mocked dataset service
     */
    DatasetService getDatasetService();

    /**
     * Provides the mocked UnLoCode service to the tests.
     *
     * @return the mocked UnLoCode service
     */
    UnLoCodeService getUnLoCodeService();

    /**
     * Test that the SECOM Get interface will return an appropriate response on
     * various queries.
     *
     * @param data the request data
     */
    @State("Test SECOM Get Interface") // Method will be run before testing interactions that require "with-data" state
    default void testSecomGetSuccess(Map<?,?> data) throws IOException, CertificateException {
        // Read some test data - Certificates should be minified without headers
        final String s201Data = new String(new ClassPathResource("s201-msg.xml").getInputStream().readAllBytes());
        final String pemData = new String(new ClassPathResource("test.pem").getInputStream().readAllBytes());
        final String minPemData = SecomPemUtils.getMinifiedPemFromCertString(pemData);

        // Create a new dataset for testing
        final S201Dataset s201Dataset = new S201Dataset("TestDataset");
        s201Dataset.setUuid(UUID.randomUUID());
        s201Dataset.setGeometry(this.getGeometryFactory().createPoint(new Coordinate(52.98, 2.28)));
        s201Dataset.setLastUpdatedAt(LocalDateTime.now());
        s201Dataset.setCancelled(false);

        // Put some content in the dataset
        final DatasetContent datasetContent = new DatasetContent();
        datasetContent.setContent(s201Data);
        s201Dataset.setDatasetContent(datasetContent);

        // Mock the service responses
        doReturn(new PageImpl<>(Collections.singletonList(s201Dataset), Pageable.ofSize(1), 1))
                .when(this.getDatasetService())
                .findAll(any(), any(), any(), any(), any(), any());

        // Mock the cKeeper client behaviour
        final SignatureCertificateDto signatureCertificateDto = new SignatureCertificateDto();
        signatureCertificateDto.setCertificateId(BigInteger.ONE);
        signatureCertificateDto.setCertificate(minPemData);
        signatureCertificateDto.setPublicKey("publicKey");
        signatureCertificateDto.setRootCertificate(minPemData);
        doReturn(signatureCertificateDto)
                .when(this.getCKeeperClient())
                .getSignatureCertificate(any(), any(), any(), any());
        final Response cKeeperResponse = mock(Response.class);
        final Response.Body cKeeperReponseBody = mock(Response.Body.class);
        doReturn(cKeeperReponseBody)
                .when(cKeeperResponse)
                .body();
        doReturn(new ByteArrayInputStream("signature".getBytes()))
                .when(cKeeperReponseBody)
                .asInputStream();
        doReturn(cKeeperResponse)
                .when(this.getCKeeperClient())
                .generateCertificateSignature(any(), any(), any());

        // And proceed with the testing
        System.out.println("Service now checking the get summary interface with " + data);
    }

}
