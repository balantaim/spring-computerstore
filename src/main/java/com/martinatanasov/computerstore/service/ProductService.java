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

package com.martinatanasov.computerstore.service;

import com.martinatanasov.computerstore.dao.ProductRepository;
import com.martinatanasov.computerstore.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Example method to retrieve all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> findAllByCategoryId(Long categoryId){
        return productRepository.findAllByCategory(categoryId);
    }

    public List<Product> getAllByKeyword(String keyword) {
        return productRepository.findAllByKeyword(keyword);
    }

    public Optional<Product> getProductById(Long id){
        return productRepository.findProductById(id);
    }
}
