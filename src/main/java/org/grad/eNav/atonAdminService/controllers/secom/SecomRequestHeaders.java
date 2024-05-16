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
package org.grad.eNav.atonAdminService.controllers.secom;

/**
 * The SECOM Request Headers Class.
 *
 * The API Gateway will propagate the SECOM requests with certain headers
 * populated to inform us of the authorised client information. These can
 * be utilised while handling the incoming requests.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class SecomRequestHeaders {

    /**
     * The SECOM Certificate Header.
     */
    public static final String CERT_HEADER = "X-SECOM-CERT";

    /**
     * The SECOM MRN Header.
     */
    public static final String MRN_HEADER = "X-SECOM-MRN";

}
