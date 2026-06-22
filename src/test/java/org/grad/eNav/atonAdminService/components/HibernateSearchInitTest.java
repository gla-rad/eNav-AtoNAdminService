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

import org.hibernate.search.engine.search.query.SearchFetchable;
import org.hibernate.search.engine.search.query.dsl.SearchQueryOptionsStep;
import org.hibernate.search.engine.search.query.dsl.SearchQuerySelectStep;
import org.hibernate.search.engine.search.query.dsl.SearchQueryWhereStep;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.util.common.SearchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    EntityManager entityManager;

    // Test Variables
    private SearchSession searchSession;
    private MassIndexer massIndexer;
    private SearchQuerySelectStep<?,?,?,?,?,?,?> searchQuerySelectStep;
    private SearchQueryOptionsStep<?,?,?,?,?,?> searchQueryOptionsStep;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setup() {
        this.searchSession = mock(SearchSession.class);
        this.massIndexer = mock(MassIndexer.class);
        this.searchQuerySelectStep = mock(SearchQuerySelectStep.class);
        this.searchQueryOptionsStep = mock(SearchQueryOptionsStep.class);
    }

    /**
     * Test that the hibernate search will initialise correctly on the
     * application events.
     */
    @Test
    void testOnApplicationEvent() throws InterruptedException {
        try (MockedStatic<Search> mockedSearch = Mockito.mockStatic(Search.class)) {
            mockedSearch.when(() -> Search.session(this.entityManager)).thenReturn(this.searchSession);

            doReturn(this.searchQuerySelectStep).when(this.searchSession).search(anyCollection());
            doReturn(this.searchQueryOptionsStep).when(this.searchQuerySelectStep).where(any(Function.class));
            doReturn(0L).when(this.searchQueryOptionsStep).fetchTotalHitCount();
            doReturn(this.massIndexer).when(this.searchSession).massIndexer(anyCollection());
            doReturn(this.massIndexer).when(this.massIndexer).threadsToLoadObjects(anyInt());
            doNothing().when(this.massIndexer).startAndWait();

            // Perform the component call
            this.hibernateSearchInit.onApplicationEvent(mock(ApplicationReadyEvent.class));
        }

        // Verify the indexing initialisation was performed
        verify(this.massIndexer, times(1)).startAndWait();
    }

    /**
     * Test that when the hibernate search will indicate that the indexes have
     * already been initialised, the hibernate search mass indexing will not
     * kick in.
     */
    @Test
    void testOnApplicationEventAlreadyIndexed() throws InterruptedException {
        try (MockedStatic<Search> mockedSearch = Mockito.mockStatic(Search.class)) {
            mockedSearch.when(() -> Search.session(this.entityManager)).thenReturn(this.searchSession);

            doReturn(this.searchQuerySelectStep).when(this.searchSession).search(anyCollection());
            doReturn(this.searchQueryOptionsStep).when(this.searchQuerySelectStep).where(any(Function.class));
            doReturn(1L).when(this.searchQueryOptionsStep).fetchTotalHitCount();

            // Perform the component call
            this.hibernateSearchInit.onApplicationEvent(mock(ApplicationReadyEvent.class));
        }

        // Verify the indexing initialisation was performed even if it failed
        verify(this.massIndexer, times(0)).startAndWait();
    }

    /**
     * Test that when the hibernate search will fail to initialise we can
     * still boot the service without an error.
     */
    @Test
    void testOnApplicationEventFailed() throws InterruptedException {
        try (MockedStatic<Search> mockedSearch = Mockito.mockStatic(Search.class)) {
            mockedSearch.when(() -> Search.session(this.entityManager)).thenReturn(this.searchSession);

            doReturn(this.searchQuerySelectStep).when(this.searchSession).search(anyCollection());
            doReturn(this.searchQueryOptionsStep).when(this.searchQuerySelectStep).where(any(Function.class));
            doReturn(0L).when(this.searchQueryOptionsStep).fetchTotalHitCount();
            doReturn(this.massIndexer).when(this.searchSession).massIndexer(anyCollection());
            doReturn(this.massIndexer).when(this.massIndexer).threadsToLoadObjects(anyInt());
            doThrow(InterruptedException.class).when(this.massIndexer).startAndWait();

            // Perform the component call
            this.hibernateSearchInit.onApplicationEvent(mock(ApplicationReadyEvent.class));
        }

        // Verify the indexing initialisation was performed even if it failed
        verify(this.massIndexer, times(1)).startAndWait();
    }

}