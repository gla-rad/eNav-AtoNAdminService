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

package org.grad.eNav.atonAdminService.models.dtos.secom;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.grad.eNav.atonAdminService.utils.*;
import org.grad.secomv2.core.models.enums.ContainerTypeEnum;
import org.grad.secomv2.core.models.enums.SECOM_DataProductType;
import org.locationtech.jts.geom.Geometry;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The SECOM Subscription Request DTO.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class SubscriptionRequestDto {

    // Class Variables
    private UUID uuid;
    private ContainerTypeEnum containerType;
    private SECOM_DataProductType dataProductType;
    private String productVersion;
    private UUID dataReference;
    @JsonSerialize(using = GeometryJSONSerializer.class)
    @JsonDeserialize(using = GeometryJSONDeserializer.class)
    private Geometry geometry;
    private String unlocode;
    private LocalDateTime subscriptionPeriodStart;
    private LocalDateTime subscriptionPeriodEnd;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @JsonSerialize(using = GeometryJSONSerializer.class)
    @JsonDeserialize(using = GeometryJSONDeserializer.class)
    private Geometry subscriptionGeometry;
    private String clientMrn;
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
}
