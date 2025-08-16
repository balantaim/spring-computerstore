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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@RequiredArgsConstructor
public class GlobalSecurityConfig {

    //Get Active Profile
    @Autowired
    private Environment environment;

    private static final String getContentSecurityPolicyAsString = "default-src 'none'; " +
                "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
                "form-action 'self' https://checkout.stripe.com; " +
                "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
                "connect-src 'self'; " +
                "img-src 'self' https://img.icons8.com https://ardes.bg https://preview.redd.it data: ; " +
                "manifest-src 'self'; " +
                "font-src 'self' https://cdnjs.cloudflare.com data: https://cdn.jsdelivr.net; " +
                "base-uri 'self'; " +
                "child-src 'none'; " +
                "frame-ancestors 'none'";


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

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http,
                                    AuthenticationSuccessHandler customAuthenticationSuccessHandler,
                                    @Value("${management.endpoints.web.base-path}") String actuatorBasePath) throws Exception {
        //Setup permission by role and methods
        http.headers(headers -> headers
                        //Enable Iframe for the same origin (default value is disabled)
                        // .frameOptions(frameOptions -> frameOptions.sameOrigin()
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
                        .ignoringRequestMatchers("/Status/payment-complete")
                )
                .exceptionHandling(config -> config
                        .accessDeniedPage("/access-denied")
                );

        if (isTestProfile()) {
            //Disable Cross Site Request Forgery (CSRF)
            http.csrf(AbstractHttpConfigurer::disable);
        } else {
            //Add filter for Bot protection (prod environment)
            http.addFilterBefore(new BotDetectionFilter(), UsernamePasswordAuthenticationFilter.class);
        }

        http.authorizeHttpRequests(config -> config
                //EndpointRequest manage the actuator endpoint
                .requestMatchers(HttpMethod.GET, actuatorBasePath).permitAll()
                .requestMatchers(EndpointRequest.to("info")).permitAll()
                .requestMatchers(EndpointRequest.to("health", "metrics", "scheduledtasks")).hasRole("ADMIN")
                .requestMatchers("/Profile/**").hasAnyRole("CUSTOMER", "MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/Products/**").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.PUT, "/Products/**").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.DELETE, "/Products/**").hasRole("CUSTOMER")
                .requestMatchers("/Cart-items/**").hasRole("CUSTOMER")
                //Permit all on GET request for static content
                .requestMatchers(HttpMethod.GET, "/css/**", "/images/**", "/js/**",
                        "/other/**", "/Products/**",
                        "/About", "/Search", "/Live-search",
                        "/robots.txt", "/error/**", "/403").permitAll()
                .requestMatchers("/", "/register/**").permitAll()
                //Stripe webhook endpoint
                .requestMatchers(HttpMethod.POST, "/Status/**").permitAll()
                .anyRequest().authenticated()
        );

        return http.build();
    }

    private boolean isTestProfile() {
        return Arrays.asList(environment.getActiveProfiles()).contains("test") ||
                Arrays.asList(environment.getActiveProfiles()).contains("benc");
    }

}
