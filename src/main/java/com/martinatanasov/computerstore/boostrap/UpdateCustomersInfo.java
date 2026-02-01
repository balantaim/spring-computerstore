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

package com.martinatanasov.computerstore.boostrap;

import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.repositories.UserDao;
import com.martinatanasov.computerstore.services.payments.PaymentCustomerService;
import com.stripe.model.CustomerCollection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateCustomersInfo implements CommandLineRunner {

    private final PaymentCustomerService paymentCustomerService;
    private final UserDao userDao;

    @Override
    public void run(String... args) {
        log.info("\n\tInitiate update customer_id values to the database");
        CustomerCollection customerCollection = paymentCustomerService.getAllCustomers();
        if (customerCollection.getData() != null) {
            customerCollection.getData()
                    .forEach(index -> {
                        User user = userDao.findByUserName(index.getEmail());
                        if (user != null) {
                            user.setCustomerId(index.getId());
                            userDao.save(user);
                        }
                    });
        }
        log.info("\n\tAll customer_id values are updated!");
    }

}
