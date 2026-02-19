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

package org.grad.eNav.atonAdminService.services.secom.v2;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.grad.secomv2.core.exceptions.SecomNotFoundException;
import org.grad.secomv2.core.exceptions.SecomValidationException;
import org.grad.secomv2.core.models.*;
import org.grad.secomv2.springboot4.components.SecomClient;
import org.grad.secomv2.springboot4.components.SecomConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.*;

/**
 * The SECOM v2 Service Class.
 *
 * A service to handle the general SECOM v2 requests that need additional
 * processing, not covered by the existing services, e.g signing.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Service
@Slf4j
public class SecomV2Service {

    /**
     * The Service Registry URL.
     */
    @Value("${secom.service-registry.url:}" )
    String discoveryServiceUrl;

    /**
     * The SECOM v2 Configuration Properties.
     */
    @Autowired
    SecomConfigProperties secomConfigProperties;

    // Class Variables
    SecomClient discoveryService;

    /**
     * The service post-construct operations where the handler auto-registers
     * it-self to the S-201 publication channel.
     */
    @PostConstruct
    public void init() {
        log.info("SECOM v2 Service is booting up...");
        this.discoveryService = Optional.ofNullable(this.discoveryServiceUrl)
                .filter(StringUtils::isNotBlank)
                .map(url -> {
                    try {
                        return new URL(url);
                    } catch (MalformedURLException ex) {
                        log.error("Invalid SECOM v2 discovery service URL provided...", ex);
                        return null;
                    }
                })
                .map(url -> {
                    try {
                        return new SecomClient(url, this.secomConfigProperties);
                    } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException ex) {
                        log.error("Unable to initialise the SSL context for the SECOM v2 discovery service...", ex);
                        return null;
                    }
                })
                .orElse(null);
    }

    /**
     * When shutting down the application we need to make sure that all
     * threads have been gracefully shutdown as well.
     */
    @PreDestroy
    public void destroy() {
        log.info("SECOM v2 Service is shutting down...");
        this.discoveryService = null;
    }

    /**
     * Based on an MRN provided, this function will contact the SECOM discovery
     * service (in this case it's the MCP MSR) and request the client endpoint
     * URI. It will then construct a SECOM client to be returned for the URI
     * discovered.
     *
     * @param mrn the MRN to be lookup up
     * @return the SECOM client for the endpoint matching the provided URI
     */
    public SecomClient getClient(String mrn) {
        // Validate the MRN
        Optional.ofNullable(mrn)
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new SecomValidationException("Cannot request a service discovery for an empty/invalid MRN"));

        // Make sure the service registry is available
        Optional.ofNullable(this.discoveryService)
                .filter(Objects::nonNull)
                .orElseThrow(() -> new SecomValidationException("Subscription request found for S-201 dataset updates but no connection to service registry"));

        // Create the discovery service search filter object for the provided MRN
        final SearchFilterObject searchFilterObject = new SearchFilterObject();
        final EnvelopeSearchFilterObject envelopeSearchFilterObject = new EnvelopeSearchFilterObject();
        final SearchParameters searchParameters = new SearchParameters();
        searchParameters.setInstanceId(mrn);
        envelopeSearchFilterObject.setQuery(searchParameters);
        searchFilterObject.setEnvelope(envelopeSearchFilterObject);

        // Lookup the endpoints of the clients from the SECOM discovery service
        final List<ServiceInstanceObject> instances = Optional.ofNullable(this.discoveryService)
                .flatMap(ds -> ds.searchService(searchFilterObject, 0, Integer.MAX_VALUE))
                .map(ResponseSearchObject::getSearchServiceResult)
                .orElse(Collections.emptyList());

        // Extract the latest matching instance
        final ServiceInstanceObject instance = instances.stream()
                .max(Comparator.comparing(ServiceInstanceObject::getVersion))
                .orElseThrow(() -> new SecomNotFoundException(mrn));

        // Now construct and return a SECOM client for the discovered URI
        try {
            return new SecomClient(new URL(instance.getEndpointUri()), this.secomConfigProperties);
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException ex) {
            log.error(ex.getMessage(), ex);
            throw new SecomValidationException(ex.getMessage());
        }
    }

    /**
     * This function allows the definition of SECOM clients without accessing
     * the SECOM Service Registry. By simply providing the callback URL of the
     * client, along with the standard SECOM configuration properties, we can
     * construct a valid SECOM client.
     *
     * @param url the URL of the SECOM client
     * @return the constucted SECOM client for the provided URL
     */
    public SecomClient getClient(@NotNull URL url) {
        // Validate the MRN
        Optional.ofNullable(url)
                .orElseThrow(() -> new SecomValidationException("Cannot request SECOM v2 client for an empty/invalid URL"));

        // Now construct and return a SECOM client for the discovered URI
        try {
            return new SecomClient(url, this.secomConfigProperties);
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException ex) {
            log.error(ex.getMessage(), ex);
            throw new SecomValidationException(ex.getMessage());
        }
    }

}
