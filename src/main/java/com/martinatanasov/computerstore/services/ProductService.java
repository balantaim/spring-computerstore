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

package com.martinatanasov.computerstore.services;

import com.martinatanasov.computerstore.entities.Product;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Page<Product> getAllProducts();

    Page<Product> findByIsVisibleTrue();

    Page<Product> findAllByCategoryId(Short categoryId, Integer pageNumber, Integer pageSize, String sortValue);

    Page<Product> getAllByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortValue);

    Page<Product> findAllByKeywordAndIsSearchableTrue(String keyword, Integer pageNumber, Integer pageSize, String sortValue);

    Page<Product> findAllByCompatibleWithAndIsSearchableTrue(String compatibleWith,  Integer pageNumber, Integer pageSize, String sortValue);

    List<Product> getAllByKeyword(String keyword);

    Optional<Product> getProductById(Integer id);

    Optional<Product> getProductByName(String productName);

}
