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

package org.grad.eNav.atonAdminService.exceptions;

import org.grad.eNav.atonAdminService.models.Pair;
import java.util.List;

/**
 * The abstract exception.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public interface AbstractException {

    /**
     * Gets the exception message.
     *
     * @return the default message.
     */
    public String getMessage();

    /**
     * Gets field errors.
     *
     * @return the field errors
     */
    public List<Pair<String, String>> getFieldErrors();

    /**
     * Gets global errors.
     *
     * @return the global errors
     */
    public List<String> getGlobalErrors();
}