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

package com.martinatanasov.computerstore.util.converter;

import com.martinatanasov.computerstore.entities.Cart;
import com.martinatanasov.computerstore.entities.Product;
import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.model.CardItemDTO;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class CartConverter {

    public Set<CardItemDTO> convertCartEntityToCartDTO(Iterable<Cart> cartItems) {
        Set<CardItemDTO> products = new HashSet<>();
        for(Cart item: cartItems){
            products.add(new CardItemDTO(
                    item.getProduct().getId(),
                    item.getProduct().getProductName(),
                    item.getProduct().getProducer(),
                    item.getProduct().getImageUrl(),
                    item.getProduct().getDescription(),
                    item.getProduct().getCategory().getName(),
                    item.getProduct().getStock(),
                    item.getProduct().getPrice())
            );
        }
        return products;
    }

    public Set<Cart> convertCartDTOToCartEntity(Iterable<CardItemDTO> cartItems, User user, Product product) {
        Set<Cart> products = new HashSet<>();
        for (CardItemDTO item : cartItems) {
            products.add(Cart.builder()
                    .user(user)
                    .product(product)
                    .quantity(item.stock())
                    .build());
        }
        return products;
    }

}
