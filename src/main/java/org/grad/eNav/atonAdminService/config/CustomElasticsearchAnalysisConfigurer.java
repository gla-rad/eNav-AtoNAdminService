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
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurationContext;
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurer;

/**
 * The Custom Elasticsearch Analysis Configurer
 *
 * Defines the custom Elasticsearch analysis configuration used for this service,
 * by adding certain analysers and normalisers. This is the Elasticsearch backend
 * equivalent of the previous Lucene analysis configuration, using the built-in
 * Elasticsearch component names instead of the Lucene factory classes.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Slf4j
public class CustomElasticsearchAnalysisConfigurer implements ElasticsearchAnalysisConfigurer {

    /**
     * Implements the Elasticsearch analysis configuration.
     *
     * @param context the Elasticsearch analysis configuration context
     */
    @Override
    public void configure(ElasticsearchAnalysisConfigurationContext context) {
        context.analyzer( "english" ).custom()
                .tokenizer( "standard" )
                .charFilters( "html_strip" )
                .tokenFilters( "lowercase", "snowball_english", "asciifolding" );

        context.tokenFilter( "snowball_english" )
                .type( "snowball" )
                .param( "language", "English" );

        context.normalizer( "lowercase" ).custom()
                .tokenFilters( "lowercase", "asciifolding" );
    }

}