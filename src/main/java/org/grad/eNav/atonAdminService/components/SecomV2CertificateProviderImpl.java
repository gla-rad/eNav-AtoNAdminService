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

package org.grad.eNav.atonAdminService.components;

import lombok.extern.slf4j.Slf4j;
import org.grad.eNav.atonAdminService.feign.CKeeperClient;
import org.grad.eNav.atonAdminService.models.domain.s100.ServiceInformationConfig;
import org.grad.eNav.atonAdminService.models.dtos.McpEntityType;
import org.grad.eNav.atonAdminService.models.dtos.SignatureCertificateDto;
import org.grad.secomv2.core.base.DigitalSignatureCertificate;
import org.grad.secomv2.core.base.SecomCertificateProvider;
import org.grad.secomv2.core.utils.SecomPemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.security.cert.CertificateException;

/**
 * The SECOM v2.0 Certificate Provider Implementation.
 *
 * In the current e-Navigation Service Architecture, it's the cKeeper
 * microservice that is responsible for storing and providing the certificates
 * to be used for signing and verifying the messages.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Component
@Slf4j
public class SecomV2CertificateProviderImpl implements SecomCertificateProvider {

    /**
     * The Service Information Config.
     */
    @Autowired
    ServiceInformationConfig serviceInformationConfig;

    /**
     * The cKeeper Feign Client.
     */
    @Autowired
    @Lazy
    CKeeperClient cKeeperClient;

    /**
     * This function overrides the interface definition to link the SECOM
     * certificate provision with the cKeeper operation. Each service can
     * request its most recent certificate, alongside some additional
     * information such as its ID, public key and root certificate thumbprint.
     *
     * @return the certificate information provided by cKeeper
     */
    @Override
    public DigitalSignatureCertificate getDigitalSignatureCertificate() {
        // Initialise SECOM the digital signature certificate
        final DigitalSignatureCertificate digitalSignatureCertificate = new DigitalSignatureCertificate();

        // Get the signature certificate from cKeeper
        final SignatureCertificateDto response = this.cKeeperClient.getSignatureCertificate(
                this.serviceInformationConfig.name(),
                this.serviceInformationConfig.version(),
                null,
                McpEntityType.SERVICE.getValue());

        // Build the SECOM digital certificate object
        try {
            digitalSignatureCertificate.setCertificateAlias(new String[]{String.format("%d", response.getCertificateId())});
            digitalSignatureCertificate.setCertificate(SecomPemUtils.getCertsFromPem(new String[]{response.getCertificate()}));
            digitalSignatureCertificate.setPublicKey(digitalSignatureCertificate.getCertificate()[0].getPublicKey());
            digitalSignatureCertificate.setRootCertificate(SecomPemUtils.getCertFromPem(response.getRootCertificate()));
        } catch (CertificateException ex) {
            log.error(ex.getMessage());
            return null;
        }

        // Return the output
        return digitalSignatureCertificate;
    }

}
