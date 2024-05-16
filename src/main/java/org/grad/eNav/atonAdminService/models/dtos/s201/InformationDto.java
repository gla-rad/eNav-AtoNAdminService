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

package org.grad.eNav.atonAdminService.models.dtos.s201;

/**
 * The Information DTO Class.
 *
 * This class is used to translate the Aids to Navigation Information entities
 * as a JSON-encoded DTO class inside the AidsToNavigationDto objects.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see org.grad.eNav.atonAdminService.models.dtos.s201.AidsToNavigationDto
 */
public class InformationDto {

    // Class Variables
    private String fileLocator;
    private String fileReference;
    private String headline;
    private String language;
    private String text;

    /**
     * Gets file locator.
     *
     * @return the file locator
     */
    public String getFileLocator() {
        return fileLocator;
    }

    /**
     * Sets file locator.
     *
     * @param fileLocator the file locator
     */
    public void setFileLocator(String fileLocator) {
        this.fileLocator = fileLocator;
    }

    /**
     * Gets file reference.
     *
     * @return the file reference
     */
    public String getFileReference() {
        return fileReference;
    }

    /**
     * Sets file reference.
     *
     * @param fileReference the file reference
     */
    public void setFileReference(String fileReference) {
        this.fileReference = fileReference;
    }

    /**
     * Gets headline.
     *
     * @return the headline
     */
    public String getHeadline() {
        return headline;
    }

    /**
     * Sets headline.
     *
     * @param headline the headline
     */
    public void setHeadline(String headline) {
        this.headline = headline;
    }

    /**
     * Gets language.
     *
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets language.
     *
     * @param language the language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Gets text.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets text.
     *
     * @param text the text
     */
    public void setText(String text) {
        this.text = text;
    }
}
