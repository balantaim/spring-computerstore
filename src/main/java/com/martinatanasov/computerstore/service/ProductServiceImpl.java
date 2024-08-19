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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final int DEFAULT_PAGE = 0;
    private final int DEFAULT_PAGE_SIZE = 25;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Page<Product> getAllProducts() {
        return productRepository.findAll(PageRequest.of(1, 10));
    }

    @Override
    public Page<Product> findAllByCategoryId(Long categoryId,  Integer pageNumber, Integer pageSize, String sortValue){
        return productRepository.findAllByCategory(categoryId, buildPageRequest(pageNumber, pageSize, sortValue));
    }

    @Override
    public Page<Product> getAllByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortValue) {
        return productRepository.findAllByKeyword(keyword, buildPageRequest(pageNumber, pageSize, sortValue));
    }

    @Override
    public List<Product> getAllByKeyword(String keyword) {
        return productRepository.findAllByKeyword(keyword);
    }

    @Override
    public Optional<Product> getProductById(Long id){
        return productRepository.findProductById(id);
    }

    public PageRequest buildPageRequest(Integer pageNumber, Integer pageSize, String sortValue) {
        int queryPageNumber;
        int queryPageSize;

        if (pageNumber != null && pageNumber > 0) {
            queryPageNumber = pageNumber - 1;
        } else {
            queryPageNumber = DEFAULT_PAGE;
        }
        if (pageSize == null) {
            queryPageSize = DEFAULT_PAGE_SIZE;
        } else {
            if (pageSize > 1000) {
                queryPageSize = 1000;
            } else {
                queryPageSize = pageSize;
            }
        }
        Sort sort;
        if(sortValue.equals("asc") || sortValue == null){
            sort = Sort.by(Sort.Order.asc("product_name"));
        } else if (sortValue.equals("desc")){
            sort = Sort.by(Sort.Order.desc("product_name"));
        } else if (sortValue.equals("0-9")) {
            sort = Sort.by(Sort.Order.asc("price"));
        } else {
            sort = Sort.by(Sort.Order.desc("price"));
        }

        return PageRequest.of(queryPageNumber, queryPageSize, sort);
    }


}
