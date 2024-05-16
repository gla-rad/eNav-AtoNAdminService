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

package org.grad.eNav.atonAdminService.models.dtos;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

/**
 * The S-201 Node Class.
 *
 * This node extends the S-100 abstract node to implement the S-201 messages
 * including the AtoN UID value.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class S201Node extends S100AbstractNode {

    // Class Variables
    private String atonUID;

    /**
     * Empty Constructor
     */
    public S201Node() {

    }

    /**
     * The Fully Populated  Constructor.
     *
     * @param atonUID       The AtoN UID
     * @param bbox          The object bounding box
     * @param content       The XML content
     */
    public S201Node(String atonUID, JsonNode bbox, String content) {
        super(bbox, content);
        this.atonUID = atonUID;
    }

    /**
     * Sets new atonUID.
     *
     * @param atonUID New value of atonUID.
     */
    public void setAtonUID(String atonUID) {
        this.atonUID = atonUID;
    }

    /**
     * Gets atonUID.
     *
     * @return Value of atonUID.
     */
    public String getAtonUID() {
        return atonUID;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof S201Node)) return false;
        if (!super.equals(o)) return false;
        S201Node s201Node = (S201Node) o;
        return Objects.equals(atonUID, s201Node.atonUID);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), atonUID);
    }

}
