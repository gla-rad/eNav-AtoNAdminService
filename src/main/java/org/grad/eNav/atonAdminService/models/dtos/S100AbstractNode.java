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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.grad.eNav.atonAdminService.models.IJsonSerializable;
import org.grad.eNav.atonAdminService.utils.GeometryJSONConverter;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.spatial4j.io.jackson.GeometryAsGeoJSONSerializer;

import java.util.Objects;

/**
 * The S-100 Node Abstract Class
 *
 * This class implements an abstract object suitable for most S-100 extensions
 * like S-125 and S-201. It will contain a generic representation of the object
 * like a bounding box and the XML object representation.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public abstract class S100AbstractNode implements IJsonSerializable {

    // Class Variables
    @JsonSerialize(using = GeometryAsGeoJSONSerializer.class)
    private Geometry geometry;
    private String content;

    /**
     * The Default Constructor.
     */
    public S100AbstractNode() {

    }

    /**
     * The Fully Populated Constructor.
     *
     * @param geometry          The object bounding box
     * @param content       The XML content
     */
    public S100AbstractNode(JsonNode geometry, String content) {
        this.geometry = GeometryJSONConverter.convertToGeometry(geometry);
        this.content = content;
    }

    /**
     * Sets new content.
     *
     * @param content New value of content.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets content.
     *
     * @return Value of content.
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets geometry.
     *
     * @return Value of geometry.
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * Sets new geometry.
     *
     * @param geometry New value of geometry.
     */
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    /**
     * A helper function that returns the geometry of the abstract node as a
     * GeoJSON node.
     *
     * @return the geometry as GeoJSON
     */
    @JsonIgnore
    public JsonNode getGeometryAsJson() {
        return GeometryJSONConverter.convertFromGeometry(this.geometry);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        S100AbstractNode that = (S100AbstractNode) o;
        return Objects.equals(geometry, that.geometry) && Objects.equals(content, that.content);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(geometry, content);
    }
}
