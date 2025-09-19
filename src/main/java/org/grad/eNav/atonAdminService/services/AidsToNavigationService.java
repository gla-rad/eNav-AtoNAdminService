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

package org.grad.eNav.atonAdminService.services;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortedNumericSortField;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.grad.eNav.atonAdminService.exceptions.DataNotFoundException;
import org.grad.eNav.atonAdminService.models.domain.s201.*;
import org.grad.eNav.atonAdminService.models.dtos.datatables.DtPagingRequest;
import org.grad.eNav.atonAdminService.repos.AidsToNavigationRepo;
import org.hibernate.search.backend.lucene.LuceneExtension;
import org.hibernate.search.backend.lucene.search.sort.dsl.LuceneSearchSortFactory;
import org.hibernate.search.engine.search.query.SearchFetchable;
import org.hibernate.search.engine.search.query.SearchQuery;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.scope.SearchScope;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.spatial4j.context.jts.JtsSpatialContext;
import org.locationtech.spatial4j.shape.jts.JtsGeometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * The Aids to Navigation Service.
 *
 * Service Implementation for managing the S-201 Aids to Navigation objects.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Service
@Slf4j
public class AidsToNavigationService {

    /**
     * The Entity Manager.
     */
    @Autowired
    EntityManager entityManager;

    /**
     * The Aggregation Service.
     */
    @Autowired
    AggregationService aggregationService;

    /**
     * The Association Service.
     */
    @Autowired
    AssociationService associationService;

    /**
     * The Generic Aids to Navigation Repo.
     */
    @Autowired
    AidsToNavigationRepo aidsToNavigationRepo;

    // Service Variables
    private final String[] searchFields = new String[] {
            "id_code"
    };
    private final String[] searchFieldsWithSort = new String[] {
            "id"
    };

    /**
     * Get all the Aids to Navigation in a pageable search.
     *
     * @param idCode the Aids to Navigation ID Code
     * @param geometry the geometry to match the Aids to Navigation for
     * @param fromTime the time to match the Aids to Navigation from
     * @param toTime the time to match the Aids to Navigation to
     * @param pageable the pagination information
     * @return the list of Aids to Navigation
     */
    @Transactional(readOnly = true)
    public Page<AidsToNavigation> findAll(String idCode,
                                          Geometry geometry,
                                          LocalDateTime fromTime,
                                          LocalDateTime toTime,
                                          Pageable pageable) {
        log.debug("Request to get Aids to Navigation in a pageable search");
        // Create the search query - always sort by name
        SearchQuery searchQuery = this.getAidsToNavigationSearchQuery(
                idCode,
                geometry,
                fromTime,
                toTime,
                new Sort(new SortedNumericSortField("id_sort", SortField.Type.LONG, true))
        );

        // Map the results to a paged response
        return Optional.of(searchQuery)
                .map(query -> pageable.isPaged() ? query.fetch(pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize()) : query.fetchAll())
                .map(searchResult -> new PageImpl<AidsToNavigation>(searchResult.hits(), pageable, searchResult.total().hitCount()))
                .orElseGet(() -> new PageImpl<>(Collections.emptyList(), pageable, 0));
    }

    /**
     * Get the number of all the Aids to Navigation in the pageable search.
     *
     * @param idCode the Aids to Navigation ID Code
     * @param geometry the geometry to match the Aids to Navigation for
     * @param fromTime the time to match the Aids to Navigation from
     * @param toTime the time to match the Aids to Navigation to
     * @return the number of all matching Aids to Navigation
     */
    @Transactional(readOnly = true)
    public long findAllTotalCount(String idCode,
                                 Geometry geometry,
                                 LocalDateTime fromTime,
                                 LocalDateTime toTime) {
        log.debug("Request to get the total count of Aids to Navigation matching the pageable search");
        // Create the search query - always sort by name
        SearchQuery searchQuery = this.getAidsToNavigationSearchQuery(
                idCode,
                geometry,
                fromTime,
                toTime,
                new Sort(new SortedNumericSortField("id_sort", SortField.Type.LONG, true))
        );

        // Map the results to a paged response
        return Optional.of(searchQuery)
                .map(SearchFetchable::fetchTotalHitCount)
                .orElse(0L);
    }

