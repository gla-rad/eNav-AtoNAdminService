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
import org.grad.secomv2.core.base.SecomTrustStoreProvider;
import org.grad.secomv2.core.utils.KeyStoreUtils;
import org.grad.secomv2.springboot3.components.SecomConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * The SECOM v2.0 Trust Store Provider Implementation.
 *
 * This class contains the implementation of the SECOM v2.0 trust store provider.
 * This is required for the SECOM library to be able to automatically pick up
 * the keystore that contains the trusted SECOM CA certificate chain.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Component
@Slf4j
public class SecomV2TrustStoreProviderImpl implements SecomTrustStoreProvider {

    /**
     * The X.509 Root Certificate Alias.
     */
    @Value("${gla.rad.service.secom.rootCertificateAlias:rootCertificate}")
    String rootCertificateAlias;

    /**
     * The SECOM Configuration properties.
     */
    @Autowired
    SecomConfigProperties secomConfigProperties;

    /**
     * Returns the alias of the root certificate as it is contained in the
     * provided trust store. This can be used to pinpoint the actual root
     * certificate entry.
     *
     * @return the alias of the root certificate
     */
    @Override
    public String getCARootCertificateAlias() {
        return this.rootCertificateAlias;
    }

    /**
     * Returns the trust store that contains the trusted SECOM certificate chain.
     * This is required to validate the received certificate for every applicable
     * request.
     *
     * @return the SECOM trust store
     */
    @Override
    public KeyStore getTrustStore() {
        try {
            return KeyStoreUtils.getKeyStore(this.secomConfigProperties.getTruststore(), this.secomConfigProperties.getTruststorePassword(), this.secomConfigProperties.getTruststoreType());
        } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException ex) {
            return null;
        }
    }

}
