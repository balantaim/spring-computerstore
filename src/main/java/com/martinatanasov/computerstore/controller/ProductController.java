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
import com.martinatanasov.computerstore.model.StoreItem;
import com.martinatanasov.computerstore.service.CategoryService;
import com.martinatanasov.computerstore.service.ProductService;
import com.martinatanasov.computerstore.util.converter.ProductConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/Products")
public class ProductController {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final ProductConverter productConverter;

    @Autowired
    public ProductController(CategoryService categoryService, ProductService productService, ProductConverter productConverter){
        this.categoryService = categoryService;
        this.productService = productService;
        this.productConverter = productConverter;
    }

    @GetMapping("")
    public String products(Model model){
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "Products/products";
    }

    @GetMapping("/cpu")
    public String cpu(Model model,
                      @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
                      @RequestParam(required = false, defaultValue = "2") Integer pageSize,
                      @RequestParam(required = false, defaultValue = "asc") String sortValue){
        Page<Product> products = productService.findAllByCategoryId(1L, pageNumber, pageSize, sortValue);
        if(!products.isEmpty()){
            Page<StoreItem> dtoProducts = productConverter.convertToStoreItems(products);
            model.addAttribute("pageNumber", pageNumber);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("sortValue", sortValue);

            model.addAttribute("products", dtoProducts);
            model.addAttribute("totalPages", dtoProducts.getTotalPages());
            model.addAttribute("totalItems", dtoProducts.getTotalElements());
        }
        return "Products/cpu";
    }

//    @GetMapping("/monitors")
//    public String monitors(Model model){
//        List<Product> products = productService.findAllByCategoryId(2L);
//        if(!CollectionUtils.isEmpty(products)){
//            model.addAttribute("products", productConverter.convertToStoreItems(products));
//        }
//        return "Products/monitors";
//    }

    @GetMapping("/video-cards")
    public String videoCards(Model model,
                             @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
                             @RequestParam(required = false, defaultValue = "2") Integer pageSize,
                             @RequestParam(required = false, defaultValue = "asc") String sortValue){
        Page<Product> products = productService.findAllByCategoryId(3L, pageNumber, pageSize, sortValue);
        if(!products.isEmpty()){
            Page<StoreItem> dtoProducts = productConverter.convertToStoreItems(products);
            model.addAttribute("pageNumber", pageNumber);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("sortValue", sortValue);

            model.addAttribute("products", dtoProducts);
            model.addAttribute("totalPages", dtoProducts.getTotalPages());
            model.addAttribute("totalItems", dtoProducts.getTotalElements());
        }
        return "Products/video-cards";
    }

    @GetMapping("/{productId}")
    public String itemReview(@PathVariable(value = "productId") Long productId,
                             Model model){
        Optional<Product> product = productService.getProductById(productId);
        if(product.isPresent()){
            model.addAttribute("product", productConverter.convertToSingleItem(product.get()));
        }
        return "Products/review";
    }
}
