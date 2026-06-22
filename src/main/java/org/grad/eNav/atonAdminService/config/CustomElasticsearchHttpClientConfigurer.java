/*
 * Copyright (c) 2026 GLA Research and Development Directorate
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

package org.grad.eNav.atonAdminService.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.hibernate.search.backend.elasticsearch.client.rest4.ElasticsearchHttpClientConfigurationContext;
import org.hibernate.search.backend.elasticsearch.client.rest4.ElasticsearchHttpClientConfigurer;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * The CustomElasticsearchHttpClientConfigurer Class
 *
 * This class allows the service to customise the ElasticSearch HTTP client
 * and connect to any type of Elasticsearch provider, even theh insecureones.
 * <p/>
 * This is obviously not the suggested approach, but this configurer needs
 * to be manually enabled through the application properties so it should not
 * create a problem for production installations. It wil just not be called.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Slf4j
public class CustomElasticsearchHttpClientConfigurer implements ElasticsearchHttpClientConfigurer {

    /**
     * Configuring the Elasticsearch SSL context so that it can accept the self
     * signed SSL certificate provided by the quick-start elastic search setup.
     * <p/>
     * In those cases we also need to disable the hostname verified. This is
     * probably not the way to implement a production-level system, but until
     * we use a cloud-hosted elastic-search it will do the trick.
     *
     * @param context A configuration context giving access to the Apache HTTP client builder
     * and configuration properties in particular.
     */
    @Override
    public void configure(ElasticsearchHttpClientConfigurationContext context) {
        try {
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy()) // or TrustAllStrategy.INSTANCE
                    .build();
            context.clientBuilder()
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException ex) {
            log.error(ex.getMessage());
        }
    }

}
