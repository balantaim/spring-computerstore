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
import com.martinatanasov.computerstore.model.OrderSummaryDTO;
import com.martinatanasov.computerstore.services.CartService;
import com.martinatanasov.computerstore.utils.converter.CartConverter;
import com.martinatanasov.computerstore.utils.converter.UserAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.web.csrf.CsrfToken;

import java.util.*;

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

    @GetMapping("")
    public String cartItems(Model model, HttpServletRequest request){
        String username = userAuthentication.getUsernameFromAuthentication();

        Iterable<Cart> cartItems = cartService.getAllItems(username);

        if (cartItems != null && cartItems.iterator().hasNext()){
            //Convert Entities to DTOs
            final Set<CardItemDTO> products = cartConverter.convertCartEntityToCartDTO(cartItems);
            //Add cart products
            model.addAttribute("products", products);
            //Add order summary
            model.addAttribute("orderSummary", calculateOrderSummary());
            model.addAttribute("csrfToken", getCSRFToken(request));

            //Todo promo code
//            if(promoCode != null){
//                if(promoCode.length() > 3){
//                    model.addAttribute("promoCode", promoCode);
//                }
//            }
        }
        return "Cart/cart";
    }

    @PostMapping("")
    public String addToCard(Model model,
                            @RequestParam(value = "promoCode", required = false) String promoCode,
                            @RequestParam(value = "itemId") Integer itemId,
                            HttpServletRequest request){
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

            model.addAttribute("csrfToken", getCSRFToken(request));
            model.addAttribute("products", products);
            //Add order summary
            model.addAttribute("orderSummary", calculateOrderSummary());
            if(promoCode != null){
                if(promoCode.length() > 3){
                    model.addAttribute("promoCode", promoCode);
                }
            }
            return "Cart/cart";
        }
        return NOT_FOUND_PAGE;
    }

    @PostMapping("/clear")
    public String deleteAll(){
        String username = userAuthentication.getUsernameFromAuthentication();
        //Delete all Cart items
        cartService.deleteAllItems(username);
        //Return to the Cart view
        return "Cart/cart";
    }

    @PostMapping("/delete/{id}")
    public Collection<ModelAndView> deleteCartItem(@PathVariable("id") Long id,
                                                   HttpServletRequest request){
        String username = userAuthentication.getUsernameFromAuthentication();
        //Delete all Cart items
        cartService.deleteSingleItem(username, id);
        //Return to the fragment views
        Iterable<Cart> cartItems = cartService.getAllItems(username);
        if (cartItems != null && cartItems.iterator().hasNext()){
            return getCartAndSummary(cartItems, request);
        }
        //Return Empty Cart content
        return List.of(
                new ModelAndView("fragments/cart-empty :: cart-empty", Collections.emptyMap())
        );
    }

    @PostMapping("/increment/{id}")
    public Collection<ModelAndView> incrementCartQuantity(@PathVariable("id") Long id, HttpServletRequest request){
        //Increment cart quantity
        cartService.updateCartQuantity(id, true);

        String username = userAuthentication.getUsernameFromAuthentication();
        Iterable<Cart> cartItems = cartService.getAllItems(username);
        if(cartItems != null && cartItems.iterator().hasNext()){
            return getCartAndSummary(cartItems, request);
        }
        return null;
    }

    @PostMapping("/decrement/{id}")
    public Collection<ModelAndView> decrementCartQuantity(@PathVariable("id") Long id, HttpServletRequest request){
        //Decrement cart quantity
        cartService.updateCartQuantity(id, false);

        String username = userAuthentication.getUsernameFromAuthentication();
        Iterable<Cart> cartItems = cartService.getAllItems(username);
        if(cartItems != null && cartItems.iterator().hasNext()){
            return getCartAndSummary(cartItems, request);
        }
        return null;
    }

    private OrderSummaryDTO calculateOrderSummary(){

        return new OrderSummaryDTO("12.50", "5.00","10.00",null,"17.50");
    }

    private Collection<ModelAndView> getCartAndSummary(Iterable<Cart> cartItems, HttpServletRequest request) {
        final Set<CardItemDTO> products = cartConverter.convertCartEntityToCartDTO(cartItems);
        //Return to the Cart view
        return List.of(
                new ModelAndView("fragments/cart-items :: cart-items", Map.of(
                        "products", products,
                        "csrfToken", getCSRFToken(request)
                )),
                new ModelAndView("fragments/order-summary :: order-summary", Map.of(
                        "orderSummary", calculateOrderSummary()
                ))
        );
    }

    private CsrfToken getCSRFToken(HttpServletRequest request){
        return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    }

}
