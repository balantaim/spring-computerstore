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

import com.martinatanasov.computerstore.entities.Cart;
import com.martinatanasov.computerstore.entities.Product;
import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.model.CardItemDTO;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CartConverter {

    public Set<CardItemDTO> convertCartEntityToCartDTO(Iterable<Cart> cartItems) {
        //LinkedHashSet will preserve the order instead of HashSet
        Set<CardItemDTO> cartItemsDto = new LinkedHashSet<>();
        for(Cart item: cartItems){
            cartItemsDto.add(new CardItemDTO(
                    item.getId(),
                    item.getProduct().getId(),
                    item.getProduct().getProductName(),
                    item.getProduct().getProducer(),
                    item.getProduct().getImageUrl(),
                    item.getProduct().getDescription(),
                    item.getProduct().getCategory().getName(),
                    item.getQuantity(),
                    item.getProduct().getPrice())
            );
        }
        //Sort Cart items by ID
        return cartItemsDto.stream()
                .sorted(Comparator.comparing(CardItemDTO::cartId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<Cart> convertCartDTOToCartEntity(Iterable<CardItemDTO> cartItemsDto, User user, Product product) {
        Set<Cart> cartItems = new HashSet<>();
        for (CardItemDTO item : cartItemsDto) {
            cartItems.add(Cart.builder()
                    .id(item.cartId())
                    .user(user)
                    .product(product)
                    .quantity(item.quantity())
                    .build());
        }
        return cartItems;
    }

}
