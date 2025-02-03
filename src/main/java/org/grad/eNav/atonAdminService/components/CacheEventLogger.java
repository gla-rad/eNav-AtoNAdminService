/*
 * Copyright (c) 2025 GLA Research and Development Directorate
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
package org.grad.eNav.atonAdminService.components;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

import lombok.extern.slf4j.Slf4j;


/**
 * Cache Event Logger Class
 *
 * @author Lawrence Hughes (email: Lawrence.Hughes@gla-rad.org)
 */
@Slf4j
public class CacheEventLogger implements CacheEventListener<Object, Object> {


    @Override
    public void onEvent(CacheEvent<? extends Object, ? extends Object> cacheEvent) {
        log.info("{}: key={}, old={}, new={}",
                cacheEvent.getType(),
                cacheEvent.getKey(),
                cacheEvent.getOldValue(),
                cacheEvent.getNewValue());
    }
}
