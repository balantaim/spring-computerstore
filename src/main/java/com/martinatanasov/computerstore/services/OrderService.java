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

package com.martinatanasov.computerstore.services;

import com.martinatanasov.computerstore.entities.Cart;
import com.martinatanasov.computerstore.entities.Order;
import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.model.Carrier;
import com.martinatanasov.computerstore.model.OrderStatus;

import java.util.Optional;
import java.util.Set;

public interface OrderService {

    Set<Order> getAllByUserEmail(String email);

    Optional<Order> getFirstByStatusAndUserId(Long userId, OrderStatus status);

    Optional<Order> getOrderById(Long orderId);

    boolean createNewOrder(User user, Iterable<Cart> cartItems);

    Order updateOrderAndEntities(User user, Order order, Carrier carrier, String trackingNumber);

    void delete(Order order);

    int getUnfinishedOrdersCount(String email);

}

