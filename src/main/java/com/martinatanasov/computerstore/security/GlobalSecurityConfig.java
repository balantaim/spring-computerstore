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

import com.martinatanasov.computerstore.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Arrays;


@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class GlobalSecurityConfig {

    //Get Active Profile
    @Autowired
    private Environment environment;

    //bcrypt bean definition
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //authenticationProvider bean definition
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService); //set the custom user details service
        auth.setPasswordEncoder(passwordEncoder()); //set the password encoder - bcrypt
        return auth;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationSuccessHandler customAuthenticationSuccessHandler) throws Exception {

        //Enable Iframe for the same origin (default value is disabled)
//        http.headers(headers -> headers
//                .frameOptions(frameOptions -> frameOptions.sameOrigin()
//                )
//        );

        //Setup permission by role and methods
        http.authorizeHttpRequests(config -> config
                        .requestMatchers("/Profile/**").hasAnyRole("CUSTOMER", "MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/Products/**").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.PUT, "/Products/**").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.DELETE, "/Products/**").hasRole("CUSTOMER")
                        .requestMatchers("/Cart-items/**").hasRole("CUSTOMER")
                        //Permit all on GET request for static content
                        .requestMatchers(HttpMethod.GET, "/css/**", "/images/**", "/js/**",
                                "/other/**", "/page/actuator/**", "/Products/**",
                                "/About", "/Search", "/Live-search",
                                "/robots.txt", "/error/**", "/403").permitAll()
                        .requestMatchers("/", "/register/**").permitAll()
                        .anyRequest().authenticated()
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
                .exceptionHandling(config -> config
                        .accessDeniedPage("/access-denied")
                );

        if (isTestProfile()) {
            //Disable Cross Site Request Forgery (CSRF)
            http.csrf(csrf -> csrf.disable());
        }
//        else {
//            //Using the CookieCsrfTokenRepository
//            //http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
//        }

        return http.build();
    }

    private boolean isTestProfile() {
        return Arrays.asList(environment.getActiveProfiles()).contains("test");
    }

}
