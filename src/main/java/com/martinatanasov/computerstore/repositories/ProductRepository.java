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

package com.martinatanasov.computerstore.repositories;

import com.martinatanasov.computerstore.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT * FROM products p WHERE p.category_id = :id", nativeQuery = true)
    Page<Product> findAllByCategory(Short id, PageRequest pageRequest);

    @Query(value = "SELECT * FROM products p WHERE p.product_name LIKE %:keyword% " +
            "OR p.description LIKE %:keyword% " +
            "OR p.producer LIKE %:keyword% " +
            "OR p.price LIKE %:keyword%",
            nativeQuery = true)
    Page<Product> findAllByKeyword(@Param("keyword") String keyword, PageRequest pageRequest);

    @Query(value = "SELECT * FROM products p WHERE p.product_name LIKE %:keyword% " +
            "OR p.description LIKE %:keyword% " +
            "OR p.producer LIKE %:keyword% " +
            "OR p.price LIKE %:keyword%",
            nativeQuery = true)
    List<Product> findAllByKeyword(@Param("keyword") String keyword);

    @Query(value = "SELECT * FROM products p WHERE " +
            "(p.product_name LIKE %:keyword% " +
            "OR p.description LIKE %:keyword% " +
            "OR p.producer LIKE %:keyword% " +
            "OR p.price LIKE %:keyword%) " +
            "AND p.is_searchable = 1",
            nativeQuery = true)
    Page<Product> findAllByKeywordAndIsSearchableTrue(@Param("keyword") String keyword, PageRequest pageRequest);

    @Query(value = "SELECT * FROM products p WHERE " +
            "p.compatible_with LIKE %:compatibleWith% " +
            "AND p.is_searchable = 1",
            nativeQuery = true)
    Page<Product> findAllByCompatibleWithAndIsSearchableTrue(@Param("compatibleWith") String compatibleWith, PageRequest pageRequest);

    Optional<Product> findProductById(Integer id);

    Page<Product> findByIsVisibleTrue(PageRequest pageRequest);

    Optional<Product> findFirstByProductNameIgnoreCase(String productName);

}
