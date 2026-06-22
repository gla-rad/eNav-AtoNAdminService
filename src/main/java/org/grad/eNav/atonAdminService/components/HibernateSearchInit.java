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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.grad.eNav.atonAdminService.models.domain.DatasetContent;
import org.grad.eNav.atonAdminService.models.domain.DatasetContentLog;
import org.grad.eNav.atonAdminService.models.domain.s201.AidsToNavigation;
import org.grad.eNav.atonAdminService.models.domain.s201.S201Dataset;
import org.grad.eNav.atonAdminService.models.domain.s201.S201DatasetIdentification;
import org.grad.eNav.atonAdminService.models.domain.secom.SubscriptionRequest;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.util.common.SearchException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * The HibernateSearchInit Component Class
 *
 * This component initialises the Elasticsearch search indexes for the database.
 * This is a persistent content that will remain available through the whole
 * application.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Profile("!test")
@Component()
@Slf4j
public class HibernateSearchInit implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * The Entity Manager.
     */
    @PersistenceContext
    EntityManager em;

    /**
     * The list of entity classes managed by Hibernate Search that need to be
     * indexed into Elasticsearch.
     */
    private static final List<Class<?>> INDEXED_ENTITIES = Arrays.asList(
            S201Dataset.class,
            S201DatasetIdentification.class,
            DatasetContent.class,
            AidsToNavigation.class,
            SubscriptionRequest.class,
            DatasetContentLog.class);

    /**
     * Override the application event handler to index the database.
     *
     * @param event the context refreshed event
     */
    @Override
    @Async
    @Transactional(readOnly = true)
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        // Skip the (potentially expensive) mass indexing if the indexes have
        // already been populated in a previous run.
        if (this.isIndexInitialised()) {
            log.info("Hibernate Search indexes already initialised - skipping mass indexing");
            return;
        }

        // Log the indexing process commencing
        log.info("Hibernate Search indexing commencing...");

        // Perform the indexing
        runMassIndex();
    }

    /**
     * Determines whether the Elasticsearch indexes have already been
     * initialised, i.e. they exist and contain at least one document. If the
     * indexes are missing (e.g. on a fresh deployment) a {@link SearchException}
     * is thrown by the backend, which we treat as "not initialised".
     *
     * @return whether the search indexes are already populated
     */
    private boolean isIndexInitialised() {
        try {
            return Search.session(em)
                    .search(INDEXED_ENTITIES)
                    .where(f -> f.matchAll())
                    .fetchTotalHitCount() > 0;
        } catch (SearchException ex) {
            // The indexes do not exist yet, so they are not initialised
            log.debug("Could not determine search index state, assuming not initialised", ex);
            return false;
        }
    }

    /**
     * Running the actual indexing operation asynchronously to
     * allow the service to continue.
     */
    private void runMassIndex() {
        try {
            Search.session(em)
                    .massIndexer(INDEXED_ENTITIES)
                    .threadsToLoadObjects(7)
                    .startAndWait();
        } catch (InterruptedException ex) {
            // The indexing process got interrupted... nothing we can do so just log
            log.error("Could not start the Hibernate Search mass indexer", ex);
        }

        // And log the result
        log.info("Hibernate Search indexing completed successfully");
    }

}

