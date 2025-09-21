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

import com.martinatanasov.computerstore.entities.Category;
import com.martinatanasov.computerstore.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    // Example method to retrieve all categories
    @Override
    public Iterable<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Iterable<Category> findAllByIsVisibleTrue() {
        return categoryRepository.findAllByIsVisibleTrue();
    }

    @Override
    public Optional<Category> getCategoryByName(final String name) {
        return categoryRepository.findCategoryByName(name);
    }

}
