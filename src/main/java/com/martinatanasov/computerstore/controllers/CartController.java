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
import com.martinatanasov.computerstore.model.CardItemDTO;
import com.martinatanasov.computerstore.services.CartService;
import com.martinatanasov.computerstore.util.converter.CartConverter;
import com.martinatanasov.computerstore.util.converter.UserAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static com.martinatanasov.computerstore.controllers.CustomErrorController.GLOBAL_ERROR_PAGE;
import static com.martinatanasov.computerstore.controllers.CustomErrorController.NOT_FOUND_PAGE;


@Controller
@PreAuthorize("hasRole('CUSTOMER')")
@RequestMapping("/Cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserAuthentication userAuthentication;
    private final CartConverter cartConverter;

    @Value("${store.product.purchase.limit}")
    private Integer PURCHASE_LIMIT_COUNT;

    @GetMapping("")
    public String cartItems(Model model){
        String username = userAuthentication.getUsernameFromAuthentication();

        Iterable<Cart> cartItems = cartService.getAllItems(username);

        if (cartItems != null && cartItems.iterator().hasNext()){
            //Convert Entities to DTOs
            final Set<CardItemDTO> products = cartConverter.convertCartEntityToCartDTO(cartItems);
            model.addAttribute("products", products);

            //Todo promo code

//            if(promoCode != null){
//                if(promoCode.length() > 3){
//                    model.addAttribute("promoCode", promoCode);
//                }
//            }

        }
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
        //Create new cart if not exist
        cartService.createCart(username, itemId, 1);
        //Get all carts
        Iterable<Cart> cartItems = cartService.getAllItems(username);

        if (cartItems != null && cartItems.iterator().hasNext()){
            //Convert Entities to DTOs
            final Set<CardItemDTO> products = cartConverter.convertCartEntityToCartDTO(cartItems);

            model.addAttribute("products", products);
            if(promoCode != null){
                if(promoCode.length() > 3){
                    model.addAttribute("promoCode", promoCode);
                }
            }
            return "Cart/cart-items";
        }
        return NOT_FOUND_PAGE;
    }

    @PostMapping("/clear")
    public String deleteAll(){
        String username = userAuthentication.getUsernameFromAuthentication();
        //Delete all Cart items
        cartService.deleteAllItems(username);
        //Return to the Cart view
        return "redirect:/Cart";
    }

    @PostMapping("/delete/{id}")
    public String deleteCartItem(@PathVariable("id") Integer id){
        String username = userAuthentication.getUsernameFromAuthentication();
        //Delete all Cart items
        cartService.deleteSingleItem(username, id);
        //Return to the Cart view
        return "redirect:/Cart";
    }

}
