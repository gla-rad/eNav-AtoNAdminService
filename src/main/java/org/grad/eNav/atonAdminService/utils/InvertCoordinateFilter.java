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

import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFilter;
import org.locationtech.jts.geom.Geometry;

/**
 * The InvertCoordinateFilter class.
 * <p/>
 * A generic class to implement the swapping of the coordinates in a generic
 * geometry object. This feature can be used while
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
class InvertCoordinateFilter implements CoordinateSequenceFilter {

    // Filter variables
    private boolean changed = false;
    private boolean done = false;

    /**
     * Performs an operation on a coordinate in a {@link CoordinateSequence}.
     *
     *@param seq  the <code>CoordinateSequence</code> to which the filter is applied
     *@param i the index of the coordinate to apply the filter to
     */
    @Override
    public void filter(CoordinateSequence seq, int i) {
        // Swap the variables
        double oldX = seq.getCoordinate(i).x;
        seq.getCoordinate(i).x = seq.getCoordinate(i).y;
        seq.getCoordinate(i).y = oldX;

        // Update the filter
        this.changed = i < seq.size();
        this.done = i == seq.size() - 1;
    }

    /**
     * Reports whether the application of this filter can be terminated.
     * Once this method returns <tt>true</tt>, it must
     * continue to return <tt>true</tt> on every subsequent call.
     *
     * @return true if the application of this filter can be terminated.
     */
    @Override
    public boolean isDone() {
        return this.done;
    }

    /**
     * Reports whether the execution of this filter
     * has modified the coordinates of the geometry.
     * If so, {@link Geometry#geometryChanged} will be executed
     * after this filter has finished being executed.
     * <p>
     * Most filters can simply return a constant value reflecting
     * whether they are able to change the coordinates.
     *
     * @return true if this filter has changed the coordinates of the geometry
     */
    @Override
    public boolean isGeometryChanged() {
        return this.changed;
    }

}
