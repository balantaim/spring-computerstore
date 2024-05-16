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

package com.martinatanasov.computerstore.controller;

import com.martinatanasov.computerstore.entity.Category;
import com.martinatanasov.computerstore.entity.Product;
import com.martinatanasov.computerstore.service.CategoryService;
import com.martinatanasov.computerstore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/Products")
public class ProductController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @Autowired
    public ProductController(CategoryService categoryService, ProductService productService){
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("")
    public String products(Model model){
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "Products/products";
    }

    @GetMapping("/cpu")
    public String cpu(Model model){
        List<Product> products = productService.findAllByCategoryId(1L);
        if(!CollectionUtils.isEmpty(products)){
//            List<Product> p = products
//                    .stream()
//                    .filter(index -> index.getProductName().equals("Intel Core i9-13900KS (2.4GHz)"))
//                    .collect(Collectors.toList());
            model.addAttribute("products", products);
        }
        return "Products/cpu";
    }

    @GetMapping("/monitors")
    public String monitors(Model model){
        List<Product> products = productService.findAllByCategoryId(2L);
        if(!CollectionUtils.isEmpty(products)){
            model.addAttribute("products", products);
        }
        return "Products/monitors";
    }

    @GetMapping("/video-cards")
    public String videoCards(Model model){
        List<Product> products = productService.findAllByCategoryId(3L);
        if(!CollectionUtils.isEmpty(products)){
            model.addAttribute("products", products);
        }
        return "Products/video-cards";
    }

    @GetMapping("/{productId}")
    public String itemReview(@PathVariable(value = "productId") Long productId,
                             Model model){
        Optional<Product> product = productService.getProductById(productId);
        if(product.isPresent()){
            model.addAttribute("product", product.get());
        }
        return "Products/review";
    }
}
