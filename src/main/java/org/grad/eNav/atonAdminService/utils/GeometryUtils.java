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

import org.locationtech.jts.geom.Geometry;

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
