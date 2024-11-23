/*
 * Copyright 2024 Martin Atanasov.
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

import com.martinatanasov.computerstore.entities.Product;
import com.martinatanasov.computerstore.model.CardItemDTO;
import com.martinatanasov.computerstore.services.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
@Controller
public class CartItemsController {

    private final ProductServiceImpl productService;

    @GetMapping("/Cart-items")
    public String cartItems(){
        return "Cart/cart-items";
    }

    @GetMapping("/Cart-add/{itemId}")
    public String addToCard(Model model,
                            @RequestParam(value = "promoCode", required = false) String promoCode,
                            @PathVariable("itemId") Integer itemId){
        Optional<Product> product = productService.getProductById(itemId);
        if (product.isPresent()){
            CardItemDTO cardItemDTO = new CardItemDTO(
                    product.get().getId(),
                    product.get().getProductName(),
                    product.get().getProducer(),
                    product.get().getImageUrl(),
                    product.get().getCategory().getName(),
                    product.get().getStock(),
                    product.get().getPrice()
            );
            model.addAttribute("product", cardItemDTO);
            if(promoCode != null){
                if(promoCode.length() > 3){
                    model.addAttribute("promoCode", promoCode);
                }
            }
            return "Cart/cart-add";
        }
        return "error/404";
    }


}
