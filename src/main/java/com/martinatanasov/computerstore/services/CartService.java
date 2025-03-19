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
import com.martinatanasov.computerstore.entities.User;

public interface CartService {

    Iterable<Cart> getAllItems(String username);

    void createCart(String username, Integer productId, Integer quantity);

    void updateCart(String username, Integer productId, Integer quantity);

    void deleteSingleItem(String username, Long cartId);

    void deleteAllItems(String username);

    boolean isCartItemExist(User user, Integer productId);

    void updateCartQuantity(Long cartId, Boolean isIncrement);

    int getCartItemsCount(Long userId);

}
