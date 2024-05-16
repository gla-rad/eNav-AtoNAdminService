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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * The WebSocketConfig Class
 *
 * This configuration class sets up the WebSocket for this app where remote
 * clients can monitor the incoming AtoN data.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * The WebSocket Name
     */
    @Value("${gla.rad.aton-service.web-socket.name:aton-service-websocket}")
    private String webSocketName;

    /**
     * The General Destination Prefix
     */
    @Value("${gla.rad.aton-service.web-socket.prefix:topic}")
    private String prefix;

    /**
     * The AtoN Service Data Endpoint of the WebSocket
     */
    @Value("${gla.rad.aton-service.web-socket.aton-data-endpoint:atons}")
    private String atonDataEndpoint;

    /**
     * This function implements the basic registration for our WebSocket message
     * broker. It basically set's the destination prefix and all endpoints.
     *
     * @param config    The message broker configuration
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/"+ this.prefix);
        config.setApplicationDestinationPrefixes("/"+ this.atonDataEndpoint);
    }

    /**
     * Increase the limits of the socket transport both in terms of size
     * and time.
     *
     * param registry   The web socket transport registration
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry)
    {
        registry.setMessageSizeLimit(5000000);
        registry.setSendBufferSizeLimit(5000000);
        registry.setSendTimeLimit(500000);
    }

    /**
     * This is where the WebSocket is actually registered into the application
     * as an endpoint and become active.
     *
     * @param registry  The active WebSocket Registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/" + this.webSocketName)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

}
