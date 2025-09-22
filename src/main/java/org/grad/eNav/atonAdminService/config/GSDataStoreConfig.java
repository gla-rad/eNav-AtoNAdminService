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

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.geotools.api.data.DataStore;
import org.geotools.api.data.DataStoreFinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The Geomesa Data Store Configuration
 *
 * A simple configuration that allows connections to the Geomesa Data Store.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Slf4j
@Configuration
@ConditionalOnProperty({"kafka.brokers"})
public class GSDataStoreConfig {

    /**
     * The Kafka Brokers addresses.
     */
    @Value("${kafka.brokers:localhost:9092}" )
    private String kafkaBrokers;

    /**
     * The Number of Kafka Consumers.
     */
    @Value("${kafka.consumer.count:1}" )
    private Integer noKafkaConsumers;

    /**
     * Returns the Geomesa Data Store as a bean so that we can easily call it
     * anywhere we want.
     *
     * @return the Geomesa Data Store constructed according to the configuration parameters
     */
    @Bean
    DataStore gsDataStore() {
        // The connection parameters
        Map<String, String> params = new HashMap<>();
        params.put("kafka.brokers", kafkaBrokers);
        params.put("kafka.consumer.config", String.format("%s=%d", ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 10485760));
        params.put("kafka.consumer.count", Objects.toString(noKafkaConsumers));

        // And construct the data store
        try {
            return DataStoreFinder.getDataStore(params);
        } catch (IOException ex) {
            log.error(ex.getMessage());
            return null;
        }
    }
}
