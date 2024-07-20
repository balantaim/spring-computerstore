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

import com.martinatanasov.computerstore.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


@Configuration
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
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService); //set the custom user details service
        auth.setPasswordEncoder(passwordEncoder()); //set the password encoder - bcrypt
        return auth;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationSuccessHandler customAuthenticationSuccessHandler) throws Exception{
        //Setup permission by role and methods
        http.authorizeHttpRequests(config -> config
                                .requestMatchers( "/Profile/**").hasAnyRole("CUSTOMER", "MANAGER", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/Products/**").hasRole("CUSTOMER")
                                .requestMatchers(HttpMethod.PUT, "/Products/**").hasRole("CUSTOMER")
                                .requestMatchers(HttpMethod.DELETE, "/Products/**").hasRole("CUSTOMER")
                                .requestMatchers( "/Cart-items/**").hasRole("CUSTOMER")
                //Permit all on GET request for static content
                                .requestMatchers(HttpMethod.GET, "/css/**", "/images/**", "/js/**",
                                        "/other/**", "/page/actuator/**", "/Products/**",
                                        "/About", "/Search", "/Live-search",
                                        "/robots.txt").permitAll()
                                .requestMatchers( "/", "/register/**").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        //Redirect to login form if no authorisation
                        .loginPage("/Login")
                        //Login method used in html
                        .loginProcessingUrl("/authenticateTheUser")
                        .successHandler(customAuthenticationSuccessHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .permitAll()
                        .logoutSuccessUrl("/Login")
                )

                //From security project
//              logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
//              logout.logoutSuccessUrl("/");
//              logout.deleteCookies("JSESSIONID");
//              logout.invalidateHttpSession(true);

                .exceptionHandling(config -> config
                        .accessDeniedPage("/access-denied")
                );
        //Use Http basic authentication
//        http.httpBasic(Customizer.withDefaults());

        //Disable Cross Site Request Forgery (CSRF)
        //Not required for REST operations like POST, PUT, DELETE and/or PATCH
        http.csrf(csrf -> csrf.disable());

        //Enable csrf token
//        http.csrf(csrf -> csrf
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));

        return http.build();
    }
}
