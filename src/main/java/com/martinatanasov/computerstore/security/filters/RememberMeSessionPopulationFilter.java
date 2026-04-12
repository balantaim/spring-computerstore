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

package com.martinatanasov.computerstore.security.filters;

import com.martinatanasov.computerstore.services.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class RememberMeSessionPopulationFilter extends OncePerRequestFilter {

    private final SessionService sessionService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isRememberMeAuthentication(auth) && !isSessionPopulated(request)) {
            log.trace("\n\tPopulate the session after remember-me is used");
            populateSession(request, auth);
        }
        filterChain.doFilter(request, response);
    }

    private boolean isRememberMeAuthentication(Authentication authentication) {
        return authentication instanceof RememberMeAuthenticationToken && authentication.isAuthenticated();
    }

    private boolean isSessionPopulated(HttpServletRequest request) {
        // Get the current session or return null
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }

    private void populateSession(HttpServletRequest request, Authentication authentication) {
        // Create a new session
        HttpSession session = request.getSession(true);
        // The session is initialized after RememberMeAuthenticationFilter if no previous session
        sessionService.initializeUserSession(authentication, session);
    }

}