/*
 * Copyright 2024-2026 Martin Atanasov.
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
import com.martinatanasov.computerstore.model.StoreItemDTO;
import com.martinatanasov.computerstore.services.CategoryService;
import com.martinatanasov.computerstore.services.ProductService;
import com.martinatanasov.computerstore.services.ReviewService;
import com.martinatanasov.computerstore.utils.converter.ProductConverter;
import com.martinatanasov.computerstore.utils.converter.UserAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
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

    private final CategoryService categoryService;
    private final ProductService productService;
    private final ReviewService reviewService;
    private final ProductConverter productConverter;
    private final UserAuthentication userAuthentication;

    @GetMapping("")
    public String products(Model model) {
        Iterable<Category> categories = categoryService.findAllByIsVisibleTrue();
        model.addAttribute("categories", categories);
        return "Products/products";
    }

    @GetMapping("/{category}")
    public String categoryItems(Model model,
            @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "3") Integer pageSize,
            @RequestParam(required = false, defaultValue = "asc") String sortValue,
            @PathVariable("category") String categoryName) {
        Optional<Category> category = categoryService.getCategoryByName(categoryName);
        short categoryId;
        if (category.isEmpty() || category.get().getIsVisible() == false) {
            return NOT_FOUND_PAGE;
        } else {
            categoryId = category.get().getId();
        }
        Page<Product> products = productService.findAllByCategoryId(categoryId, pageNumber, pageSize, sortValue);
        if (!products.isEmpty()) {
            Page<StoreItemDTO> productsDTO = productConverter.convertToStoreItems(products);
            if (pageNumber > productsDTO.getTotalElements() || pageNumber < 1) {
                return NOT_FOUND_PAGE;
            }
            model.addAttribute("categoryName", categoryName);

            model.addAttribute("pageNumber", pageNumber);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("sortValue", sortValue);

            model.addAttribute("products", productsDTO);
            model.addAttribute("totalPages", productsDTO.getTotalPages());
            model.addAttribute("totalItems", productsDTO.getTotalElements());
        }
        return "Products/category-items";
    }

    @GetMapping("/item/{productId}")
    public String itemReview(@PathVariable Integer productId,
            @RequestParam(required = false, defaultValue = "false") Boolean vote,
            Model model) {
        Optional<Product> product = productService.getProductById(productId);
        if (product.isEmpty() || product.get().getIsVisible() == false) {
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
            if (!images.isEmpty()) {
                model.addAttribute("gallery", images);
            }
        });

        product.ifPresent(value -> {
            String username = userAuthentication.getUsernameFromAuthentication();
            ProductReviewsDTO reviews;
            if (username.equals("anonymousUser")) {
                reviews = reviewService.getProductAverageRating(product.get());
                if (reviews != null && reviews.reviewsCount() > 0) {
                    model.addAttribute("reviews", reviews);
                }
            } else {
                reviews = reviewService.getProductAverageRating(username, product.get());
                if (reviews != null) {
                    model.addAttribute("reviews", reviews);
                }
            }
        });

        if (vote != null) {
            model.addAttribute("vote", vote);
        }

        return "Products/product-details";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/add-stars-vote/{productId}")
    public String voteForProduct(@PathVariable Integer productId,
            @RequestParam(value = "rating", required = false) Double starsVote,
            RedirectAttributes redirectAttributes) {
        boolean isSaved = false;
        if (starsVote != null) {
            String username = userAuthentication.getUsernameFromAuthentication();
            if (username.equals("anonymousUser")) {
                return GLOBAL_ERROR_PAGE;
            }
            isSaved = reviewService.voteForProduct(username, productId, starsVote);
        }
        redirectAttributes.addAttribute("vote", isSaved);
        return "redirect:/Products/item/" + productId;
    }

    @GetMapping("/compatible-with")
    public String getCompatibleWithItems(Model model,
            @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "3") Integer pageSize,
            @RequestParam(required = false, defaultValue = "asc") String sortValue,
            @RequestParam("compatibleWith") String compatibleWith) {
        if (compatibleWith != null && !compatibleWith.isEmpty()) {
            Page<Product> products = productService.findAllByCompatibleWithAndIsSearchableTrue(compatibleWith, pageNumber, pageSize, sortValue);
            if (!products.isEmpty()) {
                Page<StoreItemDTO> productsDTO = productConverter.convertToStoreItems(products);
                if (pageNumber > productsDTO.getTotalElements() || pageNumber < 1) {
                    return NOT_FOUND_PAGE;
                }
                model.addAttribute("pageNumber", pageNumber);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("sortValue", sortValue);

                model.addAttribute("products", productsDTO);
                model.addAttribute("totalPages", productsDTO.getTotalPages());
                model.addAttribute("totalItems", productsDTO.getTotalElements());
            }
        }
        return "Products/compatible-with-items";
    }

}