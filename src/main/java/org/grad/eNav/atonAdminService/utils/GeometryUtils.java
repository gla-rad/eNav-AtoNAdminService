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

package org.grad.eNav.atonAdminService.utils;

import com.google.gson.JsonObject;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;

import java.util.Optional;

/**
 * The Geometry Utils Class.
 *
 * This utility class contains various methods that can be used to easily
 * manage geometries and deal their relevant operations.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class GeometryUtils {

    /**
     * Translates the provided JTS geometry into its Well-Known-Text (WKT)
     * representation, which is directly understood by the Elasticsearch
     * geo_shape fields and queries.
     *
     * @param geometry the geometry to be translated
     * @return the WKT representation of the geometry, or null if not provided
     */
    public static String toWkt(Geometry geometry) {
        return Optional.ofNullable(geometry)
                .map(g -> new WKTWriter().write(g))
                .orElse(null);
    }

    /**
     * Creates the JSON for an Elasticsearch
     * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geo-shape-query.html">geo_shape</a>
     * query that matches all the documents whose geometry, indexed under the
     * provided field, intersects with the provided geometry. The result can be
     * fed directly to the Hibernate Search Elasticsearch extension through the
     * {@code fromJson} predicate.
     *
     * @param fieldName the name of the indexed geo_shape field
     * @param geometry  the geometry that the results should intersect with
     * @return the geo_shape query as a JSON object, or null if no geometry is provided
     */
    public static JsonObject geoShapeIntersectsQuery(String fieldName, Geometry geometry) {
        return Optional.ofNullable(geometry)
                .map(GeometryUtils::toWkt)
                .map(wkt -> {
                    final JsonObject shape = new JsonObject();
                    shape.addProperty("shape", wkt);
                    shape.addProperty("relation", "intersects");
                    final JsonObject field = new JsonObject();
                    field.add(fieldName, shape);
                    final JsonObject query = new JsonObject();
                    query.add("geo_shape", field);
                    return query;
                })
                .orElse(null);
    }

    /**
     * A helper function to simplify the joining of geometries without troubling
     * ourselves for the null checking... which is a pain.
     *
     * @param geometries the geometries variable argument
     * @return the resulting joined geometry
     */
    public static Geometry joinGeometries(Geometry... geometries) {
        Geometry result = null;
        for(Geometry geometry : geometries) {
            if(result == null && geometry == null) {
                result = null;
            } else if(result == null || geometry == null) {
                result = Optional.ofNullable(result).orElse(geometry);
            } else {
                result = result.union(geometry);
            }
        }
        return result;
    }
}

