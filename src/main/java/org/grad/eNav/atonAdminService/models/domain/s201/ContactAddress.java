/*
 * Copyright (c) 2024 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grad.eNav.atonAdminService.models.domain.s201;

import jakarta.persistence.*;

import java.math.BigInteger;
import java.util.List;

/**
 * The S-201 Contact Address Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Contact
 * Address.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_201.gml.cs0._2.ContactAddressType
 */
@Entity
public class ContactAddress {

    // Class Variables
    // Class Variables
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_address_generator")
    @SequenceGenerator(name="contact_address_generator", sequenceName = "contact_address_seq", allocationSize=1)
    private BigInteger id;

    private String administrativeDivision;

    private String cityName;

    private String country;

    @ElementCollection
    private List<String> deliveryPoints;

    private String postalCode;

    /**
     * Gets id.
     *
     * @return the id
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(BigInteger id) {
        this.id = id;
    }

    /**
     * Gets administrative division.
     *
     * @return the administrative division
     */
    public String getAdministrativeDivision() {
        return administrativeDivision;
    }

    /**
     * Sets administrative division.
     *
     * @param administrativeDivision the administrative division
     */
    public void setAdministrativeDivision(String administrativeDivision) {
        this.administrativeDivision = administrativeDivision;
    }

    /**
     * Gets city name.
     *
     * @return the city name
     */
    public String getCityName() {
        return cityName;
    }

    /**
     * Sets city name.
     *
     * @param cityName the city name
     */
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    /**
     * Gets country.
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets country.
     *
     * @param country the country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets delivery points.
     *
     * @return the delivery points
     */
    public List<String> getDeliveryPoints() {
        return deliveryPoints;
    }

    /**
     * Sets delivery points.
     *
     * @param deliveryPoints the delivery points
     */
    public void setDeliveryPoints(List<String> deliveryPoints) {
        this.deliveryPoints = deliveryPoints;
    }

    /**
     * Gets postal code.
     *
     * @return the postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets postal code.
     *
     * @param postalCode the postal code
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
