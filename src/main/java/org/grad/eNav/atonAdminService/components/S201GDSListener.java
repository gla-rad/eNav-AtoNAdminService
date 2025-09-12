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

package org.grad.eNav.atonAdminService.components;
import _int.iho.s_201.s_100.gml.profiles._5_2.AbstractGMLType;
import _int.iho.s_201.s_100.gml.profiles._5_2.ReferenceType;
import _int.iho.s_201.gml.cs0._2.AidsToNavigationType;
import _int.iho.s_201.gml.cs0._2.EquipmentType;
import _int.iho.s_201.gml.cs0._2.StructureObjectType;
import _int.iho.s_201.gml.cs0._2.impl.AtonAggregationImpl;
import _int.iho.s_201.gml.cs0._2.impl.AtonAssociationImpl;
import jakarta.annotation.PreDestroy;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.geotools.api.data.DataStore;
import org.geotools.api.data.FeatureEvent;
import org.geotools.api.data.FeatureListener;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.filter.FidFilterImpl;
import org.grad.eNav.atonAdminService.models.GeomesaData;
import org.grad.eNav.atonAdminService.models.GeomesaS201;
import org.grad.eNav.atonAdminService.models.domain.s201.*;
import org.grad.eNav.atonAdminService.models.dtos.S201Node;
import org.grad.eNav.atonAdminService.services.AidsToNavigationService;
import org.grad.eNav.atonAdminService.services.DatasetService;
import org.grad.eNav.atonAdminService.utils.GeometryJSONConverter;
import org.grad.eNav.atonAdminService.utils.GeometryUtils;
import org.grad.eNav.s201.utils.S201Utils;
import org.grad.secomv2.core.models.enums.SECOM_DataProductType;
import org.locationtech.geomesa.kafka.utils.KafkaFeatureEvent;
import org.locationtech.jts.geom.Geometry;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

