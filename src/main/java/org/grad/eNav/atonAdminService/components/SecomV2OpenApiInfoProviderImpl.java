/*
 * Copyright (c) 2026 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.grad.eNav.atonAdminService.components;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.servers.Server;
import org.grad.secomv2.springboot4.openapi.SecomV2OpenApiInfoProvider;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * The SECOM v2 OpenApi Provider Implementation
 * <p/>
 * Provides the definition of the service OpenAPI documentation so that it can
 * be used for the description of the SECOM V2 interfaces.
 *
 * @author - Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Component
public class SecomV2OpenApiInfoProviderImpl implements SecomV2OpenApiInfoProvider {

    /**
     * Returns the OpenAPI documentation details.
     *
     * @return The OpenAPI documentation details
     */
    @Override
    public OpenAPI getSecomOpenApiInfo() {
        return new OpenAPI().schema("secom-v2", new Schema<>().$schema("openapi.json"))
                .info(new Info().title("AtoN Admin Service - SECOM v2.0 Interfaces")
                        .description("The SECOM V2 interfaces of the AtoN Admin Service")
                        .termsOfService("https://gla-rad.org/")
                        .version("v0.0.1")
                        .contact(new Contact().email("Nikolaos.Vastardis@gla-rad.org"))
                        .license(new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0.html")))
//                .servers(Arrays.asList(new Server[]{
//                        new Server().url(".."),
//                        new Server().url("."),
//                        new Server().url("/api/secom")
//                }))
                .externalDocs(new ExternalDocumentation()
                        .description("SpringShop Wiki Documentation")
                        .url("https://springshop.wiki.github.org/docs"));
    }
}
