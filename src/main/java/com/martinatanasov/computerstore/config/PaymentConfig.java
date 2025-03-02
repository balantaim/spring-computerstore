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


import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class PaymentConfig {

    @Value("${stripe.secret.key}")
    private String SECRET_KEY;

    @PostConstruct
    public void init(){
        if (SECRET_KEY == null || SECRET_KEY.isEmpty()) {
            throw new RuntimeException("Stripe Secret key is not set!");
        } else {
            Stripe.apiKey = SECRET_KEY;
            log.info("\n\tStripe Secret Key is set!");
        }
    }

}
