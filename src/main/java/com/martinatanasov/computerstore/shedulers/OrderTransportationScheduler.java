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

package com.martinatanasov.computerstore.shedulers;


import com.martinatanasov.computerstore.entities.Order;
import com.martinatanasov.computerstore.model.OrderStatus;
import com.martinatanasov.computerstore.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderTransportationScheduler {

    /**
     * This scheduler class simulate order delivery delay.
     * It will update the order status after X period of time.
     */

    private final OrderRepository orderRepository;

    //Run on every 12 hours
    @Scheduled(cron = "0 0 */12 * * *")
    public void initiateOrderTransport() {
        log.info("\n\t---> Scheduler init delivery");
        List<Order> orders = orderRepository.findAll()
                .stream()
                .filter(order -> order.getStatus() == OrderStatus.PAYMENT_SUCCESS)
                .toList();
        if(!orders.isEmpty()) {
            orders.forEach(index -> {
                index.setStatus(OrderStatus.SHIPPING_IN_PROGRESS);
                final Order updatedOrder = orderRepository.save(index);
                log.info("Updated order: Shipping in progress: {}", updatedOrder.getId());
            });
        }
    }

    //Run on every 6 hours
    @Scheduled(cron = "0 0 */6 * * *")
    public void finishOrderTransport() {
        log.info("\n\t---> Scheduler finish delivery");
        List<Order> orders = orderRepository.findAll()
                .stream()
                .filter(order -> order.getStatus() == OrderStatus.SHIPPING_IN_PROGRESS)
                .toList();
        if(!orders.isEmpty()) {
            orders.forEach(index -> {
                index.setStatus(OrderStatus.SHIPPING_DELIVERED);
                final Order updatedOrder = orderRepository.save(index);
                log.info("Updated order: Shipping delivered: {}", updatedOrder.getId());
            });
        }
    }

}
