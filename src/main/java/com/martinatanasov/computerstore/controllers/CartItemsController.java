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

import com.martinatanasov.computerstore.entities.Cart;
import com.martinatanasov.computerstore.entities.Product;
import com.martinatanasov.computerstore.model.CardItemDTO;
import com.martinatanasov.computerstore.services.CartService;
import com.martinatanasov.computerstore.services.ProductServiceImpl;
import com.martinatanasov.computerstore.util.converter.UserAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.martinatanasov.computerstore.controllers.CustomErrorController.GLOBAL_ERROR_PAGE;
import static com.martinatanasov.computerstore.controllers.CustomErrorController.NOT_FOUND_PAGE;

@Slf4j
@Controller
@PreAuthorize("hasRole('CUSTOMER')")
@RequestMapping("/Cart")
@RequiredArgsConstructor
public class CartItemsController {

    private final ProductServiceImpl productService;
    private final CartService cartService;
    private final UserAuthentication userAuthentication;

    @Value("${store.product.purchase.limit}")
    private Integer PURCHASE_LIMIT_COUNT;

    @GetMapping("")
    public String cartItems(){
        //Todo

        return "Cart/cart-items";
    }

    @PostMapping("")
    public String addToCard(Model model,
                            @RequestParam(value = "promoCode", required = false) String promoCode,
                            @RequestParam(value = "itemId") Integer itemId){
        //Check if the product ID is valid
        if(itemId == null){
            return GLOBAL_ERROR_PAGE;
        }

        //Todo promo code
        String username = userAuthentication.getUsernameFromAuthentication();
        cartService.createCart(username, itemId, 1);

        Iterable<Cart> cartItems = cartService.getAllItems(username);

        //Optional<Product> product = productService.getProductById(itemId);
        if (cartItems.iterator().hasNext()){
            Product product = cartItems.iterator().next().getProduct();

            CardItemDTO cardItemDTO = new CardItemDTO(
                    product.getId(),
                    product.getProductName(),
                    product.getProducer(),
                    product.getImageUrl(),
                    product.getDescription(),
                    product.getCategory().getName(),
                    product.getStock(),
                    product.getPrice()
            );



            model.addAttribute("product", cardItemDTO);
            if(promoCode != null){
                if(promoCode.length() > 3){
                    model.addAttribute("promoCode", promoCode);
                }
            }
            return "Cart/cart-items";
        }
        return NOT_FOUND_PAGE;
    }

}
