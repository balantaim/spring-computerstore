/*
 * Copyright 2024-2025 Martin Atanasov.
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

import com.martinatanasov.computerstore.security.filters.BotDetectionFilter;
import com.martinatanasov.computerstore.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class GlobalSecurityConfig {

    //bcrypt bean definition
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //authenticationProvider bean definition
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider(userService);
        auth.setPasswordEncoder(passwordEncoder()); //set the password encoder - bcrypt
        return auth;
    }

    private static final String getContentSecurityPolicyAsString = "default-src 'none'; " +
            "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
            "form-action 'self' https://checkout.stripe.com; " +
            "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
            "connect-src 'self' https://cdn.jsdelivr.net; " +
            "img-src 'self' https://img.icons8.com https://ardes.bg https://preview.redd.it data: ; " +
            "manifest-src 'self'; " +
            "font-src 'self' https://cdnjs.cloudflare.com data: https://cdn.jsdelivr.net; " +
            "base-uri 'self'; " +
            "child-src 'none'; " +
            "frame-ancestors 'none'";

    @Bean
    @Order(1)
    SecurityFilterChain actuatorFilterChain(HttpSecurity http,
                                            @Value("${management.endpoints.web.base-path}") String actuatorBasePath) throws Exception {
        return http
                // Use current filter chain only specific paths
                .securityMatcher(actuatorBasePath + "/**")
                .authorizeHttpRequests(config -> config
                        //EndpointRequest manage the actuator endpoint
                        .requestMatchers(HttpMethod.GET, actuatorBasePath).permitAll()
                        .requestMatchers(EndpointRequest.to("info")).permitAll()
                        .requestMatchers(EndpointRequest.to("health", "metrics", "scheduledtasks")).hasRole("ADMIN")
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain staticAssetsFilterChain(HttpSecurity http) throws Exception {
        final String[] staticResources = {"/favicon.ico", "/other/**", "/css/**", "/images/**", "/js/**", "/robots.txt"};
        return http
                // Use current filter chain only specific paths
                .securityMatcher(staticResources)
                .authorizeHttpRequests(config -> config
                        .requestMatchers(HttpMethod.GET, staticResources).permitAll()
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    @Order(3)
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            AuthenticationSuccessHandler customAuthenticationSuccessHandler,
                                            Environment environment) throws Exception {
        http
                .authorizeHttpRequests(config -> config
                        .requestMatchers("/Profile/**").hasAnyRole("CUSTOMER", "MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/Products/**").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.PUT, "/Products/**").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.DELETE, "/Products/**").hasRole("CUSTOMER")
                        .requestMatchers("/Cart-items/**").hasRole("CUSTOMER")
                        //Permit all on GET request for static content
                        .requestMatchers(HttpMethod.GET, "/", "/Products/**", "/About", "/Search", "/Live-search", "/error/**", "/403").permitAll()
                        .requestMatchers("/Login", "/register/**", "/authenticateTheUser").permitAll()
                        //Stripe webhook endpoint
                        .requestMatchers(HttpMethod.POST, "/Status/**").permitAll()
                        //Authenticate any request that is not specified in the filter chain
                        .anyRequest().authenticated()
                )
                //Setup permission by role and methods
                .headers(headers -> headers
                        //Enable Iframe for the same origin (default value is disabled)
                        // .frameOptions(frameOptions -> frameOptions.sameOrigin())
                        //Block XSS attacks
                        .xssProtection(xxs -> xxs.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                        //Block form data from unknown origin
                        //If you want to use script inside the body use 'unsafe-inline', this will add you a new vulnerability
                        .contentSecurityPolicy(contentSecurityPolicyConfig -> contentSecurityPolicyConfig
                                .policyDirectives(getContentSecurityPolicyAsString)
                        )
                )
                .formLogin(form -> form
                        //Redirect to login form if no authorisation
                        .loginPage("/Login")
                        //.defaultSuccessUrl("/Profile", true)
                        //Login method used in the html
                        .loginProcessingUrl("/authenticateTheUser")
                        .failureUrl("/Login?error=true")
                        .successHandler(customAuthenticationSuccessHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/Login?logout=true")
                        .permitAll()
                )
                //Disable csrf webhook endpoint for Stripe
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                // Payments
                                "/Status/payment-complete", "/Checkout/step-3")
                )
                .exceptionHandling(config -> config
                        .accessDeniedPage("/access-denied")
                )
                .httpBasic(AbstractHttpConfigurer::disable);

        if (isTestProfile(environment)) {
            //Disable Cross Site Request Forgery (CSRF)
            http.csrf(AbstractHttpConfigurer::disable);
        } else {
            //Add filter for Bot protection (prod environment)
            http.addFilterBefore(new BotDetectionFilter(), UsernamePasswordAuthenticationFilter.class);
        }

        return http.build();
    }

    private boolean isTestProfile(final Environment environment) {
        return Arrays.asList(environment.getActiveProfiles()).contains("test") ||
                Arrays.asList(environment.getActiveProfiles()).contains("benc");
    }

}
