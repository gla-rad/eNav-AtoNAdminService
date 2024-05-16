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

import org.hibernate.search.mapper.pojo.bridge.ValueBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeToIndexedValueContext;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The Null Value Indexer Bridge Class.
 *
 * This hibernate search bridge will index all the null values as "NULL"
 * to make it easy to match queries for non specified fields. It can be used
 * for enums, collections and other generic objects.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class NullValueIndexerBridge implements ValueBridge<Object, String> {
    @Override
    public String toIndexedValue(Object object, ValueBridgeToIndexedValueContext valueBridgeToIndexedValueContext) {
        // Check for nulls first
        if(Objects.nonNull(object)) {
            if(object instanceof Enum){
                return ((Enum<?>)object).name();
            }
            else if(object instanceof Collection<?>) {
                return ((Collection<?>)object).stream().map(Object::toString).collect(Collectors.joining(","));
            }
            else {
                return object.toString();
            }
        }
        // Otherwise, it's null
        return "NULL";
    }
}
