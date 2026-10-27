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

package com.martinatanasov.computerstore.repositories;

import com.martinatanasov.computerstore.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT p FROM Product p WHERE p.category.id = :id")
    Page<Product> findAllByCategory(@Param("id") Short id, Pageable pageable);

    //Old native query method
//    @Query(value = "SELECT * FROM products p WHERE p.product_name LIKE %:keyword% " +
//            "OR p.description LIKE %:keyword% " +
//            "OR p.producer LIKE %:keyword% " +
//            "OR p.price LIKE %:keyword%",
//            nativeQuery = true)
//    Page<Product> findAllByKeyword(@Param("keyword") String keyword, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT p FROM Product p WHERE " +
            "p.productName LIKE CONCAT('%', :keyword, '%') " +
            "OR p.description LIKE CONCAT('%', :keyword, '%') " +
            "OR p.producer LIKE CONCAT('%', :keyword, '%') " +
            "OR CAST(p.price AS string) LIKE CONCAT('%', :keyword, '%')")
    Page<Product> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT p FROM Product p WHERE " +
            "p.productName LIKE CONCAT('%', :keyword, '%') " +
            "OR p.description LIKE CONCAT('%', :keyword, '%') " +
            "OR p.producer LIKE CONCAT('%', :keyword, '%') " +
            "OR CAST(p.price AS string) LIKE CONCAT('%', :keyword, '%')")
    List<Product> findAllByKeyword(@Param("keyword") String keyword);

    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT p FROM Product p WHERE " +
            "(p.productName LIKE CONCAT('%', :keyword, '%') " +
            "OR p.description LIKE CONCAT('%', :keyword, '%') " +
            "OR p.producer LIKE CONCAT('%', :keyword, '%') " +
            "OR CAST(p.price AS string) LIKE CONCAT('%', :keyword, '%')) " +
            "AND p.isSearchable = true")
    Page<Product> findAllByKeywordAndIsSearchableTrue(@Param("keyword") String keyword, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT p FROM Product p WHERE " +
            "p.compatibleWith LIKE CONCAT('%', :compatibleWith, '%') " +
            "AND p.isSearchable = true")
    Page<Product> findAllByCompatibleWithAndIsSearchableTrue(@Param("compatibleWith") String compatibleWith, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"category", "galleries"})
    Optional<Product> findProductById(Integer id);

    Page<Product> findByIsVisibleTrue(PageRequest pageRequest);

    Page<Product> findByIsVisibleTrue(Pageable pageable);

    Optional<Product> findFirstByProductNameIgnoreCase(String productName);

}
