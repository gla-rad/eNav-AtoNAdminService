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

package org.grad.eNav.atonAdminService.utils;

import org.grad.eNav.atonAdminService.models.dtos.datatables.DtSortField;
import org.hibernate.search.engine.search.sort.dsl.CompositeSortComponentsStep;
import org.hibernate.search.engine.search.sort.dsl.SearchSortFactory;
import org.hibernate.search.engine.search.sort.dsl.SortFinalStep;

import java.util.Collection;

/**
 * The Search Sort Utils Class.
 *
 * This utility class builds a Hibernate Search portable sort definition out of
 * a list of {@link DtSortField} entries. The portable sort DSL is supported by
 * all the Hibernate Search backends, including the Elasticsearch one, replacing
 * the previous Lucene-specific {@code fromLuceneSort} sorting.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class SearchSortUtils {

    /**
     * Builds the Hibernate Search sort definition for the provided sort fields,
     * using the portable sort DSL. When no sort fields are provided, the results
     * are sorted by their relevance score, which effectively leaves them in their
     * default order.
     *
     * @param factory    the Hibernate Search sort factory
     * @param sortFields the list of sort fields to apply
     * @return the resulting sort definition
     */
    public static SortFinalStep buildSort(SearchSortFactory factory, Collection<DtSortField> sortFields) {
        if (sortFields == null || sortFields.isEmpty()) {
            return factory.score();
        }
        final CompositeSortComponentsStep<?, ?> composite = factory.composite();
        for (DtSortField sortField : sortFields) {
            composite.add(sortField.descending()
                    ? factory.field(sortField.fieldName()).desc()
                    : factory.field(sortField.fieldName()).asc());
        }
        return composite;
    }

}