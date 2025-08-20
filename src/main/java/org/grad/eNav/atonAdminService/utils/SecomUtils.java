/*
 * Copyright (c) 2025 GLA Research and Development Directorate
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

package org.grad.eNav.atonAdminService.utils;

/**
 * The SECOM Utils Class
 * <p/>
 * This class involved certain facilities to facilitate certain SECOM
 * operations, especially in translating between versions.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class SecomUtils {

    /**
     * A simple static function that translated the SECOM container type
     * enumerations between versions 1 and 2.
     *
     * @param containerType the SECOM v1 container type enumerations
     * @return the SECOM v2 container type enumerations
     */
    public static org.grad.secomv2.core.models.enums.ContainerTypeEnum translateSecomContainerTypeEnum(org.grad.secom.core.models.enums.ContainerTypeEnum containerType) {
        if(containerType == null) {
            return null;
        }
        return org.grad.secomv2.core.models.enums.ContainerTypeEnum.fromValue(containerType.getValue());
    }

    /**
     * A simple static function that translated the SECOM data product type
     * enumerations between versions 1 and 2.
     *
     * @param dataProductType the SECOM v1 data product type enumerations
     * @return the SECOM v2 data product type enumerations
     */
    public static org.grad.secomv2.core.models.enums.SECOM_DataProductType translateSecomDataProductTypeEnum(org.grad.secom.core.models.enums.SECOM_DataProductType dataProductType) {
        if(dataProductType == null) {
            return null;
        }
        return org.grad.secomv2.core.models.enums.SECOM_DataProductType.fromDescription(dataProductType.getDescription());
    }

}
