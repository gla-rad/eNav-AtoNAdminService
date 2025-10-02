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
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.util.common.SearchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * The HibernateSearchInit Component Class
 *
 * This component initialises the Lucence search indexes for the database. This
 * is a persistent content that will remain available through the whole
 * application.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Component()
@Slf4j
public class HibernateSearchInit implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * The Entity Manager.
     */
    @PersistenceContext
    EntityManager entityManager;

    /**
     * The maximum retries to index the database.
     */
    @Value("${gla.rad.aton-service.indexing.max-retries:3}")
    int indexingMaxRetries;

    /**
     * The retry back off tim ein millis to index the database.
     */
    @Value("${gla.rad.aton-service.indexing.back-off:300}")
    int indexingBackOffMillis;

    /**
     * Override the application event handler to index the database.
     *
     * @param event the context refreshed event
     */
    @Override
    @Transactional
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        // Add some retries in case this failed - mainly for K8s
        int attempt = 0;

        // Try multiple times to index if it fails
        while (attempt < indexingMaxRetries) {
            // And perform the indexing
            try {
                //Count the attempt
                attempt++;

                // Once the application has booted up, access the search session
                SearchSession searchSession = Search.session(entityManager);

                // Create a mass indexer
                MassIndexer indexer = searchSession.massIndexer(Arrays.asList(
                                S201Dataset.class,
                                S201DatasetIdentification.class,
                                DatasetContent.class,
                                AidsToNavigation.class,
                                SubscriptionRequest.class,
                                DatasetContentLog.class))
                        .threadsToLoadObjects(7);


                // Start the indexer
                indexer.startAndWait();
                log.info("Hibernate Search indexing completed successfully");
                return;
            } catch (IllegalStateException | InterruptedException | SearchException ex) {
                log.error("Indexing attempt {} failed: {}", attempt, ex.getMessage(), ex);

                if (attempt >= indexingMaxRetries) {
                    log.error("All {} indexing attempts failed, giving up...", indexingMaxRetries);
                    break;
                }

                try {
                    log.info("Retrying in {} ms...", indexingBackOffMillis);
                    Thread.sleep(indexingBackOffMillis);
                } catch (InterruptedException iex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

}
