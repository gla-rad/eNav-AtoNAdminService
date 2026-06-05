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

package org.grad.eNav.atonAdminService;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.SecurityFilterChain;

import static org.mockito.Mockito.mock;

/**
 * This is a test configuration for implementing some Feign required security
 * mocks. Use wisely...
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@TestConfiguration
public class TestFeignSecurityConfig {

    /**
     * Provides a stub SecurityFilterChain so that
     * OAuth2ClientWebSecurityAutoConfiguration.OAuth2SecurityFilterChainConfiguration
     * (which is conditional on no SecurityFilterChain bean being present) is
     * suppressed. Without this, test contexts that exclude SecurityAutoConfiguration
     * but include OAuth2 client auto-configuration fail because
     * oauth2SecurityFilterChain requires an HttpSecurity prototype that is not
     * available when no @EnableWebSecurity is active.
     */
    @Bean
    public SecurityFilterChain mockSecurityFilterChain() {
        return mock(SecurityFilterChain.class);
    }

}
