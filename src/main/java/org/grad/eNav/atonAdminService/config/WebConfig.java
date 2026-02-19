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

import org.grad.eNav.atonAdminService.components.GeoJsonStringToGeometryConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The WebConfig Class
 *
 * This is the main configuration class for the Web MVC operations.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * The GeoJSON string to Geometry Converter.
     */
    @Autowired
    GeoJsonStringToGeometryConverter geoJsonStringToGeometryConverter;

    /**
     * Add the static resources and webjars to the web resources.
     *
     * @param registry the resource handler registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .resourceChain(false);
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/")
                .resourceChain(false);
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .resourceChain(false);
        registry.addResourceHandler("/lib/**")
                .addResourceLocations("classpath:/static/lib/")
                .resourceChain(false);
        registry.addResourceHandler("/src/**")
                .addResourceLocations("classpath:/static/src/")
                .resourceChain(false);
        registry.addResourceHandler("/xsd/**")
                .addResourceLocations("classpath:/xsd/")
                .resourceChain(false);
        registry.setOrder(1);
    }

    /**
     * Make the index.html our main page so that it's being picked up by
     * Thymeleaf.
     *
     * @param registry The View Controller Registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index");
    }

    /**
     * Add the converters between GeoJSON strings and Geometry, enums etc...
     *
     * @param registry the Formatter Registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(geoJsonStringToGeometryConverter);
    }

}
