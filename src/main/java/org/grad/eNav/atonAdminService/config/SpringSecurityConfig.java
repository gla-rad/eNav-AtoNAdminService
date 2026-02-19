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

package org.grad.eNav.atonAdminService.config;

import jakarta.servlet.DispatcherType;
import org.grad.eNav.atonAdminService.config.keycloak.KeycloakGrantedAuthoritiesMapper;
import org.grad.eNav.atonAdminService.config.keycloak.KeycloakJwtAuthenticationConverter;
import org.grad.eNav.atonAdminService.config.keycloak.KeycloakLogoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.util.*;

/**
 * The Spring Security Configuration.
 *
 * This is the security definition for the security configuration and the filter
 * chains the service.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnProperty(value = "keycloak.enabled", matchIfMissing = true)
class SpringSecurityConfig {

    /**
     * The default application name.
     */
    @Value("${keycloak.clientId:aton-service}")
    private String clientId;

    /**
     * The default application name.
     */
    @Value("${gla.rad.aton-service.resources.open:/,/index,/webjars/**,/css/**,/lib/**,/images/**,/src/**,/api/secom/**}")
    private String[] openResources;

    /**
     * The REST Template.
     *
     * @return the REST template
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Define a slightly more flexible HTTP Firewall configuration that allows
     * characters like semicolons, slashes and percentages.
     */
    @Bean
    protected HttpFirewall securityHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        firewall.setAllowUrlEncodedSlash(true);
        firewall.setAllowUrlEncodedPercent(true);
        return firewall;
    }

    /**
     * Forwarded header filter filter registration bean.
     * <p>
     * This corrects the urls produced by the microservice when accessed from a proxy server.
     * E.g. Api gateway:
     * my-service.com/style.css -> api-gateway.com/my-service/style.css
     * <p>
     * The proxy server should be sending the forwarded header address as a header
     * which this filter will pick up and resolve for us.
     *
     * @return the filter registration bean
     */
    @Bean
    protected FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        final FilterRegistrationBean<ForwardedHeaderFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new ForwardedHeaderFilter());
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR);
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegistrationBean;
    }

    /**
     * Specify a converter for the Keycloak authority claims.
     *
     * @return The Keycloak JWT Authentication Converter
     */
    @Bean
    Converter<Jwt, ? extends AbstractAuthenticationToken> keycloakJwtAuthenticationConverter() {
        return new KeycloakJwtAuthenticationConverter(this.clientId);
    }

    /**
     * Specify a mapper for the keycloak authority claims.
     *
     * @return the Keycloak Granted Authority Mapper
     */
    @Bean
    protected GrantedAuthoritiesMapper keycloakGrantedAuthoritiesMapper() {
        return new KeycloakGrantedAuthoritiesMapper(this.clientId);
    }

    /**
     * Define a logout handler for handling Keycloak logouts.
     *
     * @param restTemplate the REST template
     * @return the Keycloak logout handler
     */
    @Bean
    protected KeycloakLogoutHandler keycloakLogoutHandler(RestTemplate restTemplate) {
        return new KeycloakLogoutHandler(restTemplate);
    }

    /**
     * Define the session authentication strategy which uses a simple session
     * registry to store our current sessions.
     *
     * @return the session authentication strategy
     */
    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    /**
     * Defines the resource access authorities mapper to retrieve the respective
     * claims from the user information.
     * <p/>
     * Note that in order for this to work we need to include the resource
     * access information to the user information in the OAuth2 server (i.e.
     * in our keycloak) client scope mappers.
     *
     * @return the granted authorities mapper
     */
    @Bean
    GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            // Initialise an authorities map
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            // Parse the role/resource-access claims for this client and add
            // them to the granted authorities.
            authorities.forEach(authority -> {
                Optional.of(authority)
                        .filter(OAuth2UserAuthority.class::isInstance)
                        .map(OAuth2UserAuthority.class::cast)
                        .map(OAuth2UserAuthority::getAttributes)
                        .map(attributes -> attributes.getOrDefault("resource_access", Collections.emptyMap()))
                        .filter(Map.class::isInstance)
                        .map(Map.class::cast)
                        .map(claims -> KeycloakJwtAuthenticationConverter.extractResourceRoles(claims, this.clientId))
                        .ifPresent(mappedAuthorities::addAll);
            });
            // And finally return the populated authorities map
            return mappedAuthorities;
        };
    }

    /**
     * Defines the security web-filter chains.
     *
     * Allows open access to the health and info actuator endpoints.
     * All other actuator endpoints are only available for the actuator role.
     * Finally, all other exchanges need to be authenticated.
     */
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           ClientRegistrationRepository clientRegistrationRepository,
                                           RestTemplate restTemplate) throws Exception {
        // Authenticate through configured OpenID Provider
        http.oauth2Login(login -> login
                .loginPage("/oauth2/authorization/keycloak")
                .userInfoEndpoint((userInfoCustomizer) -> userInfoCustomizer
                        .userAuthoritiesMapper(this.userAuthoritiesMapper())
                )
//                .authorizationEndpoint().baseUri("/oauth2/authorization/keycloak")
//                .authorizationRequestRepository(new HttpSessionOAuth2AuthorizationRequestRepository())
        );
        // Also, logout at the OpenID Connect provider
        http.logout(logout -> logout
                .deleteCookies("JSESSIONID")
                .addLogoutHandler(keycloakLogoutHandler(restTemplate))
                .logoutSuccessUrl("/")
//                .logoutSuccessHandler(new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository)))
        );
        // Require authentication for all requests
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(EndpointRequest.to(
                                InfoEndpoint.class,     //info endpoints
                                HealthEndpoint.class    //health endpoints
                        )).permitAll()
                        .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ACTUATOR")
                        .requestMatchers(this.openResources).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(keycloakJwtAuthenticationConverter())
                        )
                );

        // Disable the CSRF
        http.csrf(AbstractHttpConfigurer::disable);

        // Add an exception handler to add a permission response
        http.exceptionHandling(handler ->
                handler.accessDeniedHandler((req, res, ex) -> {
                    res.setStatus(403);
                    res.addHeader(
                            "X-atonService-error",
                            "You don't seem to have the appropriate permissions to perform this action."
                    );
                })
        );

        // Build and return
        return http.cors(AbstractHttpConfigurer::disable).build();
    }

}
