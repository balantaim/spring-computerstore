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

import com.martinatanasov.computerstore.entities.*;
import com.martinatanasov.computerstore.model.Carrier;
import com.martinatanasov.computerstore.model.OrderStatus;
import com.martinatanasov.computerstore.model.PaymentStatus;
import com.martinatanasov.computerstore.model.PaymentType;
import com.martinatanasov.computerstore.repositories.CartRepository;
import com.martinatanasov.computerstore.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final BigDecimal shippingEstimate = BigDecimal.valueOf(5.00);

    @Override
    public Set<Order> getAllByUserEmail(final String email) {
        return orderRepository.getAllByUserEmail(email);
    }

    @Override
    public Optional<Order> getFirstByStatusAndUserId(final Long userId, final OrderStatus status) {
        return orderRepository.getFirstByStatusAndUserId(status, userId);
    }

    @Override
    public Optional<Order> getOrderById(final Long orderId) {
        return orderRepository.getOrderById(orderId);
    }

    @Transactional
    @Override
    public boolean createNewOrder(final User user, final Iterable<Cart> cartItems) {
        //Create a new Order
        final Order order = createInitialOrder(user);
        log.info("\n\t Order from service init: {}", order);
        return orderRepository.save(order) != null;
    }

    @Transactional
    @Override
    public Order updateOrderAndEntities(final User user, Order order, final Carrier carrier, final String trackingNumber) {
        final Iterable<Cart> cartItems = cartRepository.findAllByUserId(user.getId());
        if (cartItems != null) {
            //Create Order Items
            AtomicInteger quantity = new AtomicInteger();
            Set<OrderItem> orderItems = new HashSet<>();
            cartItems.forEach(i -> {
                orderItems.add(OrderItem.builder()
                        .order(order)
                        .product(i.getProduct())
                        .quantity(i.getQuantity())
                        .pricePerUnit(i.getProduct().getPrice())
                        .build());
                quantity.getAndIncrement();
            });
            //Check OrderItems count
            if (orderItems.isEmpty()) {
                log.error("\n\tOrder items count equal to zero!");
                return null;
            }

            //Create total amount variable
            final BigDecimal totalAmount = getTotalAmount(orderItems);

            //Create a new Payment
            final Payment payment = Payment.builder()
                    .order(order)
                    .amount(totalAmount)
                    .paymentType(PaymentType.CARD)
                    .paymentStatus(PaymentStatus.NONE)
                    .build();

            //Create shipment
            final Shipment shipment = Shipment.builder()
                    .order(order)
                    .carrier(carrier)
                    .trackingNumber(trackingNumber)
                    .quantity(quantity.get())
                    .country(user.getCountry())
                    .address(user.getAddress())
                    .phoneNumber(user.getPhoneNumber())
                    .build();
            //Update order columns
            order.setTotalAmount(totalAmount);
            order.setPayment(payment);
            order.setShipment(shipment);
            //Clear OrderItems set
            order.getOrderItems().clear();
            order.setOrderItems(orderItems);
            //Update Order status
            order.setStatus(OrderStatus.PAYMENT_REQUIRED);
            //Save Order
            final Order updatedOrder = orderRepository.save(order);
            log.info("\n\t Update Order: {}", updatedOrder);
            return updatedOrder;
        }
        return null;
    }

    @Override
    public void delete(final Order order) {
        orderRepository.delete(order);
    }

    @Override
    public int getUnfinishedOrdersCount(final String email) {
        Set<Order> orders = orderRepository.getAllByUserEmail(email);
        if (orders != null && !orders.isEmpty()) {
            orders = orders.stream()
                    .filter(i ->
                            i.getStatus() != OrderStatus.PAYMENT_SUCCESS ||
                            i.getStatus() != OrderStatus.ORDER_COMPLETED ||
                            i.getStatus() != OrderStatus.ORDER_ABORTED ||
                            i.getStatus() != OrderStatus.NEW_ORDER
                    )
                    .collect(Collectors.toSet());
            return orders.size();
        }
        return 0;
    }

    private BigDecimal getTotalAmount(final Set<OrderItem> orderItems) {
        BigDecimal orderTotal = BigDecimal.ZERO;
        for (OrderItem i : orderItems) {
            orderTotal = orderTotal.add(i.getProduct().getPrice());
        }
        //Add shipping tax
        orderTotal = orderTotal.add(shippingEstimate);
        return orderTotal;
    }

    private Order createInitialOrder(final User user) {
        Order order = Order.builder()
                .user(user)
                .totalAmount(BigDecimal.ZERO)
                .status(OrderStatus.NEW_ORDER)
                .orderDate(new Timestamp(System.currentTimeMillis()))
                .build();
        return orderRepository.save(order);
    }

}
