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

package org.grad.eNav.atonAdminService.models.domain.secom;

import jakarta.persistence.*;
import org.grad.eNav.atonAdminService.models.UnLoCodeMapEntry;
import org.grad.eNav.atonAdminService.services.UnLoCodeService;
import org.grad.eNav.atonAdminService.utils.GeometryBinder;
import org.grad.eNav.atonAdminService.utils.GeometryUtils;
import org.grad.eNav.atonAdminService.utils.NullValueIndexerBridge;
import org.grad.secomv2.core.models.enums.ContainerTypeEnum;
import org.grad.secomv2.core.models.enums.SECOM_DataProductType;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBinderRef;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.common.annotation.Param;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.NonStandardField;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * The SECOM Subscription Request Domain Entity.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Cacheable
@Indexed
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SubscriptionRequest {

    // Class Variables
    @Id
    @GenericField(sortable = Sortable.YES)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter (
                            name = "uuid_gen_strategy_class",
                            value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
                    )
            }
    )
    @Column(columnDefinition="uuid", unique = true, updatable = false, nullable = false)
    private UUID uuid;

    @KeywordField(sortable = Sortable.YES,
                  valueBridge = @ValueBridgeRef(type = NullValueIndexerBridge.class))
    @Enumerated(EnumType.STRING)
    private ContainerTypeEnum containerType;

    @KeywordField(sortable = Sortable.YES,
                  valueBridge = @ValueBridgeRef(type = NullValueIndexerBridge.class))
    @Enumerated(EnumType.STRING)
    private SECOM_DataProductType dataProductType;

    @KeywordField(sortable = Sortable.YES,
                  valueBridge = @ValueBridgeRef(type = NullValueIndexerBridge.class))
    private String productVersion;

    @GenericField(sortable = Sortable.YES,
                  valueBridge = @ValueBridgeRef(type = NullValueIndexerBridge.class))
    @Column(columnDefinition="uuid")
    private UUID dataReference;

    private Geometry geometry;

    private String unlocode;

    @GenericField(indexNullAs = "1970-01-01T00:00:00")
    private LocalDateTime subscriptionPeriodStart;

    @GenericField(indexNullAs = "9999-01-01T00:00:00")
    private LocalDateTime subscriptionPeriodEnd;

    @CreatedDate
    @GenericField(sortable = Sortable.YES)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @GenericField(sortable = Sortable.YES)
    private LocalDateTime updatedAt;

    @NonStandardField(name="subscriptionGeometry", valueBinder = @ValueBinderRef(
            type = GeometryBinder.class,
            params = @Param(name="fieldName", value = "subscriptionGeometry")
    ))
    private Geometry subscriptionGeometry;

    @KeywordField(sortable = Sortable.YES,
                  valueBridge = @ValueBridgeRef(type = NullValueIndexerBridge.class))
    private String clientMrn;

    @KeywordField(sortable = Sortable.YES,
            valueBridge = @ValueBridgeRef(type = NullValueIndexerBridge.class))
    private URL callbackEndpoint;

    private Boolean pushAll;

    /**
     * Gets uuid.
     *
     * @return the uuid
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Sets uuid.
     *
     * @param uuid the uuid
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets container type.
     *
     * @return the container type
     */
    public ContainerTypeEnum getContainerType() {
        return containerType;
    }

    /**
     * Sets container type.
     *
     * @param containerType the container type
     */
    public void setContainerType(ContainerTypeEnum containerType) {
        this.containerType = containerType;
    }

    /**
     * Gets data product type.
     *
     * @return the data product type
     */
    public SECOM_DataProductType getDataProductType() {
        return dataProductType;
    }

    /**
     * Sets data product type.
     *
     * @param dataProductType the data product type
     */
    public void setDataProductType(SECOM_DataProductType dataProductType) {
        this.dataProductType = dataProductType;
    }

    /**
     * Gets product version.
     *
     * @return the product version
     */
    public String getProductVersion() {
        return productVersion;
    }

    /**
     * Sets product version.
     *
     * @param productVersion the product version
     */
    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    /**
     * Gets data reference.
     *
     * @return the data reference
     */
    public UUID getDataReference() {
        return dataReference;
    }

    /**
     * Sets data reference.
     *
     * @param dataReference the data reference
     */
    public void setDataReference(UUID dataReference) {
        this.dataReference = dataReference;
    }

    /**
     * Gets geometry.
     *
     * @return the geometry
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * Sets geometry.
     *
     * @param geometry the geometry
     */
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    /**
     * Gets unlocode.
     *
     * @return the unlocode
     */
    public String getUnlocode() {
        return unlocode;
    }

    /**
     * Sets unlocode.
     *
     * @param unlocode the unlocode
     */
    public void setUnlocode(String unlocode) {
        this.unlocode = unlocode;
    }

    /**
     * Gets subscription period start.
     *
     * @return the subscription period start
     */
    public LocalDateTime getSubscriptionPeriodStart() {
        return subscriptionPeriodStart;
    }

    /**
     * Sets subscription period start.
     *
     * @param subscriptionPeriodStart the subscription period start
     */
    public void setSubscriptionPeriodStart(LocalDateTime subscriptionPeriodStart) {
        this.subscriptionPeriodStart = subscriptionPeriodStart;
    }

    /**
     * Gets subscription period end.
     *
     * @return the subscription period end
     */
    public LocalDateTime getSubscriptionPeriodEnd() {
        return subscriptionPeriodEnd;
    }

    /**
     * Sets subscription period end.
     *
     * @param subscriptionPeriodEnd the subscription period end
     */
    public void setSubscriptionPeriodEnd(LocalDateTime subscriptionPeriodEnd) {
        this.subscriptionPeriodEnd = subscriptionPeriodEnd;
    }

    /**
     * Gets created at.
     *
     * @return the created at
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets created at.
     *
     * @param createdAt the created at
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets updated at.
     *
     * @return the updated at
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets updated at.
     *
     * @param updatedAt the updated at
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets subscription geometry.
     *
     * @return the subscription geometry
     */
    public Geometry getSubscriptionGeometry() {
        return subscriptionGeometry;
    }

    /**
     * Sets subscription geometry.
     *
     * @param subscriptionGeometry the subscription geometry
     */
    public void setSubscriptionGeometry(Geometry subscriptionGeometry) {
        this.subscriptionGeometry = subscriptionGeometry;
    }

    /**
     * Gets client mrn.
     *
     * @return the client mrn
     */
    public String getClientMrn() {
        return clientMrn;
    }

    /**
     * Sets client mrn.
     *
     * @param clientMrn the client mrn
     */
    public void setClientMrn(String clientMrn) {
        this.clientMrn = clientMrn;
    }

    /**
     * Gets callback endpoint.
     *
     * @return the callback endpoint
     */
    public URL getCallbackEndpoint() {
        return callbackEndpoint;
    }

    /**
     * Sets callback endpoint.
     *
     * @param callbackEndpoint the callback endpoint
     */
    public void setCallbackEndpoint(URL callbackEndpoint) {
        this.callbackEndpoint = callbackEndpoint;
    }

    /**
     * Gets push all.
     *
     * @return the push all
     */
    public Boolean getPushAll() {
        return pushAll;
    }

    /**
     * Sets push all.
     *
     * @param pushAll the push all
     */
    public void setPushAll(Boolean pushAll) {
        this.pushAll = pushAll;
    }

    /**
     * This utility function will update the general subscription geometry based
     * on all various geometry fields. Note that for the UN/LoCode we will need
     * access to the UnLoCode Service.
     *
     * @param unLoCodeService the UnLoCode Service to access the UN/LoCode data from
     */
    public void updateSubscriptionGeometry(UnLoCodeService unLoCodeService) {
        Optional.ofNullable(GeometryUtils.joinGeometries(
                        this.getGeometry(),
                        Optional.ofNullable(this.getUnlocode())
                                .map(unLoCodeService::getUnLoCodeMapEntry)
                                .map(UnLoCodeMapEntry::getGeometry)
                                .orElse(null),
                        new GeometryFactory(new PrecisionModel(), 4326).createPolygon(new Coordinate[]{
                                new Coordinate(-90, -180),
                                new Coordinate(90, -180),
                                new Coordinate(90, 180),
                                new Coordinate(-90, 180),
                                new Coordinate(-90, -180),
                        }) // Add the whole world if nothing else is there
                ))
                .ifPresent(this::setSubscriptionGeometry);
    }
}
