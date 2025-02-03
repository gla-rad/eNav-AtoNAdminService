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

package org.grad.eNav.atonAdminService.models.dtos;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * The Signature Certificate DTO.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class SignatureCertificateDto implements Serializable {

    // Class Variables
    private BigInteger certificateId;
    private String certificate;
    private String publicKey;
    private String rootCertificate;

    /**
     * Gets certificate id.
     *
     * @return the certificate id
     */
    public BigInteger getCertificateId() {
        return certificateId;
    }

    /**
     * Sets certificate id.
     *
     * @param certificateId the certificate id
     */
    public void setCertificateId(BigInteger certificateId) {
        this.certificateId = certificateId;
    }

    /**
     * Gets certificate.
     *
     * @return the certificate
     */
    public String getCertificate() {
        return certificate;
    }

    /**
     * Sets certificate.
     *
     * @param certificate the certificate
     */
    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    /**
     * Gets public key.
     *
     * @return the public key
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * Sets public key.
     *
     * @param publicKey the public key
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * Gets root certificate.
     *
     * @return the root certificate
     */
    public String getRootCertificate() {
        return rootCertificate;
    }

    /**
     * Sets root certificate.
     *
     * @param rootCertificate the root certificate
     */
    public void setRootCertificate(String rootCertificate) {
        this.rootCertificate = rootCertificate;
    }

}
