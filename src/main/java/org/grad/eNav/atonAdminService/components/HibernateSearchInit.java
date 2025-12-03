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
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
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
import java.util.concurrent.*;

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

    @PersistenceUnit
    EntityManagerFactory emf;

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
     * The Indexing Task - To be run in a separate thread
     */
    Callable<Boolean> indexingTask = () -> {
        // Start the indexer
        try {
            log.debug("Trying to index in {} ms...", indexingBackOffMillis);

            // Allow some waiting time to make sure the database connection is up
            Thread.sleep(this.indexingBackOffMillis);

            // Once the application has booted up, access the search session
            EntityManager em = this.emf.createEntityManager();
            SearchSession searchSession = Search.session(em);

            // Create a mass indexer
            MassIndexer indexer = searchSession.massIndexer(Arrays.asList(
                            S201Dataset.class,
                            S201DatasetIdentification.class,
                            DatasetContent.class,
                            AidsToNavigation.class,
                            SubscriptionRequest.class,
                            DatasetContentLog.class))
                    .threadsToLoadObjects(7);

            indexer.startAndWait();
        } catch (InterruptedException ex) {
            log.error(ex.getMessage(), ex);
            return Boolean.FALSE;
        }

        // Log the success and return
        log.info("Hibernate Search indexing completed successfully");
        return Boolean.TRUE;
    };

    /**
     * Override the application event handler to index the database.
     *
     * @param event the context refreshed event
     */
    @Override
    @Transactional
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        Boolean indexed = Boolean.FALSE;

        // Add some retries in case this failed - mainly for K8s
        int attempt = 0;

        // Try multiple times to index if it fails
        while (!indexed && attempt < indexingMaxRetries) {
            // Create an executor service with virtual threads
            try(ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                // Run the indexing operation in a separate thread
                Future<?> future = executor.submit(this.indexingTask);

                // And wait for the result
                indexed = (Boolean) future.get();

                //Count the attempt
                attempt++;
            } catch (Exception ex) {
                log.error("Indexing attempt {} failed: {}", attempt, ex.getMessage(), ex);
            }
        }
    }
}
