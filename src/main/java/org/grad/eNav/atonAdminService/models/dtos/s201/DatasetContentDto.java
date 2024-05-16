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

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * The Dataset Content DTO Class
 * <p>
 * This is the basic class for transmitting the generic Dataset Content
 * Information onto third parties. This is going to be encoded as a JSON object,
 * and it does NOT contain all the fields of the locally persisted class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see org.grad.eNav.atonAdminService.models.domain.DatasetContent
 */
public class DatasetContentDto {

    // Class Variables
    private LocalDateTime generatedAt;
    private BigInteger sequenceNo;
    private BigInteger contentLength;

    /**
     * Gets generated at.
     *
     * @return the generated at
     */
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    /**
     * Sets generated at.
     *
     * @param generatedAt the generated at
     */
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    /**
     * Gets sequence no.
     *
     * @return the sequence no
     */
    public BigInteger getSequenceNo() {
        return sequenceNo;
    }

    /**
     * Sets sequence no.
     *
     * @param sequenceNo the sequence no
     */
    public void setSequenceNo(BigInteger sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    /**
     * Gets content length.
     *
     * @return the content length
     */
    public BigInteger getContentLength() {
        return contentLength;
    }

    /**
     * Sets content length.
     *
     * @param contentLength the content length
     */
    public void setContentLength(BigInteger contentLength) {
        this.contentLength = contentLength;
    }
}
