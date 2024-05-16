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

import org.geotools.api.data.DataStore;
import org.grad.eNav.atonAdminService.components.DomainDtoMapper;
import org.grad.eNav.atonAdminService.config.GlobalConfig;
import org.grad.eNav.atonAdminService.models.domain.DatasetContentLog;
import org.grad.eNav.atonAdminService.models.domain.s201.AidsToNavigation;
import org.grad.eNav.atonAdminService.models.domain.s201.S201Dataset;
import org.grad.eNav.atonAdminService.models.domain.secom.SubscriptionRequest;
import org.grad.eNav.atonAdminService.models.dtos.DatasetContentLogDto;
import org.grad.eNav.atonAdminService.models.dtos.s201.AidsToNavigationDto;
import org.grad.eNav.atonAdminService.models.dtos.s201.S201DataSetDto;
import org.grad.eNav.atonAdminService.models.dtos.secom.SubscriptionRequestDto;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.mock;

/**
 * This is a test only configuration that will get activated when the "test"
 * profile is active.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@TestConfiguration
@Import(GlobalConfig.class)
@TestPropertySource({"classpath:application.properties"})
public class TestingConfiguration {

	/**
	 * Aids to Navigation Mapper from Domain to DTO.
	 */
	@Bean
	public DomainDtoMapper<AidsToNavigation, AidsToNavigationDto> aidsToNavigationToDtoMapper() {
		return new DomainDtoMapper<>();
	}

	/**
	 * DatasetMapper from Domain to DTO.
	 */
	@Bean
	public DomainDtoMapper<S201Dataset, S201DataSetDto> datasetDtoMapper() {
		return new DomainDtoMapper<>();
	}

	/**
	 * DatasetMapper from DTO to Domain.
	 */
	@Bean
	public DomainDtoMapper<S201DataSetDto, S201Dataset> datasetDomainMapper() {
		return new DomainDtoMapper<>();
	}

	/**
	 * DatasetContentLogMapper from Domain to DTO.
	 */
	@Bean
	public DomainDtoMapper<DatasetContentLog, DatasetContentLogDto> datasetContentLogDtoMapper() {
		return new DomainDtoMapper<>();
	}

	/**
	 * SubscriptionDomainMapper from DTO to Domain.
	 */
	@Bean
	public DomainDtoMapper<SubscriptionRequestDto, SubscriptionRequest> subscriptionDomainMapper() {
		return new DomainDtoMapper<>();
	}

	/**
	 * SubscriptionDomainMapper from Domain to DTO.
	 */
	@Bean
	public DomainDtoMapper<SubscriptionRequest, SubscriptionRequestDto> subscriptionDtoMapper() {
		return new DomainDtoMapper<>();
	}

	/**
	 * The registration listener for feign registers the client inside a
	 * client repository which is als needed, hence mocked.
	 */
	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
		return mock(ClientRegistrationRepository.class);
	}

	/**
	 * Mock a Geomesa Data Store bean so that we pretend we have a connection
	 * while the actual GS Data Store configuration is not enabled.
	 *
	 * @return the Geomesa Data Store bean
	 */
	@Bean
	DataStore gsDataStore() {
		return mock(DataStore.class);
	}

}
