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
import com.martinatanasov.computerstore.model.StoreItem;
import com.martinatanasov.computerstore.services.ProductServiceImpl;
import com.martinatanasov.computerstore.util.converter.ProductConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.martinatanasov.computerstore.controllers.CustomErrorController.GLOBAL_ERROR_PAGE;

@Controller
public class HomeController {

    private final ProductServiceImpl productService;
    private final ProductConverter productConverter;

    @Autowired
    HomeController(ProductServiceImpl productService, ProductConverter productConverter){
        this.productService = productService;
        this.productConverter = productConverter;
    }

    @GetMapping("/")
    public String home(Model model){
        Page<Product> getProducts = productService.getAllProducts();
        //Convert product items to StoreItems
        Page<StoreItem> filteredProducts = productConverter.convertToStoreItems(getProducts);
        if (filteredProducts != null){
            model.addAttribute("products", filteredProducts);
        }
        return "Home/index";
    }

    @GetMapping("/Search")
    public String filterByKeyword(Model model,
                                  @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
                                  @RequestParam(required = false, defaultValue = "3") Integer pageSize,
                                  @RequestParam(required = false, defaultValue = "asc") String sortValue,
                                  @RequestParam(required = false, defaultValue = "") String keyword){
        Page<Product> products;
        if(keyword == null || keyword.isEmpty()){
            keyword = "";
        }
        products = productService.getAllByKeyword(keyword, pageNumber, pageSize, sortValue);
        if(products != null){
            Page<StoreItem> dtoProducts = productConverter.convertToStoreItems(products);
            if(pageNumber > dtoProducts.getTotalElements() || pageNumber < 1){
                return GLOBAL_ERROR_PAGE;
            }
            model.addAttribute("keyword", keyword);
            model.addAttribute("pageNumber", pageNumber);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("sortValue", sortValue);

            model.addAttribute("products", dtoProducts);
            model.addAttribute("totalPages", dtoProducts.getTotalPages());
            model.addAttribute("totalItems", dtoProducts.getTotalElements());
        }
        return "Home/search";
    }

    @GetMapping("/Live-search")
    public String liveSearch(@RequestParam("query") String query, Model model){
        List<Product> getProducts = productService.getAllByKeyword(query);
        //Convert Product items to StoreItems
        model.addAttribute("products", productConverter.convertToStoreItems(getProducts));
        return "Home/liveSearch";
    }

    //Remove white spaces
    @InitBinder
    public void initBinder(WebDataBinder webDataBinder){
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

}
