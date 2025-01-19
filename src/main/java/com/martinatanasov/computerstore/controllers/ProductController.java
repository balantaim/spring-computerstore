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

import com.martinatanasov.computerstore.entities.Category;
import com.martinatanasov.computerstore.entities.Product;
import com.martinatanasov.computerstore.model.GalleryDTO;
import com.martinatanasov.computerstore.model.ProductReviewsDTO;
import com.martinatanasov.computerstore.model.StoreItem;
import com.martinatanasov.computerstore.services.CategoryServiceImpl;
import com.martinatanasov.computerstore.services.ProductServiceImpl;
import com.martinatanasov.computerstore.services.ReviewServiceImpl;
import com.martinatanasov.computerstore.util.converter.ProductConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.martinatanasov.computerstore.controllers.CustomErrorController.GLOBAL_ERROR_PAGE;
import static com.martinatanasov.computerstore.controllers.CustomErrorController.NOT_FOUND_PAGE;

@RequiredArgsConstructor
@Controller
@RequestMapping("/Products")
public class ProductController {

    private final CategoryServiceImpl categoryService;
    private final ProductServiceImpl productService;
    private final ReviewServiceImpl reviewService;
    private final ProductConverter productConverter;

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
            return NOT_FOUND_PAGE;
        } else {
            categoryId = category.get().getId();
        }
        Page<Product> products = productService.findAllByCategoryId(categoryId, pageNumber, pageSize, sortValue);
        if(!products.isEmpty()){
            Page<StoreItem> dtoProducts = productConverter.convertToStoreItems(products);
            if(pageNumber > dtoProducts.getTotalElements() || pageNumber < 1){
                return NOT_FOUND_PAGE;
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
                             @RequestParam(required = false, defaultValue = "false") Boolean vote,
                             Model model){
        Optional<Product> product = productService.getProductById(productId);
        if(product.isEmpty()){
            return NOT_FOUND_PAGE;
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
            if(!images.isEmpty()){
                model.addAttribute("gallery", images);
            }
        });

        product.ifPresent(value -> {
            String username = getUsernameFromAuthentication();
            ProductReviewsDTO reviews;
            if(username.equals("anonymousUser") || username == null){
                reviews = reviewService.getProductAverageRating(product.get());
                if(reviews != null && reviews.reviewsCount() > 0){
                    model.addAttribute("reviews", reviews);
                }
            } else {
                reviews = reviewService.getProductAverageRating(username, product.get());
                if(reviews != null){
                    model.addAttribute("reviews", reviews);
                }
            }
        });

        if(vote != null){
            model.addAttribute("vote", vote);
        }

        return "Products/product-details";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/add-stars-vote/{productId}")
    public String voteForProduct(@PathVariable(value = "productId") Integer productId,
                                 @RequestParam(value = "rating", required = false) Double starsVote,
                                 RedirectAttributes redirectAttributes){
        boolean isSaved = false;
        if(starsVote != null){
            String username = getUsernameFromAuthentication();
            if(username.equals("anonymousUser") || username == null){
                return GLOBAL_ERROR_PAGE;
            }
            isSaved = reviewService.voteForProduct(username, productId, starsVote);
        }
        redirectAttributes.addAttribute("vote", isSaved);
        return "redirect:/Products/item/" + productId;
    }

    private String getUsernameFromAuthentication(){
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

}