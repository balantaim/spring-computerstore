/*
 * Copyright 2026 Martin Atanasov.
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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Configuration
class DateTimeTemplateConfig implements WebMvcConfigurer {

    /**
     * This is configuration for Template's date formatter
     */

    private final ZoneId DISPLAY_ZONE;
    private final DateTimeFormatter FORMAT;

    DateTimeTemplateConfig(@Value("${application.display-timezone}") String displayZone) {
        this.DISPLAY_ZONE = ZoneId.of(displayZone);
        this.FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // LocalDateTime fields that hold UTC values
        registry.addConverter(LocalDateTime.class, String.class,
                utc -> utc.atOffset(ZoneOffset.UTC).atZoneSameInstant(DISPLAY_ZONE).format(FORMAT));
        // Instant fields
        registry.addConverter(Instant.class, String.class,
                instant -> instant.atZone(DISPLAY_ZONE).format(FORMAT));
    }

}