/**
 * The AtoN Geomesa Data Store Listener Class
 *
 * This class defines the main operation of the AtoN listening operation
 * on the Geomesa Kafka Data Store.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class S201GDSListener implements FeatureListener {

    /**
     * The Model Mapper.
     */
    @Autowired
    ModelMapper modelMapper;

    /**
     * The Aids to Navigation Service.
     */
    @Autowired
    AidsToNavigationService aidsToNavigationService;

    /**
     * The Dataset Service.
     */
    @Autowired
    DatasetService datasetService;

    /**
     * The AtoN Information Channel to publish the published data to.
     */
    @Autowired
    @Qualifier("atonPublicationChannel")
    PublishSubscribeChannel atonPublicationChannel;

    /**
     * The AtoN Information Channel to publish the deleted data to.
     */
    @Autowired
    @Qualifier("atonDeletionChannel")
    PublishSubscribeChannel atonDeletionChannel;

    // Component Variables
    protected DataStore consumer;
    protected GeomesaData<S201Node> geomesaData;
    protected Geometry geometry;
    protected SimpleFeatureSource featureSource;

    /**
     * Once the listener has been initialised, it will create a consumer of
     * the data store provided and publish the incoming messages into the
     * AtoN data channel.
     *
     * @param consumer      The data store to consume the messages from
     */
    public void init(DataStore consumer,
                     GeomesaData<S201Node> geomesaData,
                     Geometry geometry) throws IOException {
        // Remember the input data
        this.consumer = consumer;
        this.geomesaData = geomesaData;
        this.geometry = geometry;

        // And add the feature listener to start reading
        this.featureSource = this.consumer.getFeatureSource(this.geomesaData.getTypeName());
        Optional.ofNullable(this.featureSource).ifPresent(fs -> fs.addFeatureListener(this));

        // Log an information message
        log.info(String.format("Initialised Geomesa Datastore Listener for area: %s",
                 GeometryJSONConverter.convertFromGeometry(geometry)));
    }

    /**
     * When shutting down the application we need to make sure that all
     * threads have been gracefully shutdown as well.
     */
    @PreDestroy
    public void destroy() {
        log.info("AtoN message Listener is shutting down...");
        this.featureSource.removeFeatureListener(this);
    }

    /**
     * The main data store listener operation where events are being handled.
     *
     * @param featureEvent      The feature event that took place
     */
    public void changed(FeatureEvent featureEvent) {
        // We are only interested in Kafka Feature Messages, otherwise don't bother
        if(!(featureEvent instanceof KafkaFeatureEvent)) {
            return;
        }

        // Combine all geometries to figure out the affected area
        Geometry affectedGeometry = null;

        // For feature additions/changes
        if (featureEvent.getType() == FeatureEvent.Type.CHANGED) {
            // Extract the S-201 information
            final List<S201Node> s201Nodes = Optional.of(featureEvent)
                    .filter(KafkaFeatureEvent.KafkaFeatureChanged.class::isInstance)
                    .map(KafkaFeatureEvent.KafkaFeatureChanged.class::cast)
                    .map(KafkaFeatureEvent.KafkaFeatureChanged::feature)
                    .filter(this.geomesaData.getSubsetFilter()::evaluate)
                    .map(Collections::singletonList)
                    .map(sl -> new GeomesaS201().retrieveData(sl))
                    .orElseGet(Collections::emptyList);

            // Parse and save the created/updated AtoN entries
            final List<? extends AidsToNavigation> listOfAtons = s201Nodes.stream()
                    .flatMap(this::parseS201Dataset)
                    .parallel()
                    .map(this.aidsToNavigationService::save)
                    .toList();

            // Publish the created/updated AtoN entries
            listOfAtons.stream()
                    .map(MessageBuilder::withPayload)
                    .map(builder -> builder.setHeader(MessageHeaders.CONTENT_TYPE, SECOM_DataProductType.S201))
                    .map(builder -> builder.setHeader("deletion", false))
                    .map(MessageBuilder::build)
                    .forEach(msg -> this.atonPublicationChannel.send(msg));

            // And now calculate the affected geometry of the changes
            affectedGeometry = listOfAtons.stream()
                    .map(AidsToNavigation::getGeometry)
                    .filter(Objects::nonNull)
                    .reduce(GeometryUtils::joinGeometries)
                    .orElse(null);
        }
        // For feature deletions,
        else if (featureEvent.getType() == FeatureEvent.Type.REMOVED) {
            /// Extract the S-201 message UIDs and use it to delete all referencing nodes
            final Set<String> idCodes = Optional.of(featureEvent)
                    .filter(KafkaFeatureEvent.KafkaFeatureRemoved.class::isInstance)
                    .map(KafkaFeatureEvent.KafkaFeatureRemoved.class::cast)
                    .map(KafkaFeatureEvent.KafkaFeatureRemoved::getFilter)
                    .filter(FidFilterImpl.class::isInstance)
                    .map(FidFilterImpl.class::cast)
                    .map(FidFilterImpl::getFidsSet)
                    .orElse(Collections.emptySet());

            // Now delete the selected AtoNs and collect the output
            final List<? extends AidsToNavigation> listOfAtons = idCodes.stream()
                    .map(this.aidsToNavigationService::findByIdCode)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(AidsToNavigation::getId)
                    .map(this.aidsToNavigationService::delete)
                    .toList();

            // Publish the deleted AtoN entries
            listOfAtons.stream()
                    .map(MessageBuilder::withPayload)
                    .map(builder -> builder.setHeader(MessageHeaders.CONTENT_TYPE, SECOM_DataProductType.S201))
                    .map(builder -> builder.setHeader("deletion", true))
                    .map(MessageBuilder::build)
                    .forEach(msg -> this.atonDeletionChannel.send(msg));

            // And now calculate the affected geometry of the changes
            affectedGeometry = listOfAtons.stream()
                    .map(AidsToNavigation::getGeometry)
                    .filter(Objects::nonNull)
                    .reduce(GeometryUtils::joinGeometries)
                    .orElse(null);
        }

        // Now we should update all datasets that are affected in this area.
        Optional.ofNullable(affectedGeometry)
                .map(geometry -> this.datasetService.findAll(null,
                        geometry,
                        null,
                        null,
                        Boolean.FALSE,
                        Pageable.unpaged()))
                .orElse(Page.empty())
                .stream()
                .map(S201Dataset::getUuid)
                .forEach(this.datasetService::requestDatasetContentUpdate);
    }

    /**
     * A helper that processes the S201Node entry provided and parses the
     * contained S-201 Aids to Navigation entries. Each entry might be
     * complex with a structure that contains multiple equipment entries, and
     * additional aggregation/association links to other objects which also
     * need to be referenced in the dataset. Therefore, we need to be able to
     * handle all the information and parse to the local data format.
     *
     * @param s201Node  the S-201 dataset node to be processed
     * @return the contained list of Aids to Navigation entries
     */
    protected Stream<? extends AidsToNavigation> parseS201Dataset(S201Node s201Node) {
        // Get the S-201 node content included members
        final List<? extends AbstractGMLType> members = Optional.of(s201Node)
                .map(S201Node::getContent)
                .map(xml -> {
                    try {
                        return S201Utils.getDatasetMembers(xml);
                    } catch (JAXBException ex) {
                        log.error(ex.getMessage());
                        return null;
                    }
                })
                .orElse(Collections.emptyList());

        // Map the individual types to local objects but with the original ID
        final Map<String, ? extends StructureObject> structureObjectTypeMap = members.stream()
                .filter(StructureObjectType.class::isInstance)
                .map(StructureObjectType.class::cast)
                .collect(Collectors.toMap(
                        StructureObjectType::getId,
                        aton -> (StructureObject) this.modelMapper.map(aton, S201AtonTypes.fromS201Class(aton.getClass()).getLocalClass())
                ));
        final Map<String, ? extends Equipment> equipmentTypeMap = members.stream()
                .filter(EquipmentType.class::isInstance)
                .map(EquipmentType.class::cast)
                .collect(Collectors.toMap(
                        EquipmentType::getId,
                        aton -> (Equipment) this.modelMapper.map(aton, S201AtonTypes.fromS201Class(aton.getClass()).getLocalClass())
                ));
        final Map<String, ? extends AidsToNavigation> otherAidsToNavigationMap = members.stream()
                .filter(AidsToNavigationType.class::isInstance)
                .map(AidsToNavigationType.class::cast)
                .filter(not(StructureObjectType.class::isInstance))
                .filter(not(EquipmentType.class::isInstance))
                .collect(Collectors.toMap(
                        AidsToNavigationType::getId,
                        aton -> this.modelMapper.map(aton, S201AtonTypes.fromS201Class(aton.getClass()).getLocalClass())
                ));

        // Combine all elements to a single map
        final Map<String, AidsToNavigation> combinedAidsToNavigationMap = new HashMap<>();
        combinedAidsToNavigationMap.putAll(structureObjectTypeMap);
        combinedAidsToNavigationMap.putAll(equipmentTypeMap);
        combinedAidsToNavigationMap.putAll(otherAidsToNavigationMap);

        // Now start building the links to the remaining objects (e.g. aggregations/associations)
        final Map<String, AtonAggregation> aggregationsMap = members.stream()
                .filter(AtonAggregationImpl.class::isInstance)
                .map(AtonAggregationImpl.class::cast)
                .collect(Collectors.toMap(
                        AtonAggregationImpl::getId,
                        aggr -> {
                            AtonAggregation result = this.modelMapper.map(aggr, AtonAggregation.class);
                            result.setPeers(aggr.getAtonAggregationBies()
                                    .stream()
                                    .map(this::getInternalReference)
                                    .filter(combinedAidsToNavigationMap::containsKey)
                                    .map(combinedAidsToNavigationMap::get)
                                    .collect(Collectors.toSet()));
                            return result;
                        }
                ));
        final Map<String, AtonAssociation> associationsMap = members.stream()
                .filter(AtonAssociationImpl.class::isInstance)
                .map(AtonAssociationImpl.class::cast)
                .collect(Collectors.toMap(
                        AtonAssociationImpl::getId,
                        asso -> {
                            AtonAssociation result = this.modelMapper.map(asso, AtonAssociation.class);
                            result.setPeers(asso.getAtonAssociationBies()
                                    .stream()
                                    .map(this::getInternalReference)
                                    .filter(combinedAidsToNavigationMap::containsKey)
                                    .map(combinedAidsToNavigationMap::get)
                                    .collect(Collectors.toSet()));
                            return result;
                        }
                ));

        // Now map the S-201 structure objects and add all the associated information
        try {
            for (AbstractGMLType member : members) {
                //Sanity Check
                if(Objects.isNull(member)) {
                    continue;
                }
                // Handle structure members
                if (member instanceof StructureObjectType structure) {
                    Optional.of(structure)
                            .map(StructureObjectType::getchildren)
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(this::getInternalReference)
                            .filter(equipmentTypeMap::containsKey)
                            .map(equipmentTypeMap::get)
                            .forEach(e -> e.setParent(structureObjectTypeMap.get(structure.getId())));
                }
                // Handle equipment members
                if (member instanceof EquipmentType equipment) {
                    Optional.of(equipment)
                            .map(EquipmentType::getParent)
                            .map(this::getInternalReference)
                            .filter(structureObjectTypeMap::containsKey)
                            .map(structureObjectTypeMap::get)
                            .map(StructureObject::getChildren)
                            .ifPresent(l -> l.add(equipmentTypeMap.get(equipment.getId())));
                }
                // Handle aggregation members
                if (member instanceof AtonAggregationImpl aggregation) {
                    aggregation.getAtonAggregationBies()
                            .stream()
                            .map(this::getInternalReference)
                            .filter(combinedAidsToNavigationMap::containsKey)
                            .map(combinedAidsToNavigationMap::get)
                            .map(AidsToNavigation::getAggregations)
                            .forEach(aggregations -> aggregations.add(aggregationsMap.get(aggregation.getId())));
                }
                // Handle association members
                if (member instanceof AtonAssociationImpl association) {
                    association.getAtonAssociationBies()
                            .stream()
                            .map(this::getInternalReference)
                            .filter(combinedAidsToNavigationMap::containsKey)
                            .map(combinedAidsToNavigationMap::get)
                            .map(AidsToNavigation::getAssociations)
                            .forEach(associations -> associations.add(associationsMap.get(association.getId())));
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        // And now return the combined populated data
        return Stream.of(
                structureObjectTypeMap.values().stream(),
                equipmentTypeMap.values().stream(),
                otherAidsToNavigationMap.values().stream()
        ).flatMap(i -> i);
    }

    /**
     * Internal references in S-100 datasets points to an included feature
     * using its ID with a hash ('#') prefix. This prefix should be removed
     * to get the actual ID value. This small utility function performs this
     * exact operation.
     *
     * @param referenceType     The reference type object
     * @return the href of the reference without the hash ('#') prefix
     */
    protected String getInternalReference(ReferenceType referenceType) {
        return Optional.ofNullable(referenceType)
                .map(ReferenceType::getHref)
                .map(r -> r.replaceFirst("#",""))
                .orElse(null);
    }

}
