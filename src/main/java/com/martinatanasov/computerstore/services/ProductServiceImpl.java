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

package com.martinatanasov.computerstore.services;

import com.martinatanasov.computerstore.entities.Product;
import com.martinatanasov.computerstore.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CacheManager cacheManager;

    @Override
    public Page<Product> getAllProducts() {
        return productRepository.findAll(PageRequest.of(1, 5));
    }

    @Override
    public Page<Product> findByIsVisibleTrue() {
        return productRepository.findByIsVisibleTrue(PageRequest.of(1, 5));
    }

    @Cacheable(cacheNames = "productListCache")
    @Override
    public Page<Product> findAllByCategoryId(Short categoryId, Integer pageNumber, Integer pageSize, String sortValue) {
        return productRepository.findAllByCategory(categoryId, buildPageRequest(pageNumber, pageSize, sortValue));
    }

    @Override
    public Page<Product> getAllByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortValue) {
        return productRepository.findAllByKeyword(keyword, buildPageRequest(pageNumber, pageSize, sortValue));
    }

    @Override
    public Page<Product> findAllByKeywordAndIsSearchableTrue(String keyword, Integer pageNumber, Integer pageSize, String sortValue) {
        return productRepository.findAllByKeywordAndIsSearchableTrue(keyword, buildPageRequest(pageNumber, pageSize, sortValue));
    }

    @Override
    public Page<Product> findAllByCompatibleWithAndIsSearchableTrue(String compatibleWith, Integer pageNumber, Integer pageSize, String sortValue) {
        return productRepository.findAllByCompatibleWithAndIsSearchableTrue(compatibleWith, buildPageRequest(pageNumber, pageSize, sortValue));
    }

    @Override
    public List<Product> getAllByKeyword(String keyword) {
        return productRepository.findAllByKeyword(keyword)
                .stream()
                .filter(i -> i.getIsSearchable() == true)
                .collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "productCache", key = "#id")
    @Override
    public Optional<Product> getProductById(Integer id) {
        return productRepository.findProductById(id);
    }

    @Override
    public Optional<Product> getProductByName(String productName) {
        return productRepository.findFirstByProductNameIgnoreCase(productName);
    }

    @Override
    public @Nullable Product updateProductFromManagement(final Integer id, final Boolean isVisible, final Boolean isSearchable) {
        Optional<Product> product = productRepository.findProductById(id);
        if (product.isPresent()) {
            product.get().setIsVisible(isVisible);
            product.get().setIsSearchable(isSearchable);
            //Update the product
            productRepository.save(product.get());
            //Update the cache
            Objects.requireNonNull(cacheManager.getCache("productCache")).evict(id);
            Objects.requireNonNull(cacheManager.getCache("productListCache")).clear();
            return product.get();
        }
        return null;
    }

    public PageRequest buildPageRequest(Integer pageNumber, Integer pageSize, String sortValue) {
        final int DEFAULT_PAGE_NUMBER = 0, DEFAULT_PAGE_SIZE = 25;
        int queryPageNumber, queryPageSize;

        if (pageNumber != null && pageNumber > 0) {
            queryPageNumber = pageNumber - 1;
        } else {
            queryPageNumber = DEFAULT_PAGE_NUMBER;
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
        Sort sort = getProductSort(sortValue);
        return PageRequest.of(queryPageNumber, queryPageSize, sort);
    }

    private Sort getProductSort(String sortValue) {
        return switch (sortValue) {
            case "asc" -> Sort.by(Sort.Order.asc("product_name"));
            case "desc" -> Sort.by(Sort.Order.desc("product_name"));
            case "0-9" -> Sort.by(Sort.Order.asc("price"));
            default -> Sort.by(Sort.Order.desc("price"));
        };
    }

}
