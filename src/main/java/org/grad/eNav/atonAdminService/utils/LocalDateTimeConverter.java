/*
 * Copyright (c) 2024 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grad.eNav.atonAdminService.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * The LocalDateTime Converter.
 * <p/>
 * This converter is used to add the local timezone information to the database
 * field before the entries are persisted. This would ensure the timezone
 * information is persistent and accurate, while keeping the internal
 * functionality simple through a LocalDateTime representation.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, ZonedDateTime> {

    @Override
    public ZonedDateTime convertToDatabaseColumn(LocalDateTime localDateTime) {
        return (localDateTime == null ? null : ZonedDateTime.of(localDateTime, ZoneId.systemDefault()));
    }

    @Override
    public LocalDateTime convertToEntityAttribute(ZonedDateTime zonedDateTime) {
        return (zonedDateTime == null ? null : zonedDateTime.toLocalDateTime());
    }

}
