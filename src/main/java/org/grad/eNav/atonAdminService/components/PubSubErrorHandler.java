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

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Simple error handler for publish-subscribe channel.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Component
@Slf4j
public class PubSubErrorHandler implements ErrorHandler {

    // Class Variables
    protected Queue<Throwable> handledErrors = new LinkedList<>();

    /**
     * Handing the errors by just logging them and adding them into the
     * handle errors queue.
     *
     * @param throwable The error thrown that needs to be handled
     */
    @Override
    public void handleError(Throwable throwable) {
        log.error(throwable.getMessage());
        handledErrors.add(throwable);
    }

    /**
     * Returns the handle errors queue so that is can be reviewed by other
     * components.
     *
     * @return The handle errors queue
     */
    public Queue<Throwable> getHandlerErrors() {
        return this.handledErrors;
    }

}
