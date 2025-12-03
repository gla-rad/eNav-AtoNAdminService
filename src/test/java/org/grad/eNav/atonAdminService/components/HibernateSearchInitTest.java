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

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HibernateSearchInitTest {

    /**
     * The Tested Component.
     */
    @InjectMocks
    @Spy
    HibernateSearchInit hibernateSearchInit;

    /**
     * The Entity Manager mock.
     */
    @Mock
    EntityManagerFactory entityManagerFactory;

    // Test Variables
    private EntityManager entityManager;
    private SearchSession searchSession;
    private MassIndexer massIndexer;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setup() {
        this.hibernateSearchInit.indexingMaxRetries = 3;
        this.hibernateSearchInit.indexingBackOffMillis = 100;
        this.entityManager = mock(EntityManager.class);
        this.searchSession = mock(SearchSession.class);
        this.massIndexer = mock(MassIndexer.class);
    }

    /**
     * Test that the indexing task can be called successfully and perform the
     * mass indexer operation.
     */
    @Test
    void testIndexingTask() throws Exception {
        try (MockedStatic<Search> mockedSearch = Mockito.mockStatic(Search.class)) {
            doReturn(this.entityManager).when(this.entityManagerFactory).createEntityManager();
            mockedSearch.when(() -> Search.session(this.entityManager)).thenReturn(this.searchSession);

            doReturn(this.massIndexer).when(this.searchSession).massIndexer(anyCollection());
            doReturn(this.massIndexer).when(this.massIndexer).threadsToLoadObjects(anyInt());
            doNothing().when(this.massIndexer).startAndWait();

            // Perform the indexing task
            assertEquals(Boolean.TRUE, this.hibernateSearchInit.indexingTask.call());
        }

        // Verify the indexing initialisation was performed
        verify(this.massIndexer, times(1)).startAndWait();
    }

    /**
     * Test that if the indexing task fails, it will return an un-successful
     * result.
     */
    @Test
    void tesIndexingTaskFailed() throws Exception {
        try (MockedStatic<Search> mockedSearch = Mockito.mockStatic(Search.class)) {
            doReturn(this.entityManager).when(this.entityManagerFactory).createEntityManager();
            mockedSearch.when(() -> Search.session(this.entityManager)).thenReturn(this.searchSession);

            doReturn(this.massIndexer).when(this.searchSession).massIndexer(anyCollection());
            doReturn(this.massIndexer).when(this.massIndexer).threadsToLoadObjects(anyInt());
            doThrow(InterruptedException.class).when(this.massIndexer).startAndWait();

            // Perform the indexing task
            assertEquals(Boolean.FALSE, this.hibernateSearchInit.indexingTask.call());
        }

        // Verify the indexing initialisation was performed
        verify(massIndexer, times(1)).startAndWait();
    }

    /**
     * Test that on an application event the hibernate search init component
     * will attempt to run the indexing task as a future callable. This should
     * only take one if successful.
     */
    @Test
    void testOnApplicationEvent() throws Exception {
        // Set a mock indexing task to verify the operation
        Callable<Boolean> indexingTaskMock = mock(Callable.class);
        doReturn(Boolean.TRUE).when(indexingTaskMock).call();
        this.hibernateSearchInit.indexingTask = indexingTaskMock;

        // Perform the component call
        this.hibernateSearchInit.onApplicationEvent(mock(ApplicationReadyEvent.class));

        verify(indexingTaskMock, times(1)).call();
    }

    /**
     * Test that on an application event the hibernate search init component
     * will attempt to run the indexing task as a future callable. This should
     * only take a number of times (e.g. 3) if not successful./
     */
    @Test
    void testOnApplicationEventFailed() throws Exception {
        // Set a mock indexing task to verify the operation
        Callable<Boolean> indexingTaskMock = mock(Callable.class);
        doReturn(Boolean.FALSE).when(indexingTaskMock).call();
        this.hibernateSearchInit.indexingTask = indexingTaskMock;

        // Perform the component call
        this.hibernateSearchInit.onApplicationEvent(mock(ApplicationReadyEvent.class));

        verify(indexingTaskMock, times(3)).call();
    }

}