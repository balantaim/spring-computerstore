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

package com.martinatanasov.computerstore.repositories;

import com.martinatanasov.computerstore.entities.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    ProductRepository repository;

    @Test
    void getCPU(){
        Page<Product> products = repository.findAllByCategory(1L, PageRequest.of(1, 3,
                Sort.by(Sort.Order.asc("product_name"))));

        assertThat(products.get()).isNotNull();
        assertThat(products.getTotalPages()).isGreaterThan(0);
        assertThat(products.getTotalElements()).isGreaterThan(0);
    }

    @Test
    void getMonitors(){
        Page<Product> products = repository.findAllByCategory(2L, PageRequest.of(1, 3,
                Sort.by(Sort.Order.asc("product_name"))));

        assertThat(products.get()).isNotNull();
        assertThat(products.getTotalPages()).isGreaterThan(0);
        assertThat(products.getTotalElements()).isGreaterThan(0);
    }

    @Test
    void getVideoCards(){
        Page<Product> products = repository.findAllByCategory(3L, PageRequest.of(1, 3,
                Sort.by(Sort.Order.asc("product_name"))));

        assertThat(products.get()).isNotNull();
        assertThat(products.getTotalPages()).isGreaterThan(0);
        assertThat(products.getTotalElements()).isGreaterThan(0);
    }

}