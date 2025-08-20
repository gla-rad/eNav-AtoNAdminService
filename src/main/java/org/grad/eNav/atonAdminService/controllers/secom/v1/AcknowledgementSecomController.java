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
package org.grad.eNav.atonAdminService.controllers.secom.v1;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;
import org.grad.secom.core.exceptions.SecomInvalidCertificateException;
import org.grad.secom.core.exceptions.SecomSignatureVerificationException;
import org.grad.secom.core.exceptions.SecomValidationException;
import org.grad.secom.core.interfaces.AcknowledgementSecomInterface;
import org.grad.secom.core.models.AcknowledgementObject;
import org.grad.secom.core.models.AcknowledgementResponseObject;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;
import java.util.Optional;

/**
 * The SECOM Acknowledgement Interface Controller.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Component
@Path("/")
@Validated
@Slf4j
public class AcknowledgementSecomController implements AcknowledgementSecomInterface {

    /**
     * POST /v1/acknowledgement : During upload of information, an
     * acknowledgement can be requested which is expected to be received when
     * the uploaded message has been delivered to the end system (technical
     * acknowledgement), and an acknowledgement when the message has been opened
     * (read) by the end user (operational acknowledgement). The acknowledgement
     * contains a reference to object delivered.
     *
     * @param acknowledgementObject  the acknowledgement object
     * @return the acknowledgement response object
     */
    @Tag(name = "SECOM")
    public AcknowledgementResponseObject acknowledgment(@Valid AcknowledgementObject acknowledgementObject) {
        log.debug("SECOM acknowledgement received");

        // Handle errors - dummy field check
        if(Objects.isNull(acknowledgementObject) || Objects.isNull(acknowledgementObject.getEnvelope()) || Objects.isNull(acknowledgementObject.getEnvelope().getTransactionIdentifier())) {
            throw new SecomValidationException("No valid transaction identifier provided");
        } else if( Objects.isNull(acknowledgementObject.getEnvelopeSignature())) {
            throw new SecomSignatureVerificationException("No valid signature provided");
        } else if( Objects.isNull(acknowledgementObject.getEnvelope().getEnvelopeSignatureCertificate())) {
            throw new SecomInvalidCertificateException("No valid certificate provided");
        }

        // Process the incoming request - for now just log
        // TODO: We need to actually check for active transactions and log acks
        Optional.ofNullable(acknowledgementObject)
                .map(AcknowledgementObject::getEnvelope)
                .ifPresent(e -> log.debug("Acknowledgement of type {} for transaction {} received at {}",
                        e.getAckType(), e.getTransactionIdentifier(), e.getCreatedAt()));

        // Create the response
        AcknowledgementResponseObject acknowledgementResponseObject = new AcknowledgementResponseObject();
        acknowledgementResponseObject.setMessage(String.format("Successfully received ACK for %s", acknowledgementObject.getEnvelope().getTransactionIdentifier()));
        acknowledgementResponseObject.setSECOM_ResponseCode(null);

        // Return the response
        return acknowledgementResponseObject;
    }

}
