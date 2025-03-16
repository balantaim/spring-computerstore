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

package com.martinatanasov.computerstore.controllers;

import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.repositories.UserDaoImpl;
import com.martinatanasov.computerstore.services.payments.PaymentCustomerServiceImpl;
import com.martinatanasov.computerstore.utils.converter.UserAuthentication;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.model.CustomerSearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.martinatanasov.computerstore.controllers.CustomErrorController.GLOBAL_ERROR_PAGE;
import static com.martinatanasov.computerstore.controllers.CustomErrorController.NOT_FOUND_PAGE;


@Slf4j
@RequiredArgsConstructor
@RestController

@RequestMapping("/Payments")
public class PaymentController {

    private final PaymentCustomerServiceImpl paymentCustomerService;
    private final UserAuthentication userAuthentication;
    private final UserDaoImpl userDao;

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/get-all")
    public String getAllCustomers() {
        CustomerCollection result = paymentCustomerService.getAllCustomers();
        return result.toString();
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    @GetMapping("/{customerId}")
    public String getCustomerById (@PathVariable String customerId) {
        Customer customer = paymentCustomerService.getCustomerById(customerId);
        if (customer != null) {
            return customer.toString();
        }
        return "customerId " + customerId + " not found!";
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/search/{keyword}")
    public String getCustomerByKeyword (@PathVariable String keyword){
        if(keyword != null && keyword.length() >= 2 && keyword.length() <= 255) {
            CustomerSearchResult customers = paymentCustomerService.getCustomersByKeyword(keyword);
            log.info(customers.toString());
            return customers.toString();
        }
        return NOT_FOUND_PAGE;
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/create")
    public String createCustomer(){
        String email = userAuthentication.getUsernameFromAuthentication();
        User user = userDao.findByUserName(email);
        if (user != null) {
            Customer paymentCustomer = paymentCustomerService.createCustomer(user);
            if (user.getCustomerId() == null) {
                user.setCustomerId(paymentCustomer.getId());
                userDao.save(user);
            }
            log.info("\n\t Result {}", paymentCustomer);
            return "data: " + paymentCustomer;
        }
        return GLOBAL_ERROR_PAGE;
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/delete/{customerId}")
    public String deleteCustomerById (@PathVariable String customerId) {
        if (customerId != null) {
            paymentCustomerService.deleteCustomerById(customerId);
            //Update user
            String email = userAuthentication.getUsernameFromAuthentication();
            User user = userDao.findByUserName(email);
            user.setCustomerId(null);
            userDao.save(user);
        }
        return "customerId";
    }

}