    /**
     * Handles a datatables pagination request and returns the results list in
     * an appropriate format to be viewed by a datatables jQuery table.
     *
     * @param dtPagingRequest the Datatables pagination request
     * @return the Datatables paged response
     */
    @Transactional(readOnly = true)
    public Page<AidsToNavigation> handleDatatablesPagingRequest(DtPagingRequest dtPagingRequest) {
        log.debug("Request to get Aids to Navigation in a Datatables pageable search");
        // Create the search query
        SearchQuery searchQuery = this.getSearchAidsToNavigationQueryByText(
                dtPagingRequest.getSearch().getValue(),
                dtPagingRequest.getLucenceSort(Arrays.asList(searchFieldsWithSort))
        );

        // Map the results to a paged response
        return Optional.of(searchQuery)
                .map(query -> query.fetch(dtPagingRequest.getStart(), dtPagingRequest.getLength()))
                .map(searchResult -> new PageImpl<AidsToNavigation>(searchResult.hits(), dtPagingRequest.toPageRequest(), searchResult.total().hitCount()))
                .orElseGet(() -> new PageImpl<>(Collections.emptyList(), dtPagingRequest.toPageRequest(), 0));
    }

    /**
     * Returns the Aids to Navigation based on the provided ID Code is that
     * exists.
     *
     * @param idCode the ID Code
     * @return the Aids to Navigation if that exists
     */
    public Optional<AidsToNavigation> findByIdCode(String idCode) {
        return this.aidsToNavigationRepo.findByIdCode(idCode);
    }

    /**
     * A simple saving operation that persists the models in the database using
     * the correct repository based on the instance type.
     *
     * @param aidsToNavigation the Aids to Navigation entity to be saved
     * @return the saved Aids to Navigation entity
     */
    @Transactional
    public AidsToNavigation save(AidsToNavigation aidsToNavigation) {
        log.debug("Request to save Aid to Navigation : {}", aidsToNavigation);

        // Update the entity ID if the Code ID was found
        this.aidsToNavigationRepo.findByIdCode(aidsToNavigation.getIdCode())
                .ifPresent(aton -> {
                    // Re-use the object ID
                    aidsToNavigation.setId(aton.getId());
                    // Re-use the existing information IDs
                    final AtomicInteger informationCounter = new AtomicInteger();
                    final List<BigInteger> informationIds = aton.getInformations()
                            .stream().map(Information::getId)
                            .toList();
                    aidsToNavigation.getInformations()
                            .stream()
                            .filter(inf -> informationCounter.get() < informationIds.size())
                            .forEach(inf -> inf.setId(informationIds.get(informationCounter.getAndIncrement())));
                });

        // Now save for each type
        final AidsToNavigation saved = this.aidsToNavigationRepo.save(aidsToNavigation);

        // Update the associations and aggregations links
        saved.setPeerAtonAggregations(this.aggregationService.updateAidsToNavigationAggregations(saved.getIdCode(), aidsToNavigation.getPeerAtonAggregations()));
        saved.setPeerAtonAssociations(this.associationService.updateAidsToNavigationAssociations(saved.getIdCode(), aidsToNavigation.getPeerAtonAssociations()));

        // DO NOT REMOVE: Perform a log, which also handles lazy loading!
        log.debug(String.format("Saved Aid to Navigation %s with %d aggregations and %d associations.",
                saved.getIdCode(),
                saved.getPeerAtonAggregations().size(),
                saved.getPeerAtonAssociations().size()));

        // Return the saved entry
        return saved;
    }

    /**
     * Delete the Aids to Navigation by ID.
     *
     * @param id the ID of the Aids to Navigation
     */
    @Transactional
    public AidsToNavigation delete(BigInteger id) {
        log.debug("Request to delete Aids to Navigation with ID : {}", id);

        // Make sure the station node exists
        final AidsToNavigation aidsToNavigation = this.aidsToNavigationRepo.findById(id)
                .orElseThrow(() -> new DataNotFoundException(String.format("No Aid to Navigation found for the provided ID: %d", id)));

        // Update the associations and aggregations links and clean up
        aidsToNavigation.getPeerAtonAggregations().stream()
                .peek(aggr -> aggr.getAtonAggregationBies().remove(aidsToNavigation))
                .filter(aggr -> aggr.getAtonAggregationBies().isEmpty())
                .map(AtonAggregation::getId)
                .forEach(this.aggregationService::delete);
        aidsToNavigation.getPeerAtonAssociations().stream()
                .peek(asso -> asso.getAtonAssociationBies().remove(aidsToNavigation))
                .filter(asso -> asso.getAtonAssociationBies().isEmpty())
                .map(AtonAssociation::getId)
                .forEach(this.associationService::delete);

        // Now delete the aid to navigation
        this.aidsToNavigationRepo.delete(aidsToNavigation);

        // And return the object for AOP
        return aidsToNavigation;
    }

