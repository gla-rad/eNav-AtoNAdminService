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

package org.grad.eNav.atonAdminService.models;

import org.geotools.api.data.Query;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.filter.Filter;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;

import java.util.List;

/**
 * The Interface for the data entries transported through the Geomesa data
 * stores.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public interface GeomesaData<T> {

    // Class Variables
    String getTypeName();
    SimpleFeatureType getSimpleFeatureType();
    List<SimpleFeature> getFeatureData(List<T> list);
    List<Query> getFeatureQueries();
    Filter getSubsetFilter();

    /**
     * Creates a geotools filter based on a bounding box and date range
     *
     * @param geomField geometry attribute name
     * @param x0 bounding box min x value
     * @param y0 bounding box min y value
     * @param x1 bounding box max x value
     * @param y1 bounding box max y value
     * @param dateField date attribute name
     * @param t0 minimum time, exclusive, in the format "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
     * @param t1 maximum time, exclusive, in the format "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
     * @param attributesQuery any additional query string, or null
     * @return filter object
     * @throws CQLException if invalid CQL
     */
    static Filter createFilter(String geomField, double x0, double y0, double x1, double y1,
                               String dateField, String t0, String t1,
                               String attributesQuery) throws CQLException {

        // there are many different geometric predicates that might be used;
        // here, we just use a bounding-box (BBOX) predicate as an example.
        // this is useful for a rectangular query area
        String cqlGeometry = "BBOX(" + geomField + ", " + x0 + ", " + y0 + ", " + x1 + ", " + y1 + ")";

        // there are also quite a few temporal predicates; here, we use a
        // "DURING" predicate, because we have a fixed range of times that
        // we want to query
        String cqlDates = "(" + dateField + " DURING " + t0 + "/" + t1 + ")";

        // there are quite a few predicates that can operate on other attribute
        // types; the GeoTools Filter constant "INCLUDE" is a default that means
        // to accept everything
        String cqlAttributes = attributesQuery == null ? "INCLUDE" : attributesQuery;

        String cql = cqlGeometry + " AND " + cqlDates  + " AND " + cqlAttributes;

        // we use geotools ECQL class to parse a CQL string into a Filter object
        return ECQL.toFilter(cql);
    }
}
