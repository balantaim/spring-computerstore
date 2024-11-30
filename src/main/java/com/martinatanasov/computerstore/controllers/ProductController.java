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

import com.martinatanasov.computerstore.entities.Category;
import com.martinatanasov.computerstore.entities.Product;
import com.martinatanasov.computerstore.model.GalleryDTO;
import com.martinatanasov.computerstore.model.StoreItem;
import com.martinatanasov.computerstore.services.CategoryServiceImpl;
import com.martinatanasov.computerstore.services.ProductServiceImpl;
import com.martinatanasov.computerstore.util.converter.ProductConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/Products")
public class ProductController {

    private final CategoryServiceImpl categoryService;
    private final ProductServiceImpl productService;
    private final ProductConverter productConverter;

    @Autowired
    ProductController(CategoryServiceImpl categoryService, ProductServiceImpl productService, ProductConverter productConverter){
        this.categoryService = categoryService;
        this.productService = productService;
        this.productConverter = productConverter;
    }

    @GetMapping("")
    public String products(Model model){
        Iterable<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "Products/products";
    }

    @GetMapping("/{category}")
    public String categoryItems(Model model,
                               @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
                               @RequestParam(required = false, defaultValue = "3") Integer pageSize,
                               @RequestParam(required = false, defaultValue = "asc") String sortValue,
                               @PathVariable("category") String categoryName){
        Optional<Category> category = categoryService.getCategoryByName(categoryName);
        short categoryId;
        if(category.isEmpty()){
            return "error/404";
        } else {
            categoryId = category.get().getId();
        }
        Page<Product> products = productService.findAllByCategoryId(categoryId, pageNumber, pageSize, sortValue);
        if(!products.isEmpty()){
            Page<StoreItem> dtoProducts = productConverter.convertToStoreItems(products);
            if(pageNumber > dtoProducts.getTotalElements() || pageNumber < 1){
                return "error/404";
            }
            model.addAttribute("categoryName", categoryName);

            model.addAttribute("pageNumber", pageNumber);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("sortValue", sortValue);

            model.addAttribute("products", dtoProducts);
            model.addAttribute("totalPages", dtoProducts.getTotalPages());
            model.addAttribute("totalItems", dtoProducts.getTotalElements());
        }
        return "Products/category-items";
    }

    @GetMapping("/item/{productId}")
    public String itemReview(@PathVariable(value = "productId") Integer productId,
                             Model model){
        Optional<Product> product = productService.getProductById(productId);
        if(product.isEmpty()){
            return "error/404";
        }
        product.ifPresent(value -> model.addAttribute("product", productConverter.convertToSingleItem(value)));
        product.ifPresent(value -> {
            String categoryName = product.get().getCategory().getName();
            model.addAttribute("categoryName", categoryName);
        });

        product.ifPresent(value -> {
            Set<GalleryDTO> images = value.getGalleries()
                    .stream()
                    .map(productConverter::convertToGalleryItem)
                    .collect(Collectors.toSet());
            model.addAttribute("gallery", images);
        });
        return "Products/product-details";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/add-stars-vote/{productId}")
    public String voteForProduct(@PathVariable(value = "productId") Integer productId,
                                 @RequestParam(value = "rating", required = false) Double starsVote){
        if(starsVote != null){
            System.out.println("\n\tStar vote ->>>> " + starsVote);
        }
        return "redirect:/Products/item/" + productId;
    }

}