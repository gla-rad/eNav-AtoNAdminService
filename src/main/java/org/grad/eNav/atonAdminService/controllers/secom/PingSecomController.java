package org.grad.eNav.atonAdminService.controllers.secom;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;
import org.grad.secom.core.interfaces.PingSecomInterface;
import org.grad.secom.core.models.PingResponseObject;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;


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
