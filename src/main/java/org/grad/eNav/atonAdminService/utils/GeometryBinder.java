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

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.hibernate.search.backend.elasticsearch.ElasticsearchExtension;
import org.hibernate.search.mapper.pojo.bridge.ValueBridge;
import org.hibernate.search.mapper.pojo.bridge.binding.ValueBindingContext;
import org.hibernate.search.mapper.pojo.bridge.mapping.programmatic.ValueBinder;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeFromIndexedValueContext;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeToIndexedValueContext;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;

import java.util.Optional;

/**
 * The Hibernate Search Geometry Value Binder.
 *
 * This value binder class is used in order for Hibernate Search to generate
 * indexable fields from the geometry variables of each instance and then
 * be able to perform search queries on them using the Elasticsearch backend.
 * <p>
 * The geometries are indexed into a native Elasticsearch
 * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/geo-shape.html">geo_shape</a>
 * field, using their Well-Known-Text (WKT) representation. Elasticsearch accepts
 * WKT for both the indexing and the querying of geo_shape fields, which makes it
 * an easy and lossless way to map the JTS geometries. The geo-spatial queries can
 * then be constructed through the {@link GeometryUtils} utility.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class GeometryBinder implements ValueBinder {

    /**
     * The main binding operation where the geometry value bridge is used and
     * the geometry objects are indexed as a native Elasticsearch geo_shape
     * field, using their WKT representation.
     *
     * @param context    The value binding context.
     */
    @Override
    public void bind(ValueBindingContext<?> context) {
        context.bridge(
                Geometry.class,
                new GeometryValueBridge(),
                context.typeFactory()
                        .extension(ElasticsearchExtension.get())
                        .asNative()
                        .mapping("{\"type\": \"geo_shape\"}")
        );
    }

    /**
     * The private Geometry Value Bridge that translates the JTS geometries into
     * their WKT representation, wrapped into a JSON primitive so that it can be
     * indexed into a native Elasticsearch geo_shape field.
     */
    private static class GeometryValueBridge implements ValueBridge<Geometry, JsonElement> {
        @Override
        public JsonElement toIndexedValue(Geometry value, ValueBridgeToIndexedValueContext context) {
            return Optional.ofNullable(value)
                    .map(g -> new WKTWriter().write(g))
                    .<JsonElement>map(JsonPrimitive::new)
                    .orElse(null);
        }

        @Override
        public Geometry fromIndexedValue(JsonElement value, ValueBridgeFromIndexedValueContext context) {
            // The geo_shape field is not projectable, so this is mostly a
            // best-effort reconstruction kept for type consistency.
            try {
                return Optional.ofNullable(value)
                        .filter(JsonElement::isJsonPrimitive)
                        .map(JsonElement::getAsString)
                        .map(wkt -> {
                            try {
                                return new WKTReader().read(wkt);
                            } catch (ParseException ex) {
                                throw new IllegalArgumentException(ex);
                            }
                        })
                        .orElse(null);
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }
    }

}