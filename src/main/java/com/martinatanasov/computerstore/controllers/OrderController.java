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

package com.martinatanasov.computerstore.controllers;

import com.martinatanasov.computerstore.entities.Order;
import com.martinatanasov.computerstore.entities.OrderItem;
import com.martinatanasov.computerstore.services.OrderService;
import com.martinatanasov.computerstore.utils.converter.OrderConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;
import java.util.Set;

import static com.martinatanasov.computerstore.controllers.CustomErrorController.NOT_FOUND_PAGE;


@RequiredArgsConstructor
@Controller
@RequestMapping("/Orders")
@PreAuthorize("hasRole('CUSTOMER')")
public class OrderController {

    private final OrderService orderService;
    private final OrderConverter orderConverter;

    @GetMapping("")
    public String orders(Model model) {
        final String username = getUserName();
        if (username != null) {
            Set<Order> orders = orderService.getAllByUserEmail(username);
            if (orders != null && !orders.isEmpty()) {
                model.addAttribute("orders", orderConverter.convertOrdersEntityToOrderDTO(orders));
            }
            return "Orders/orders";
        }
        return NOT_FOUND_PAGE;
    }

    @GetMapping("/order-details/{orderId}")
    public String orderDetails(@PathVariable(name = "orderId") Long orderId, Model model) {
        if (orderId != null) {
            final String email = getUserName();
            if (email != null) {
                final Optional<Order> order = orderService.getOrderById(orderId);
                if (order.isPresent()) {
                    final Set<OrderItem> orderItems = order.get().getOrderItems();
                    model.addAttribute("order", orderConverter.convertOrderToOrderDTO(order.get()));
                    model.addAttribute("orderItems", orderConverter.convertOrderItemsToDTO(orderItems));
                    return "Orders/order-details";
                }
            }
        }
        return NOT_FOUND_PAGE;
    }

    private String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }


}
