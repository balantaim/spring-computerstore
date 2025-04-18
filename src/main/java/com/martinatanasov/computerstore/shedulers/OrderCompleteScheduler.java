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

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderCompleteScheduler {

    /**
     * This scheduler class simulate order complete.
     * It will update the order status after X period of time.
     * After completing the order, a refund is not possible!
     */

    private final OrderRepository orderRepository;
    private final static long DAYS_AFTER_ORDER_IS_COMPLETED = 14;

    //Run every day of the mount at midnight
    @Scheduled(cron = "0 0 0 * * *")
    public void orderComplete() {
        log.info("\n\t---> Scheduler order complete");
        List<Order> orders = orderRepository.findAll()
                .stream()
                .filter(order -> order.getStatus() == OrderStatus.SHIPPING_DELIVERED)
                .toList();
        if (!orders.isEmpty()) {
            orders.forEach(index -> {
                //Todo get last modified date instead of order created date
                if(isOrderFinalized(index.getOrderDate())) {
                    index.setStatus(OrderStatus.ORDER_COMPLETED);
                    final Order updatedOrder = orderRepository.save(index);
                    log.info("Updated order: Order completed: {}", updatedOrder.getId());
                }
            });

        }
    }

    private boolean isOrderFinalized(final Timestamp orderDate) {
        final long duration = Duration.between(orderDate.toInstant(), Instant.now()).toDays();
        return duration > DAYS_AFTER_ORDER_IS_COMPLETED;
    }

}