    /**
     * Delete the Aids to Navigation by its AtoN number.
     *
     * @param idCode the ID Code of the Aids to Navigation
     */
    @Transactional
    public AidsToNavigation deleteByIdCode(String idCode) throws DataNotFoundException {
        log.debug("Request to delete Aid to Navigation with AtoN number : {}", idCode);
        BigInteger id = this.aidsToNavigationRepo.findByIdCode(idCode)
                .map(AidsToNavigation::getId)
                .orElseThrow(() ->
                        new DataNotFoundException(String.format("No Aid to Navigation found for the provided AtoN ID Code: %s", idCode))
                );
        return this.delete(id);
    }

    /**
     * Constructs a hibernate search query using Lucene based on the provided
     * search test. This query will be based solely on the station nodes table
     * and will include the following fields:
     * - UID
     * - Type
     * - Message
     *
     * @param searchText the text to be searched
     * @param sort the sorting selection for the search query
     * @return the full text query
     */
    protected SearchQuery<AidsToNavigation> getSearchAidsToNavigationQueryByText(String searchText, Sort sort) {
        SearchSession searchSession = Search.session( this.entityManager );
        SearchScope<AidsToNavigation> scope = searchSession.scope( AidsToNavigation.class );
        return searchSession.search( scope )
                .extension(LuceneExtension.get())
                .where(f -> f.wildcard()
                        .fields( this.searchFields )
                        .matching( Optional.ofNullable(searchText).map(st -> "*"+st).orElse("") + "*" ))
                .sort(f -> f.fromLuceneSort(sort))
                .toQuery();
    }

    /**
     * Constructs a hibernate search query using Lucene based on the provided
     * AtoN UID and geometry. This query will be based solely on the aton
     * messages table and will include the following fields:
     * - UID
     * - Geometry
     * For any more elaborate search, the getSearchMessageQueryByText funtion
     * can be used.
     *
     * @param idCode the AtoN ID Code to be searched
     * @param geometry the geometry that the results should intersect with
     * @param geometry the geometry that the results should intersect with
     * @param fromTime the date-time the results should match from
     * @param sort the sorting selection for the search query
     * @return the full text query
     */
    protected SearchQuery<AidsToNavigation> getAidsToNavigationSearchQuery(String idCode,
                                                                           Geometry geometry,
                                                                           LocalDateTime fromTime,
                                                                           LocalDateTime toTime,
                                                                           Sort sort) {
        // Then build and return the hibernate-search query
        SearchSession searchSession = Search.session( this.entityManager );
        SearchScope<AidsToNavigation> scope = searchSession.scope( AidsToNavigation.class );
        return searchSession.search( scope )
                .where( f -> f.bool(b -> {
                            b.must(f.matchAll());
                            Optional.ofNullable(idCode).ifPresent(v -> b.must(f.match()
                                    .field("id_code")
                                    .matching(v)));
                            Optional.ofNullable(geometry).ifPresent(g-> b.must(f.extension(LuceneExtension.get())
                                    .fromLuceneQuery(createGeoSpatialQuery(g))));
                            Optional.ofNullable(fromTime).ifPresent(v -> b.must(f.range()
                                    .field("dateEnd")
                                    .atLeast(fromTime.toLocalDate())));
                            Optional.ofNullable(toTime).map(LocalDateTime::toLocalDate).ifPresent(v -> b.must(f.range()
                                    .field("dateStart")
                                    .atMost(toTime.toLocalDate())));
                        })
                )
                .sort(f -> ((LuceneSearchSortFactory)f).fromLuceneSort(sort))
                .toQuery();
    }


    /**
     * Creates a Lucene geo-spatial query based on the provided geometry. The
     * query isa recursive one based on the maxLevels defined (in this case 12,
     * which result in a sub-meter precision).
     *
     * @param geometry      The geometry to generate the spatial query for
     * @return The Lucene geo-spatial query constructed
     */
    protected Query createGeoSpatialQuery(Geometry geometry) {
        // Initialise the spatial strategy
        JtsSpatialContext ctx = JtsSpatialContext.GEO;
        int maxLevels = 12; //results in sub-meter precision for geo-hash
        SpatialPrefixTree grid = new GeohashPrefixTree(ctx, maxLevels);
        RecursivePrefixTreeStrategy strategy = new RecursivePrefixTreeStrategy(grid,"geometry");

        // Create the Lucene GeoSpatial Query
        return Optional.ofNullable(geometry)
                .map(g -> new SpatialArgs(SpatialOperation.Intersects, new JtsGeometry(g, ctx, false , true)))
                .map(strategy::makeQuery)
                .orElse(null);
    }


}
