/*
 * Copyright 2026 Martin Atanasov.
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

import com.martinatanasov.computerstore.entities.Order;
import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.model.OrderStatus;
import com.martinatanasov.computerstore.repositories.CartRepository;
import com.martinatanasov.computerstore.repositories.OrderRepository;
import com.martinatanasov.computerstore.repositories.UserDao;
import com.martinatanasov.computerstore.utils.converter.UserConverter;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final UserDao userDao;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserConverter userConverter;

    @Override
    public void initializeUserSession(Authentication authentication, HttpSession session) {
        final String userName = authentication.getName();
        final User user = userDao.findByUserName(userName);
        //Set userDTO to the session
        session.setAttribute("user", userConverter.userToSessionUserDTO(user));
        //Get User's order count
        final int orderCount = getOrderCountByUsername(userName);
        //Get User's card count
        final int cartCount = getCartCountByUserId(user.getId());
        //Set User's session attributes
        session.setAttribute("orders-count", orderCount > 0 ? orderCount : null);
        session.setAttribute("cart-items-count", cartCount > 0 ? cartCount : null);
    }

    private int getOrderCountByUsername(String userName) {
        final Set<Order> orders = orderRepository.getAllByUserEmail(userName);
        if (orders != null && !orders.isEmpty()) {
            return orders.stream()
                    .filter(i ->
                            i.getStatus() == OrderStatus.PAYMENT_REQUIRED || i.getStatus() == OrderStatus.SHIPPING_IN_PROGRESS
                    )
                    .collect(Collectors.toSet())
                    .size();
        }
        return 0;
    }

    private int getCartCountByUserId(Long userId) {
        final Integer count = cartRepository.countByUserId(userId);
        return count == null ? 0:count;
    }

}
