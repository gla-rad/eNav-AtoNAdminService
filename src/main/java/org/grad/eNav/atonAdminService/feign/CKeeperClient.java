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

package org.grad.eNav.atonAdminService.feign;

import feign.Response;
import org.grad.eNav.atonAdminService.config.FeignClientConfig;
import org.grad.eNav.atonAdminService.models.dtos.SignatureCertificateDto;
import org.grad.eNav.atonAdminService.models.dtos.SignatureVerificationRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

/**
 * The Feign Interface For the CKeeper Client.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Component
@FeignClient(name = "ckeeper", configuration = FeignClientConfig.class)
public interface CKeeperClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/signature/certificate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    SignatureCertificateDto getSignatureCertificate(@RequestParam("entityName") String entityName,
                                                    @RequestParam(value = "version", required = false) String version,
                                                    @RequestParam(value = "mmsi", required = false) String mmsi,
                                                    @RequestParam(value = "entityType", required = false) String entityType);

    @RequestMapping(method = RequestMethod.POST, value = "/api/signature/certificate/{certificateId}", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    Response generateCertificateSignature(@PathVariable BigInteger certificateId,
                                          @RequestParam(value="algorithm", required = false) String algorithm,
                                          @RequestBody byte[] payload);

    @RequestMapping(method = RequestMethod.POST, value = "/api/signature/entity/verify/{entityName}",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Response verifyEntitySignature(@PathVariable String entityName,
                                   @RequestBody SignatureVerificationRequestDto signatureVerificationRequestDto);

}
