/*
* Copyright (c) 2024 GLA Research and Development Directorate
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.grad.eNav.atonAdminService.controllers.secom.v1;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;
import org.grad.secom.core.interfaces.PingSecomInterface;
import org.grad.secom.core.models.PingResponseObject;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;

/**
 * The SECOM v1 Ping Interface Controller.
 *
 * @author Lawrence Hughes (email: Lawrence.Hughes@gla-rad.org)
 */
@Component
@Path("/")
@Validated
@Slf4j
public class PingSecomController implements PingSecomInterface  {

    /**
     * GET /v1/ping : returns a PingResponseObject which
     * holds the last private interaction time
     */
    @Tag(name = "SECOM")
    @Override
    public PingResponseObject ping() {

        PingResponseObject pingResponseObject = new PingResponseObject();
        pingResponseObject.setLastPrivateInteractionTime(Instant.now());
        log.debug("PingSecomController called");
        return pingResponseObject;

    }
}
