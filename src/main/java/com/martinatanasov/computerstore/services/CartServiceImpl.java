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
import com.martinatanasov.computerstore.entities.Product;
import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.repositories.CartRepository;
import com.martinatanasov.computerstore.repositories.ProductRepository;
import com.martinatanasov.computerstore.repositories.UserDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final UserDao userDao;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Value("${store.product.purchase.limit}")
    private Integer PURCHASE_LIMIT_COUNT;

    @Override
    public Iterable<Cart> getAllItems(final String username) {
        Long userId = getUserId(username);
        if(userId != null){
            Iterable<Cart> cartItems = cartRepository.findAllByUserId(userId);
            return cartItems == null ? new HashSet<>():cartItems;
        }
        return null;
    }

    @Override
    public void createCart(final String username, final Integer productId, final Integer quantity) {
        final User user = getUser(username);
        boolean isCardItemExist = isCartItemExist(user, productId);
        if(!isCardItemExist){
            Optional<Product> product = productRepository.findProductById(productId);
            //Perform create if the product is valid and has at least 1 count
            if(product.isPresent() && product.get().getStock() > 0){
                Cart cart = new Cart();
                cart.setUser(user);
                cart.setProduct(product.get());
                cart.setQuantity(quantity);

                cartRepository.save(cart);
            }
        }
    }

    @Override
    public void updateCart(final String username, final Integer productId, final Integer quantity) {
        if(isTransactionValid(quantity)){
            Long userId = getUserId(username);
            if(userId != null){
                //Perform update
                Optional<Cart> item = cartRepository.findFirstByProductId(productId);
                if (item.isPresent()){
                    item.get().setQuantity(quantity);
                    cartRepository.save(item.get());
                }
            }
        }
    }

    @Override
    public void deleteSingleItem(final String username, final Long cartId) {
        Long userId = getUserId(username);
        if(userId != null && cartId != null){
            //Perform single delete
            Optional<Cart> item = cartRepository.findById(cartId);

            item.ifPresent(cartRepository::delete);
        }
    }

    @Transactional
    @Override
    public void deleteAllItems(final String username) {
        Long userId = getUserId(username);
        if(userId != null){
            cartRepository.deleteAllByUserId(userId);
        }
    }

    @Override
    public boolean isCartItemExist(final User user, final Integer productId) {
        Set<Cart> item = cartRepository.findAllByUserId(user.getId());
        if (item == null) {
            return false;
        } else {
            Set<Cart> filteredCarts = item.stream()
                    .filter(index -> Objects.equals(index.getProduct().getId(), productId))
                    .collect(Collectors.toSet());
            return !filteredCarts.isEmpty();
        }
    }

    @Override
    public void updateCartQuantity(final Long cartId, final Boolean isIncrement) {
        Optional<Cart> item = cartRepository.findById(cartId);
        if(item.isPresent()){
            //Save the updated cart object
            int newQuantity = isIncrement ? (item.get().getQuantity() + 1):(item.get().getQuantity() - 1);
            //Check if the quantity is valid
            if(isTransactionValid(newQuantity)){
                item.get().setQuantity(newQuantity);
                cartRepository.save(item.get());
            }
        }
    }

    private boolean isTransactionValid(Integer quantity){
        return quantity > 0 && quantity <= PURCHASE_LIMIT_COUNT;
    }

    private Long getUserId(final String username){
        final User user = userDao.findByUserName(username);
        return user != null ? user.getId():null;
    }

    private User getUser(final String username){
        return userDao.findByUserName(username);
    }

}
