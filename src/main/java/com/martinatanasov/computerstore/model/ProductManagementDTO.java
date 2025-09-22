/*
 * Copyright 2025 Martin Atanasov.
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

package com.martinatanasov.computerstore.model;

import com.martinatanasov.computerstore.entities.Category;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;

public record ProductManagementDTO(
        @NotNull
        Integer id,
        String productName,
        String description,
        String producer,
        String price,
        @NotNull
        Integer stock,
        String imageUrl,
        Timestamp creationDate,
        Timestamp modifyDate,
        @NotNull
        Boolean isVisible,
        @NotNull
        Boolean isSearchable,
        String productNumber,
        String compatibleWith,
        String barcodeUtc,
        Category category
) {
}
