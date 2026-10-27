/*
 * Copyright 2025-2026 Martin Atanasov.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.martinatanasov.computerstore.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Slf4j
@Configuration
class TimeZoneConfig {

    /**
     * Set the timezone of the Java application. This is important for date and time operations.
     */
    private final String APPLICATION_TIME_ZONE;

    TimeZoneConfig(@Value("${application.timezone:UTC}") String applicationTimeZone) {
        this.APPLICATION_TIME_ZONE = applicationTimeZone;
    }

    @PostConstruct
    public void init(){
        if (APPLICATION_TIME_ZONE.isEmpty()) {
            throw new RuntimeException("Application timezone is not set!");
        }
        TimeZone.setDefault(TimeZone.getTimeZone(APPLICATION_TIME_ZONE));
        log.info("\n\tApplication time zone set to {}!", APPLICATION_TIME_ZONE);
    }

}
