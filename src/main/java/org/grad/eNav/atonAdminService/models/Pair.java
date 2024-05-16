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

import java.util.Objects;

/**
 * A generic pair implementation for the AtoN Service.
 *
 * Since java does not have its own pair implementation, this is a simple
 * class to provide the pair functionality.
 *
 * @param <T> The pair key
 * @param <A> The pair value
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class Pair<T,A> {

    //Class Variables
    private T key;
    private A value;

    /**
     * Pair constructor.
     * @param key first value of the pair
     * @param value second value of the pair
     */
    public Pair(T key, A value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Sets new key.
     *
     * @param key New value of key.
     */
    public void setKey(T key) {
        this.key = key;
    }

    /**
     * Gets value.
     *
     * @return Value of value.
     */
    public A getValue() {
        return value;
    }

    /**
     * Sets new value.
     *
     * @param value New value of value.
     */
    public void setValue(A value) {
        this.value = value;
    }

    /**
     * Gets key.
     *
     * @return Value of key.
     */
    public T getKey() {
        return key;
    }

    /**
     * The pair equality function.
     *
     * @param o The object to check against
     * @return Whether the provided object is equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(key, pair.key) &&
                Objects.equals(value, pair.value);
    }

    /**
     * The hashcode generation function.
     *
     * @return The object hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    /**
     * Overriding the toString method of the pair object.
     *
     * @return The object string representation
     */
    @Override
    public String toString() {
        return "Pair{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
