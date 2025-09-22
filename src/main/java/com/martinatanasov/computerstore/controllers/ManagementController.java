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
import com.martinatanasov.computerstore.services.CategoryService;
import com.martinatanasov.computerstore.services.ProductService;
import com.martinatanasov.computerstore.utils.converter.ProductConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.martinatanasov.computerstore.controllers.CustomErrorController.GLOBAL_ERROR_PAGE;
import static com.martinatanasov.computerstore.controllers.CustomErrorController.NOT_FOUND_PAGE;

@Controller
@PreAuthorize("hasRole('MANAGER')")
@RequiredArgsConstructor
public class ManagementController {

    private final ProductService productService;
    private final ProductConverter productConverter;
    private final CategoryService categoryService;

    @GetMapping("/Management")
    public String management(Model model) {
        Iterable<Category> categories = categoryService.findAllByIsVisibleTrue();
        model.addAttribute("categories", categories);
        return "Management/management";
    }

    @GetMapping("/Management/{category}")
    public String getProductsByCategory(Model model,
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
        final Page<Product> products = productService.findAllByCategoryId(categoryId, pageNumber, pageSize, sortValue);

        if (products != null) {
            model.addAttribute("products", productConverter.productToProductManagementDTO(products));
            model.addAttribute("categoryName", categoryName);
            model.addAttribute("pageNumber", pageNumber);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("sortValue", sortValue);
            model.addAttribute("totalPages", products.getTotalPages());
            model.addAttribute("totalItems", products.getTotalElements());
        }
        return "Management/management-products";
    }

    @PostMapping("/Management/update/{category}/{id}")
    public String updateProduct(@PathVariable Integer id,
                                @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
                                @RequestParam(required = false, defaultValue = "3") Integer pageSize,
                                @RequestParam(required = false, defaultValue = "asc") String sortValue,
                                @PathVariable("category") String categoryName,
                                @RequestParam("isVisible") Boolean isVisible,
                                @RequestParam("isSearchable") Boolean isSearchable) {
        if (id != null && isVisible != null && isSearchable != null && categoryName != null) {
            productService.updateProductFromManagement(id, isVisible, isSearchable);
            //Redirect to previous page
            return "redirect:/Management/" + categoryName + "?pageNumber=" + pageNumber + "&pageSize=" + pageSize + "&sortValue=" + sortValue;
        }
        return GLOBAL_ERROR_PAGE;
    }

}
