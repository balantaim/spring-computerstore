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
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Slf4j
@Configuration
public class TimeZoneConfig {

    /**
     * Set the timezone of the Java application. This is important for date and time operations.
     */

    @Nullable
    @Value("${application.timezone:UTC}")
    private String applicationTimeZone;

    @PostConstruct
    public void init(){
        if (applicationTimeZone == null) {
            throw new RuntimeException("Application timezone cannot be null!");
        }
        TimeZone.setDefault(TimeZone.getTimeZone(applicationTimeZone));
        log.info("\n\tApplication time zone set to {}!",
                applicationTimeZone == null ? "UTC" : applicationTimeZone);
    }

}
