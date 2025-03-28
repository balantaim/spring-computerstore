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

package com.martinatanasov.computerstore.repositories;

import com.martinatanasov.computerstore.entities.Cart;
import com.martinatanasov.computerstore.entities.Payment;
import com.martinatanasov.computerstore.entities.Shipment;
import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.model.UserFailedAttempts;

import java.util.List;

public interface UserDao {
    User findByUserName(String email);

    User findByUserId(Long id);

    User findByCustomerId(String customerId);

    Iterable<User> getAllUsers();

    List<Shipment> findShipmentsByUserId(Long id);

    List<Cart> findCartsByUserId(Long id);

    List<Payment> findPaymentsByUserId(Long id);

    void save(User user);

    void updateUserDetails(User user);

    void deleteByUserEmail(String email);

    //@Query(value = "SELECT attempts, enabled, lock_date FROM users u WHERE u.email=:email%", nativeQuery = true)
    Object[] getUserLoginAttempts(final String name);

    void setLoginFailedAttempt(final String name, UserFailedAttempts info);

}
