/*
 * Copyright 2025 Martin Atanasov.
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;


@Profile("prod")
@Slf4j
@RequiredArgsConstructor
@Configuration
public class DatasourceTimeZoneConfig {

    /**
     * Set the timezone of the datasource. This is important for date and time operations.
     */

    private final JdbcTemplate jdbcTemplate;
    @Value("${application.timezone:UTC}")
    private String applicationTimeZone;

    @PostConstruct
    public void init() {
        if (applicationTimeZone == null) {
            throw new RuntimeException("Application timezone cannot be null!");
        }
        jdbcTemplate.execute("SET time_zone = '" + applicationTimeZone + "'");
        log.info("\n\tDatasource time zone set to {}!", applicationTimeZone);
    }

}
