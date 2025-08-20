package org.grad.eNav.atonAdminService.controllers.secom.v1;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.servers.Server;
import org.grad.secom.springboot3.openapi.SecomOpenApiInfoProvider;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * The SECOM OpenApi Provider Implementation
 * <p/>
 * Provides the definition of the service OpenAPI documentation so that it can
 * be used for the description of the SECOM interfaces.
 *
 * @author - Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Component
public class SecomOpenApiInfoProviderImpl implements SecomOpenApiInfoProvider {

    /**
     * Returns the OpenAPI documentation details.
     *
     * @return The OpenAPI documentation details
     */
    @Override
    public OpenAPI getOpenApiInfo() {
        return new OpenAPI().schema("secom-v1", new Schema<>().$schema("openapi.json"))
                .info(new Info().title("AtoN Admin Service - SECOM v1.0 Interfaces")
                        .description("The SECOM interfaces of the AtoN Admin Service")
                        .termsOfService("https://gla-rad.org/")
                        .version("v0.0.1")
                        .contact(new Contact().email("Nikolaos.Vastardis@gla-rad.org"))
                        .license(new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(Arrays.asList(new Server[]{
                        new Server().url(".."),
                        new Server().url("."),
                        new Server().url("/api/secom")
                }))
                .externalDocs(new ExternalDocumentation()
                        .description("SpringShop Wiki Documentation")
                        .url("https://springshop.wiki.github.org/docs"));
    }
}
