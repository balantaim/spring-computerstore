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

import com.martinatanasov.computerstore.entity.Product;
import com.martinatanasov.computerstore.service.ProductService;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private ProductService productService;

    @Autowired
    HomeController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping("/")
    public String home(Model model){
        List<Product> getProducts = productService.getAllProducts();
        List<Product> filteredProducts = getProducts.stream()
                        .filter(i -> i.getCategory().getId() == 2 || (i.getId() > 5 && i.getId() <= 7))
                        .collect(Collectors.toList());
        model.addAttribute("products", filteredProducts);
        model.addAttribute("active","Home");
        return "Home/index";
    }

    @GetMapping("/Search")
    public String filterByKeyword(Model model, String keyword){
        List<Product> getProducts = productService.getAllByKeyword(keyword);
        if(getProducts != null){
            model.addAttribute("products", getProducts);
        }
        return "Home/search";
    }

    @GetMapping("/Live-search")
    public String liveSearch(@RequestParam("query") String query, Model model){
        List<Product> getProducts = productService.getAllByKeyword(query);
        model.addAttribute("products", getProducts);
        return "Home/liveSearch";
    }


    //Remove white spaces
//    @InitBinder
//    public void initBinder(WebDataBinder webDataBinder){
//        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
//        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
//    }

}
