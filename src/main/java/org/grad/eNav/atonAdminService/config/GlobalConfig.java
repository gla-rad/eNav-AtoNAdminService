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

import _int.iho.s_201.gml.cs0._2.*;
import _int.iho.s_201.gml.cs0._2.impl.*;
import _int.iho.s_201.s_100.gml.base._5_2.impl.DataSetIdentificationTypeImpl;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.CaseUtils;
import org.grad.eNav.atonAdminService.models.domain.s100.ServiceInformationConfig;
import org.grad.eNav.atonAdminService.models.domain.s201.*;
import org.grad.eNav.atonAdminService.models.domain.s201.AtonAggregation;
import org.grad.eNav.atonAdminService.models.domain.s201.AtonAssociation;
import org.grad.eNav.atonAdminService.models.domain.secom.SubscriptionRequest;
import org.grad.eNav.atonAdminService.models.dtos.s201.AidsToNavigationDto;
import org.grad.eNav.atonAdminService.models.enums.ReferenceTypeRole;
import org.grad.eNav.atonAdminService.utils.*;
import org.grad.eNav.s201.utils.S201Utils;
import org.locationtech.jts.io.ParseException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.ValidationException;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.ErrorMessage;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The Global Configuration.
 *
 * A class to define the global configuration for the application.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Configuration
@EnableConfigurationProperties(ServiceInformationConfig.class)
@Slf4j
public class GlobalConfig {

    /**
     * The Application Operator Name Information.
     */
    @Value("${gla.rad.aton-service.info.operatorName:Unknown}")
    private String appOperatorName;

    /**
     * <p>
     * Add an HTTP trace repository in memory to be used for the respective
     * actuator.
     * </p>
     * <p>
     * The functionality has been removed by default in Spring Boot 2.2.0. For
     * more info see:
     * </p>
     * <a href="https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.2.0-M3-Release-Notes#actuator-http-trace-and-auditing-are-disabled-by-default">...</a>
     *
     * @return the in memory HTTP trance repository
     */
    @ConditionalOnProperty(value = "management.endpoint.httpexchanges.enabled", havingValue = "true")
    @Bean
    public HttpExchangeRepository httpTraceRepository() {
        return new InMemoryHttpExchangeRepository();
    }

