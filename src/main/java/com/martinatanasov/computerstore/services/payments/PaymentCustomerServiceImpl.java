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
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.model.CustomerSearchResult;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import com.stripe.param.CustomerSearchParams;
import com.stripe.param.CustomerUpdateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * Payment customer is needed in order to create invoices. Create payment customer for every user that are going to create payment.
 * Customer Update method is not supported! We don't send any address or taxes information to the payment provider.
 *
 * @author Martin Atanasov
 */

@Slf4j
@Service
public class PaymentCustomerServiceImpl implements PaymentCustomerService {

    @Override
    public CustomerCollection getAllCustomers() {
        CustomerListParams params = CustomerListParams.builder().build();
        // Create parameters with count limit
        //CustomerListParams params = CustomerListParams.builder().setLimit(10L).build();
        CustomerCollection customers;
        try {
            customers = Customer.list(params);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
        return customers;
    }

    @Override
    public Customer getCustomerById(final String customerId) {
        Customer customer;
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

        CustomerSearchResult customers;
        try {
            customers = Customer.search(params);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

        log.info(customers.toString());
        return customers;
    }

    @Override
    public Customer createCustomer(final User user) {
        String fullName = "",
                description = "Computer store's customer",
                phoneNumber;

        //Extract fullName and phoneNumber from the user
        if (user != null) {
            phoneNumber = user.getPhoneNumber();
            fullName = getUserFullName(user);
        } else {
            return null;
        }

        //Create Customer parameters object
        CustomerCreateParams params;
        if (!fullName.isEmpty()) {
            params = CustomerCreateParams.builder()
                    .setName(fullName)
                    .setEmail(user.getEmail())
                    .setPhone(phoneNumber)
                    .setDescription(description)
                    .build();
        } else {
            params = CustomerCreateParams.builder()
                    .setEmail(user.getEmail())
                    .setPhone(phoneNumber)
                    .setDescription(description)
                    .build();
        }

        //Create customer in the Stripe system
        Customer paymentCustomer;
        try {
            //Create Customer
            paymentCustomer = Customer.create(params);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
        log.info("\n\tCustomer: {}", paymentCustomer);
        return paymentCustomer;
    }

    @Override
    public void deleteCustomerById(final String customerId) {
        Customer resource;
        try {
            resource = Customer.retrieve(customerId);
            final Customer customer = resource.delete();
            log.info("\n\tCustomer deleted: {}", customer);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateCustomerById(final String customerId, final User user) {
        Customer resource = null;
        try {
            resource = Customer.retrieve(customerId);
            if (resource != null) {
                final CustomerUpdateParams params = CustomerUpdateParams.builder()
                        .setName(getUserFullName(user).isEmpty() ? null:getUserFullName(user))
                        .setEmail(user.getEmail())
                        .setPhone(user.getPhoneNumber())
                        .build();
                final Customer customer = resource.update(params);
                log.info("\n\tUpdated customer's data: {}", customer);
            }
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getUserFullName(final User user) {
        String fullName = "";
        if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
            fullName += user.getFirstName();
        }
        if (user.getLastName() != null) {
            if (!fullName.isEmpty()) {
                fullName += " " + user.getLastName();
            } else {
                fullName += user.getLastName();
            }
        }
        return fullName;
    }

}
