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

package org.grad.eNav.atonAdminService.models.domain.s100;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * The Service Information Configuration.
 *
 * This configuration allows the service to read the basic information from
 * the application properties files. This information should be static and
 * not editable, so there is no reason to place it in a database really.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Validated
@ConfigurationProperties(prefix = "gla.rad.service.info")
public record ServiceInformationConfig(@NotNull String name,
                                       @NotNull String version,
                                       @NotNull String organization,
                                       List<String> electronicMailAddresses,
                                       @Pattern(regexp = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")
                                       String url,
                                       String phone,
                                       String fax,
                                       String city,
                                       String postalCode,
                                       String country,
                                       List<String> locales,
                                       String administrativeArea,
                                       String ihoProducerCode,
                                       String copyright) {

}
