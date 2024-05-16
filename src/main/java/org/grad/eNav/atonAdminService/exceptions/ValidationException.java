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

import org.apache.logging.log4j.util.Strings;
import org.grad.eNav.atonAdminService.models.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ValidationException extends RuntimeException implements AbstractException {


    /**
     * The Field errors.
     */
    private List<Pair<String,String>> fieldErrors;

    /**
     * The Global errors.
     */
    private List<String> globalErrors;

    /**
     * Instantiates a new Validation exception.
     *
     * Call to super to {@link Throwable#fillInStackTrace()}
     *
     * @param fieldErrors	The errors of individual fields
     * @param globalErrors	The global validation errors
     */
    public ValidationException(final List<Pair<String,String>> fieldErrors, final List<String> globalErrors) {
        super();
        setFieldErrors(fieldErrors);
        setGlobalErrors(globalErrors);
    }

    /**
     * Instantiates a new Validation exception.
     *
     * Call to super to {@link Throwable#fillInStackTrace()}
     */
    public ValidationException() {
        super();
        setFieldErrors(null);
        setGlobalErrors(null);
    }

    /**
     * Instantiates a new Validation exception.
     *
     * @param globalError the global error
     */
    public ValidationException(final String globalError) {
        this();
        addGlobalError(globalError);
    }

    /**
     * Instantiates a new Validation exception.
     *
     * @param field   the field
     * @param message the message
     */
    public ValidationException(final String field, final String message) {
        this();
        addFieldError(field, message);
    }

    /**
     * Gets short message.
     *
     * @return the short message
     */
    @Override
    public String getMessage() {
        return Stream.concat(
                getGlobalErrors().stream(),
                getFieldErrors().stream().map(p->String.format("Field \"%s\" %s.", p.getKey(),p.getValue()))
        ).collect(Collectors.collectingAndThen(Collectors.joining(" \n", "", ""),
                message -> Strings.isNotBlank(message) ? message : "There was an error during validation."
        ));
    }

    /**
     * Add global error.
     *
     * @param error the error
     */
    public void addGlobalError(final String error) {
        this.globalErrors.add(error);
    }

    /**
     * Add field error.
     *
     * @param field the field
     * @param error the error
     */
    public void addFieldError(final String field, final String error) {
        this.fieldErrors.add(new Pair<>(field,error));
    }

    /**
     * Gets field errors.
     *
     * @return the field errors
     */
    public List<Pair<String, String>> getFieldErrors() {
        return fieldErrors;
    }

    /**
     * Sets field errors.
     *
     * @param fieldErrors the field errors
     */
    public void setFieldErrors(final List<Pair<String, String>> fieldErrors) {
        this.fieldErrors = Optional.ofNullable(fieldErrors).orElseGet(ArrayList::new);
    }

    /**
     * Gets global errors.
     *
     * @return the global errors
     */
    public List<String> getGlobalErrors() {
        return globalErrors;
    }

    /**
     * Sets global errors.
     *
     * @param globalErrors the global errors
     */
    public void setGlobalErrors(final List<String> globalErrors) {
        this.globalErrors = Optional.ofNullable(globalErrors).orElseGet(ArrayList::new);
    }
}
