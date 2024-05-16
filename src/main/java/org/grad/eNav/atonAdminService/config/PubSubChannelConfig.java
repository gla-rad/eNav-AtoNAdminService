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

import org.grad.eNav.atonAdminService.components.PubSubErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.config.EnableIntegration;

import java.util.concurrent.Executor;

/**
 * The PubSubChannelConfig Class
 *
 * This class establishes a publish-subscribe channel when the internal
 * components of this application can exchange data messages. This will
 * be used to transfer the incoming radar data to various consumers.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Configuration
@EnableIntegration
public class PubSubChannelConfig {

    /**
     * The Asynchronous Task Executor.
     */
    @Autowired
    Executor taskExecutor;

    /**
     * Defining a publication S-201-subscribe Spring Integration channel to
     * exchange the published S-201 datasets between the application components.
     *
     * @return The publish-subscribe channel for the published S-201 datasets
     */
    @Bean
    public PublishSubscribeChannel s201PublicationChannel() {
        PublishSubscribeChannel pubsubChannel = new PublishSubscribeChannel(this.taskExecutor);
        pubsubChannel.setErrorHandler(new PubSubErrorHandler());
        return pubsubChannel;
    }

    /**
     * Defining a deletion publish-subscribe Spring Integration channel to
     * exchange the removed S-201 datasets between the application components.
     *
     * @return The publish-subscribe channel for the removed S-201 datasets
     */
    @Bean
    public PublishSubscribeChannel s201RemovalChannel() {
        PublishSubscribeChannel pubsubChannel = new PublishSubscribeChannel(this.taskExecutor);
        pubsubChannel.setErrorHandler(new PubSubErrorHandler());
        return pubsubChannel;
    }

    /**
     * Defining a publication publish-subscribe Spring Integration channel to
     * exchange the incoming AtoN information between the application components.
     *
     * @return The publish-subscribe channel for the incoming AtoN information
     */
    @Bean
    public PublishSubscribeChannel atonPublicationChannel() {
        PublishSubscribeChannel pubsubChannel = new PublishSubscribeChannel(this.taskExecutor);
        pubsubChannel.setErrorHandler(new PubSubErrorHandler());
        return pubsubChannel;
    }

    /**
     * Defining a deletion publish-subscribe Spring Integration channel to
     * exchange the deleted AtoN information between the application components.
     *
     * @return The publish-subscribe channel for the deleted AtoN information
     */
    @Bean
    public PublishSubscribeChannel atonDeletionChannel() {
        PublishSubscribeChannel pubsubChannel = new PublishSubscribeChannel(this.taskExecutor);
        pubsubChannel.setErrorHandler(new PubSubErrorHandler());
        return pubsubChannel;
    }

}
