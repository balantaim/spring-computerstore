/*
 * Copyright 2024 Martin Atanasov.
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

package com.martinatanasov.computerstore.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Profile(value = "prod")
@Component
public class BotDetectionFilter extends OncePerRequestFilter {

    private static final Set<String> BLOCKED_USER_AGENTS = Set.of(
            "electron", "webdriver", "phantomjs", "puppeteer", "postman", "insomnia"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null && BLOCKED_USER_AGENTS.stream().anyMatch(userAgent.toLowerCase()::contains)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }
        filterChain.doFilter(request, response);
    }

}