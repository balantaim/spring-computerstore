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

package com.martinatanasov.computerstore.services.payments;

import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.repositories.UserDaoImpl;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.model.CustomerSearchResult;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import com.stripe.param.CustomerSearchParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * Payment customer is needed in order to create invoices. Create payment customer for every user that are going to create payment.
 * Customer Update method is not supported! We don't send any address or taxes information to the payment provider.
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentCustomerServiceImpl implements PaymentCustomerService {

    private final UserDaoImpl userDao;

    @Override
    public CustomerCollection getAllCustomers() {
        CustomerListParams params = CustomerListParams.builder().build();
        // With limit
        //CustomerListParams params = CustomerListParams.builder().setLimit(10L).build();
        CustomerCollection customers = null;
        try {
            customers = Customer.list(params);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
        return customers;
    }

    @Override
    public Customer getCustomerById(final String customerId) {
        Customer customer = null;
        try {
            customer = Customer.retrieve(customerId);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
        return customer;
    }

    @Override
    public CustomerSearchResult getCustomersByKeyword(final String keyword) {
        CustomerSearchParams params = CustomerSearchParams.builder()
                        .setQuery("name:'" + keyword + "' OR email:'" + keyword + "'")
                        .build();

        CustomerSearchResult customers = null;
        try {
            customers = Customer.search(params);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

        log.info(customers.toString());
        return customers;
    }

    @Override
    public String createCustomer(final String email) {
        User user = userDao.findByUserName(email);
        String fullName = "";
        if (user != null) {
            if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
                fullName += user.getFirstName();
            }
            if (user.getLastName() != null) {
                if(!fullName.isEmpty()) {
                    fullName += " " + user.getLastName();
                } else {
                    fullName += user.getLastName();
                }
            }
        }

        CustomerCreateParams params = null;
        String description = "Online store payer";
        if (fullName.isEmpty()) {
            params = CustomerCreateParams.builder()
                    .setName(fullName)
                    .setEmail(email)
                    .setDescription(description)
                    .build();
        } else {
            params = CustomerCreateParams.builder()
                    .setEmail(email)
                    .setDescription(description)
                    .build();
        }

        Customer paymentCustomer = null;
        try {
            //Create Customer
            paymentCustomer = Customer.create(params);
            //Update User's customerId
            if(paymentCustomer != null) {
                assert user != null;
                user.setCustomerId(paymentCustomer.getId());
                userDao.save(user);
                log.info("User's customerId: {} updated", paymentCustomer.getId());
            }

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
        log.info("\n\tCustomer: {}", paymentCustomer);
        assert paymentCustomer != null;
        return "Status: " + paymentCustomer.getCreated();
    }

    @Override
    public void deleteCustomerById(final String customerId) {
        Customer resource = null;
        try {
            resource = Customer.retrieve(customerId);
            Customer customer = resource.delete();
            log.info("\n\tCustomer deleted: {}", customer);
            if (customer != null) {
                User user = userDao.findByCustomerId(customerId);
                if (user != null) {
                    user.setCustomerId(null);
                    userDao.save(user);
                    log.info("\n\tCustomer ID has been removed for user: {}!", user.getEmail());
                }
            }
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }
}
