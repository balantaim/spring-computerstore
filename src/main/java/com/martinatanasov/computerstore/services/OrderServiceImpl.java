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

package com.martinatanasov.computerstore.services;

import com.martinatanasov.computerstore.entities.*;
import com.martinatanasov.computerstore.model.Carrier;
import com.martinatanasov.computerstore.model.OrderStatus;
import com.martinatanasov.computerstore.model.PaymentStatus;
import com.martinatanasov.computerstore.model.PaymentType;
import com.martinatanasov.computerstore.repositories.CartRepository;
import com.martinatanasov.computerstore.repositories.OrderRepository;
import com.martinatanasov.computerstore.repositories.ProductRepository;
import com.martinatanasov.computerstore.repositories.UserDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
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
    private final ProductRepository productRepository;
    private final UserDao userDao;
    private final JsonMapper jsonMapper;

    @Override
    public Set<Order> getAllByUserEmail(final String email) {
        return orderRepository.getAllByUserEmail(email).stream()
                .filter(item -> item.getStatus() != OrderStatus.NEW_ORDER)
                .collect(Collectors.toSet());
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
    public @Nullable Order updateOrderAndEntities(final User user, Order order, final Carrier carrier, final String trackingNumber) {
        final Iterable<Cart> cartItems = cartRepository.findAllByUserId(user.getId());
        if (cartItems != null) {
            AtomicInteger quantity = new AtomicInteger();
            Set<OrderItem> orderItems = new HashSet<>();

            //Add carrier item to the cart items
            Optional<Product> carrierProductItem = productRepository.findFirstByProductNameIgnoreCase(carrier.name());
            carrierProductItem.ifPresent(product -> {
                        orderItems.add(
                                OrderItem.builder()
                                        .order(order)
                                        .product(product)
                                        .quantity(1)
                                        .pricePerUnit(product.getPrice())
                                        .build()
                        );
                        quantity.getAndIncrement();
                    }
            );

            //Add all cartItems to the OrderItems
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

    @Transactional
    @Override
    public boolean updateOrderAndPaymentAfterPaymentComplete(final String rawJson) {
        log.trace("\n\tData as string: {}", rawJson);

        final JsonNode root = jsonMapper.readTree(rawJson);

        final JsonNode metadataNode = root.get("metadata");
        //Check if the payment status is equal to paid
        if (Objects.equals(root.get("payment_status") != null ? root.get("payment_status").asText() : null, "paid")) {
            final JsonNode customer = root.get("customer");
            final String customerId = customer != null ? customer.asText() : null;
            log.info("\n\tCustomerId: {}", customerId);

            final Long orderId = root.get("client_reference_id") != null ? root.get("client_reference_id").asLong() : null;
            log.info("\n\tOrder ID: {}", orderId);

            if (metadataNode != null) {
                final String carrier = metadataNode.get("carrier") != null ? metadataNode.get("carrier").asText() : null;
                final String trackingNumber = metadataNode.get("tracking_number") != null ? metadataNode.get("tracking_number").asText() : null;
                log.trace("\n\tCarrier provider: {}", carrier);
                log.trace("\n\tTracking number: {}", trackingNumber);
            }

            final User user = userDao.findByCustomerId(customerId);
            Optional<Order> order = orderRepository.getOrderById(orderId);
            if (user != null && order.isPresent()) {
                //Check if customerId and orderId match the JSON's data
                if (Objects.equals(user.getCustomerId(), customerId) && Objects.equals(order.get().getId(), orderId)) {
                    Payment payment = order.get().getPayment();
                    //Update the payment status and order status
                    if (payment.getPaymentStatus() == PaymentStatus.NONE) {
                        payment.setPaymentStatus(PaymentStatus.COMPLETED);
                        order.get().setPayment(payment);
                        order.get().setStatus(OrderStatus.PAYMENT_SUCCESS);
                        //Save the data
                        orderRepository.save(order.get());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void abortOrder(final Order order) {
        orderRepository.save(order);
    }

    @Override
    public void delete(final Order order) {
        orderRepository.delete(order);
    }

    @Override
    public int getUnfinishedOrdersCount(final String email) {
        final Set<Order> orders = orderRepository.getAllByUserEmail(email);
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

    private BigDecimal getTotalAmount(final Set<OrderItem> orderItems) {
        BigDecimal orderTotal = BigDecimal.ZERO;
        for (OrderItem i : orderItems) {
            BigDecimal itemTotal = i.getProduct()
                    .getPrice()
                    .multiply(BigDecimal.valueOf(i.getQuantity()));
            orderTotal = orderTotal.add(itemTotal);
        }
        return orderTotal;
    }

    private Order createInitialOrder(final User user) {
        final Order order = Order.builder()
                .user(user)
                .totalAmount(BigDecimal.ZERO)
                .status(OrderStatus.NEW_ORDER)
                .build();
        return orderRepository.save(order);
    }

}