    /**
     * The Model Mapper allows easy mapping between DTOs and domain objects.
     *
     * @return the model mapper bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // ================================================================== //
        // Provide a configuration for all the mappings here to keep tidy     //
        // ================================================================== //

        // Since the S-201 objects contains lists that do NOT have a setter,
        // we can use the protected fields directly to perform the mapping.
        // Note that this creates ambiguity with the existing setters, so we
        // should account for that.
        modelMapper.getConfiguration()
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PROTECTED)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setAmbiguityIgnored(true);

        // Configure the dataset top-level mapping first
        modelMapper.emptyTypeMap(S201Dataset.class, DatasetImpl.class)
                .addMappings(mapper -> {
                    mapper.using(ctx -> modelMapper.map(((S201Dataset)ctx.getSource()).getDatasetIdentificationInformation(), DataSetIdentificationTypeImpl.class) )
                            .map(src -> src, DatasetImpl::setDatasetIdentificationInformation);
                    mapper.using(ctx -> new DatasetImpl.MembersImpl())
                            .map(src -> src, DatasetImpl::setMembers);
                })
                .implicitMappings();

        // For interface fields that don't have constructors, use converters
        modelMapper.addConverter(ctx -> new SignalSequenceTypeImpl(),
                SignalSequence.class, SignalSequenceType.class);

        // Loop all the mapped S-201 AtoN types and configure the model mapper
        // to translate correctly from the S-201 onto the local classes and
        // vice versa
        for(S201AtonTypes atonType : S201AtonTypes.values()) {
            // Skip the unknown type, we don't need it
            if(atonType == S201AtonTypes.UNKNOWN) {
                continue;
            }

            modelMapper.createTypeMap(atonType.getS201Class(), atonType.getLocalClass())
                    .implicitMappings()
                    .addMappings(mapper -> {
                        mapper.skip(AidsToNavigation::setId); // We don't know if the ID is correct so skip it
                        mapper.using(ctx -> S201Utils.s100TruncatedDateToLocalDate(Optional.ofNullable(ctx.getSource())
                                        .map(AidsToNavigationTypeImpl.class::cast)
                                        .map(AidsToNavigationTypeImpl::getFixedDateRange)
                                        .map(FixedDateRangeType::getDateStart)
                                        .orElse(null)))
                                .map(src -> src, AidsToNavigation::setDateStart);
                        mapper.using(ctx -> S201Utils.s100TruncatedDateToLocalDate(Optional.ofNullable(ctx.getSource())
                                        .map(AidsToNavigationTypeImpl.class::cast)
                                        .map(AidsToNavigationTypeImpl::getFixedDateRange)
                                        .map(FixedDateRangeType::getDateEnd)
                                        .orElse(null)))
                                .map(src -> src, AidsToNavigation::setDateEnd);
                        mapper.using(ctx -> S201Utils.s100TruncatedDateToLocalDate(Optional.ofNullable(ctx.getSource())
                                        .map(AidsToNavigationTypeImpl.class::cast)
                                        .map(AidsToNavigationTypeImpl::getPeriodicDateRange)
                                        .map(PeriodicDateRangeType::getDateStart)
                                        .orElse(null)))
                                .map(src -> src, AidsToNavigation::setPeriodStart);
                        mapper.using(ctx -> S201Utils.s100TruncatedDateToLocalDate(Optional.ofNullable(ctx.getSource())
                                        .map(AidsToNavigationTypeImpl.class::cast)
                                        .map(AidsToNavigationTypeImpl::getPeriodicDateRange)
                                        .map(PeriodicDateRangeType::getDateEnd)
                                        .orElse(null)))
                                .map(src -> src, AidsToNavigation::setPeriodEnd);
                        mapper.using(ctx -> new GeometryS201Converter().convertToGeometry(((AidsToNavigationType) ctx.getSource())))
                                .map(src -> src, AidsToNavigation::setGeometry);
                    });

            // For structures, don't map the children
            if(atonType.isStructure()) {
                modelMapper.typeMap(atonType.getS201Class(), atonType.getLocalStructureClass())
                        .addMappings(mapper -> {
                            mapper.skip(StructureObject::setChildren);
                        });
            }

            // For equipment, don't map the parent
            if(atonType.isEquipment()) {
                modelMapper.typeMap(atonType.getS201Class(), atonType.getLocalEquipmentClass())
                        .addMappings(mapper -> {
                            mapper.skip(Equipment::setParent);
                        });
            }

            modelMapper.createTypeMap(atonType.getLocalClass(), atonType.getS201Class())
                    .implicitMappings()
                    .addMappings(mapper -> {
                        mapper.using(ctx -> "ID-ATON-" + ((AidsToNavigation) ctx.getSource()).getId())
                                .map(src -> src, AidsToNavigationType::setId);
                        mapper.when(ctx -> ctx.getDestinationType().equals(FixedDateRangeType.class))
                                .with(ctx -> new FixedDateRangeTypeImpl())
                                .using(ctx -> {
                                    final FixedDateRangeType fixedDateRangeType = new FixedDateRangeTypeImpl();
                                    fixedDateRangeType.setDateStart(S201Utils.localDateToS100TruncatedDate(((AidsToNavigation)ctx.getSource()).getDateStart()));
                                    fixedDateRangeType.setDateEnd(S201Utils.localDateToS100TruncatedDate(((AidsToNavigation)ctx.getSource()).getDateEnd()));
                                    return  fixedDateRangeType;
                                })
                                .map(src -> src, AidsToNavigationType::setFixedDateRange);
                        mapper.when(ctx -> ctx.getDestinationType().equals(PeriodicDateRangeType.class))
                                .with(ctx -> new PeriodicDateRangeTypeImpl())
                                .using(ctx -> {
                                    final PeriodicDateRangeType periodicDateRangeType = new PeriodicDateRangeTypeImpl();
                                    periodicDateRangeType.setDateStart(S201Utils.localDateToS100TruncatedDate(((AidsToNavigation)ctx.getSource()).getPeriodStart()));
                                    periodicDateRangeType.setDateEnd(S201Utils.localDateToS100TruncatedDate(((AidsToNavigation)ctx.getSource()).getPeriodEnd()));
                                    return  periodicDateRangeType;
                                })
                                .map(src -> src, AidsToNavigationType::setPeriodicDateRange);
                        mapper.using(ctx -> modelMapper.map(((AidsToNavigation)ctx.getSource()).getInformations(), new TypeToken<List<InformationTypeImpl>>() {}.getType()) )
                                .map(src -> src, AidsToNavigationTypeImpl::setInformations);
                        mapper.using(ctx -> modelMapper.map(((AidsToNavigation)ctx.getSource()).getFeatureNames(), new TypeToken<List<FeatureNameTypeImpl>>() {}.getType()) )
                                .map(src -> src, AidsToNavigationTypeImpl::setFeatureNames);
                        mapper.using(ctx -> new GeometryS201Converter().convertFromGeometry((AidsToNavigation) ctx.getSource()))
                                .map(src -> src, (dest, val) -> {
                                    try {
                                        new PropertyDescriptor("geometries", atonType.getS201Class()).getWriteMethod().invoke(dest, val);
                                    } catch (Exception ex) {
                                        log.error(ex.getMessage());
                                    }
                                });
                    });

            // For structures, map the children to reference types
            if(atonType.isStructure()) {
                modelMapper.typeMap(atonType.getLocalStructureClass(), atonType.getS201StructureClass())
                        .addMappings(mapper -> {
                            mapper.using(ctx -> new ReferenceTypeS201Converter().convertToReferenceTypes(((StructureObject)ctx.getSource()).getChildren(), ReferenceTypeRole.CHILD))
                                    .map(src-> src, StructureObjectTypeImpl::setchildren);
                        });
            }

            // For equipment, map the parent to single reference type
            if(atonType.isEquipment()) {
                modelMapper.typeMap(atonType.getLocalEquipmentClass(), atonType.getS201EquipmentClass())
                        .addMappings(mapper -> {
                            mapper.using(ctx -> new ReferenceTypeS201Converter().convertToReferenceType(((Equipment) ctx.getSource()).getParent(), ReferenceTypeRole.PARENT))
                                    .map(src-> src, EquipmentTypeImpl::setParent);
                        });
            }
        }

        // Create the Aggregation/Association type maps
        modelMapper.createTypeMap(AtonAggregationImpl.class, AtonAggregation.class)
                .implicitMappings()
                .addMappings(mapper -> {
                    mapper.skip(AtonAggregation::setId); // We don't know if the ID is correct so skip it
                    mapper.skip(AtonAggregation::setPeers);
                });
        modelMapper.createTypeMap(AtonAssociationImpl.class, AtonAssociation.class)
                .implicitMappings()
                .addMappings(mapper -> {
                    mapper.skip(AtonAssociation::setId); // We don't know if the ID is correct so skip it
                    mapper.skip(AtonAssociation::setPeers);
                });
        modelMapper.createTypeMap(AtonAggregation.class, AtonAggregationImpl.class)
                .implicitMappings()
                .addMappings(mapper -> {
                    mapper.using(ctx -> "ID-AGGR-" + ((AtonAggregation) ctx.getSource()).getId())
                            .map(src -> src, AtonAggregationImpl::setId);
                    mapper.using(ctx -> new ReferenceTypeS201Converter().convertToReferenceTypes(((AtonAggregation) ctx.getSource()).getPeers(), ReferenceTypeRole.AGGREGATION))
                            .map(src-> src, AtonAggregationImpl::setAtonAggregationBies);
                });
        modelMapper.createTypeMap(AtonAssociation.class, AtonAssociationImpl.class)
                .implicitMappings()
                .addMappings(mapper -> {
                    mapper.using(ctx -> "ID-ASSO-" + ((AtonAssociation) ctx.getSource()).getId())
                            .map(src -> src, AtonAssociationImpl::setId);
                    mapper.using(ctx -> new ReferenceTypeS201Converter().convertToReferenceTypes(((AtonAssociation) ctx.getSource()).getPeers(), ReferenceTypeRole.ASSOCIATION))
                            .map(src-> src, AtonAssociationImpl::setAtonAssociationBies);
                });

        // Create the Base Aids to Navigation type map for the DTOs
        modelMapper.createTypeMap(AidsToNavigation.class, AidsToNavigationDto.class)
                .implicitMappings()
                .addMappings(mapper -> {
                    mapper.using(ctx -> S201AtonTypes.fromLocalClass(((AidsToNavigation) ctx.getSource()).getClass()).getDescription())
                            .map(src -> src, AidsToNavigationDto::setAtonType);
                    mapper.using(ctx -> convertTos201DataSet(modelMapper, Collections.singletonList((AidsToNavigation) ctx.getSource())))
                            .map(src -> src, AidsToNavigationDto::setContent);
                });

        // Add the base to all Aids to Navigation DTO Mappings
        for(S201AtonTypes atonType : S201AtonTypes.values()) {
            // Skip the unknown type, we don't need it
            if(atonType == S201AtonTypes.UNKNOWN) {
                continue;
            }
            modelMapper.createTypeMap(atonType.getLocalClass(), AidsToNavigationDto.class)
                    .implicitMappings()
                    .includeBase(AidsToNavigation.class, AidsToNavigationDto.class);
        }

        // Now map the SECOM v1.0 Subscription Requests
        modelMapper.createTypeMap(org.grad.secom.core.models.SubscriptionRequestObject.class, SubscriptionRequest.class)
                .implicitMappings()
                .addMappings(mapper -> {
                    mapper.using(ctx -> SecomUtils.translateSecomContainerTypeEnum((org.grad.secom.core.models.enums.ContainerTypeEnum)ctx.getSource()))
                            .map(src -> src.getContainerType(), SubscriptionRequest::setContainerType);
                    mapper.using(ctx -> SecomUtils.translateSecomDataProductTypeEnum((org.grad.secom.core.models.enums.SECOM_DataProductType)ctx.getSource()))
                            .map(src -> src.getDataProductType(), SubscriptionRequest::setDataProductType);
                    mapper.using(ctx -> Optional.of(ctx)
                                    .map(MappingContext::getSource)
                                    .map(String.class::cast)
                                    .map(g -> {
                                        try {
                                            return WKTUtils.convertWKTtoGeometry(g);
                                        } catch (ParseException ex) {
                                            throw new ValidationException(Collections.singletonList(new ErrorMessage(ex.getMessage())));
                                        }
                                    })
                                    .orElse(null))
                            .map(src -> src.getGeometry(), SubscriptionRequest::setGeometry);
                });

        // Now map the SECOM v2.0 Subscription Requests - Use the envelope
        modelMapper.createTypeMap(org.grad.secomv2.core.models.EnvelopeSubscriptionObject.class, SubscriptionRequest.class)
                .implicitMappings()
                .addMappings(mapper -> {
                    mapper.using(ctx -> Optional.of(ctx)
                                    .map(MappingContext::getSource)
                                    .map(String.class::cast)
                                    .map(g -> {
                                        try {
                                            return WKTUtils.convertWKTtoGeometry(g);
                                        } catch (ParseException ex) {
                                            throw new ValidationException(Collections.singletonList(new ErrorMessage(ex.getMessage())));
                                        }
                                    })
                                    .orElse(null))
                            .map(src -> src.getGeometry(), SubscriptionRequest::setGeometry);
                });
        // ================================================================== //

        return modelMapper;
    }

    /**
     * Converts a whole list of Aids to Navigation objects into an XML string
     * representation conforming to the S-201 data product specification.
     *
     * @param atons the list of the Aids to Navigation objects
     * @return the respective S-201 data string representation
     */
    public static String convertTos201DataSet(ModelMapper modelMapper, List<AidsToNavigation> atons) {
        final S201DatasetBuilder s201DatasetBuilder = new S201DatasetBuilder(modelMapper);
        final String datasetTitle = CaseUtils.toCamelCase("AtoN Dataset for " + atons.stream()
                .map(AidsToNavigation::getIdCode)
                .collect(Collectors.joining(" ")), true, ' ');
        final S201Dataset s201Dataset = new S201Dataset(datasetTitle);
        final Dataset dataset = s201DatasetBuilder.packageToDataset(s201Dataset, atons);
        try {
            return S201Utils.marshalS201(dataset);
        } catch (JAXBException ex) {
            return "";
        }
    }

}
