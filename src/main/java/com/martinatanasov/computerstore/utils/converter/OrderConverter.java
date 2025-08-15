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

package com.martinatanasov.computerstore.utils.converter;

import com.martinatanasov.computerstore.entities.Order;
import com.martinatanasov.computerstore.entities.OrderItem;
import com.martinatanasov.computerstore.model.OrderDTO;
import com.martinatanasov.computerstore.model.OrderItemDTO;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class OrderConverter {

    public Set<OrderDTO> convertOrdersEntityToOrderDTO(final Set<Order> orders) {
        List<OrderDTO> orderDto = new ArrayList<>();
        for (Order item : orders) {
            orderDto.add(new OrderDTO(
                    item.getId(),
                    item.getOrderDate(),
                    item.getModifyDate(),
                    item.getTotalAmount(),
                    item.getStatus().toString(),
                    item.getOrderIdentifier())
            );
        }
        //Filter by date. Older will be placed first.
        orderDto.sort(Comparator.comparing(OrderDTO::orderDate, Comparator.reverseOrder()));
        return new LinkedHashSet<>(orderDto);
    }

    public OrderDTO convertOrderToOrderDTO(final Order order) {
        return new OrderDTO(
                order.getId(),
                order.getOrderDate(),
                order.getModifyDate(),
                order.getTotalAmount(),
                order.getStatus().toString(),
                order.getOrderIdentifier()
        );
    }

    public Set<OrderItemDTO> convertOrderItemsToDTO(final Set<OrderItem> orderItems) {
        Set<OrderItemDTO> orderItemsDto = new HashSet<>();
        orderItems.forEach(i -> {
            orderItemsDto.add(orderItemToOrderItemDTO(i));
        });
        return orderItemsDto;
    }

    public OrderItemDTO orderItemToOrderItemDTO(final OrderItem item) {
        return new OrderItemDTO(
          item.getId(),
                item.getQuantity(),
                item.getPricePerUnit(),
                item.getProduct().getProductName(),
                item.getProduct().getImageUrl()
        );
    }

}
