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

import org.hibernate.search.backend.elasticsearch.index.layout.IndexLayoutStrategy;
import org.hibernate.search.util.common.SearchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The PrefixedIndexLayoutStrategy Class
 *
 * A Hibernate Search {@link IndexLayoutStrategy} that prepends a configurable,
 * service-wide prefix to every Elasticsearch index name and its read/write
 * aliases. This provides a single, global way to namespace the indexes of this
 * service (e.g. to share an Elasticsearch cluster between multiple services or
 * environments) without having to set the index name on each {@code @Indexed}
 * entity individually.
 * <p/>
 * The prefix is read from the {@code gla.rad.aton-service.search.index-prefix}
 * property and defaults to an empty string, in which case the behaviour is
 * identical to Hibernate Search's default {@code simple} layout strategy.
 * <p/>
 * This component is resolved by Hibernate Search as a Spring bean (the
 * {@code SpringBeanContainer} registered by spring-boot-starter-data-jpa), so
 * it must be referenced via a bean reference in the configuration, e.g.:
 * <pre>
 * spring.jpa.properties.hibernate.search.backend.layout.strategy=bean:prefixedIndexLayoutStrategy
 * </pre>
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Component
public class PrefixedIndexLayoutStrategy implements IndexLayoutStrategy {

    /**
     * The pattern used to strip the {@code -<6 digits>} rollover suffix off a
     * concrete Elasticsearch index name when extracting its unique key.
     */
    private static final Pattern UNIQUE_KEY_EXTRACTION_PATTERN = Pattern.compile("(.*)-\\d{6}");

    /**
     * The service-wide prefix applied to every index name and alias. Defaults
     * to an empty string so that, when unset, the layout matches the default
     * Hibernate Search {@code simple} strategy.
     */
    @Value("${gla.rad.aton-service.search.index-prefix:aton-admin-service-}")
    String indexPrefix;

    /**
     * Builds the name of the concrete, write-targeted Elasticsearch index that
     * backs the given Hibernate Search index.
     *
     * @param hibernateSearchIndexName the Hibernate Search index name
     * @return the prefixed initial Elasticsearch index name
     */
    @Override
    public String createInitialElasticsearchIndexName(String hibernateSearchIndexName) {
        return this.indexPrefix + hibernateSearchIndexName + "-000001";
    }

    /**
     * Builds the write alias pointing to the active index for the given
     * Hibernate Search index.
     *
     * @param hibernateSearchIndexName the Hibernate Search index name
     * @return the prefixed write alias
     */
    @Override
    public String createWriteAlias(String hibernateSearchIndexName) {
        return this.indexPrefix + hibernateSearchIndexName + "-write";
    }

    /**
     * Builds the read alias pointing to the active index for the given
     * Hibernate Search index.
     *
     * @param hibernateSearchIndexName the Hibernate Search index name
     * @return the prefixed read alias
     */
    @Override
    public String createReadAlias(String hibernateSearchIndexName) {
        return this.indexPrefix + hibernateSearchIndexName + "-read";
    }

    /**
     * Extracts the unique key identifying an index from its Hibernate Search
     * name. The prefix is included so that the key is consistent with the one
     * derived from the concrete Elasticsearch index name.
     * <p/>
     * Only used under the {@code index-name} type-name mapping strategy.
     *
     * @param hibernateSearchIndexName the Hibernate Search index name
     * @return the prefixed unique key for this index
     */
    @Override
    public String extractUniqueKeyFromHibernateSearchIndexName(String hibernateSearchIndexName) {
        return this.indexPrefix + hibernateSearchIndexName;
    }

    /**
     * Extracts the unique key identifying an index from its concrete
     * Elasticsearch name by stripping the {@code -<6 digits>} rollover suffix.
     * The retained prefix keeps this consistent with the key derived from the
     * Hibernate Search name.
     * <p/>
     * Only used under the {@code index-name} type-name mapping strategy.
     *
     * @param elasticsearchIndexName a primary index name from an Elasticsearch response
     * @return the prefixed unique key for this index
     */
    @Override
    public String extractUniqueKeyFromElasticsearchIndexName(String elasticsearchIndexName) {
        final Matcher matcher = UNIQUE_KEY_EXTRACTION_PATTERN.matcher(elasticsearchIndexName);
        if (!matcher.matches()) {
            throw new SearchException("Unrecognized Elasticsearch index name: " + elasticsearchIndexName);
        }
        return matcher.group(1);
    }

}