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

package com.martinatanasov.computerstore.utils.converter;


import com.martinatanasov.computerstore.entities.Gallery;
import com.martinatanasov.computerstore.entities.Product;
import com.martinatanasov.computerstore.model.GalleryDTO;
import com.martinatanasov.computerstore.model.StoreItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductConverter {

    public List<StoreItemDTO> convertToStoreItems(final List<Product> products){
        return products.stream()
                .map(i -> new StoreItemDTO(i.getId(),
                        i.getProductName(),
                        i.getDescription(),
                        i.getProducer(),
                        //Convert Double to string with exponent 2
                        String.format("%.2f", i.getPrice()),
                        i.getStock(),
                        i.getImageUrl(),
                        i.getProductNumber(),
                        i.getCompatibleWith(),
                        i.getBarcodeUtc(),
                        i.getProductSpecifications()))
                .collect(Collectors.toList());
    }

    public Page<StoreItemDTO> convertToStoreItems(final Page<Product> products){
        return products
                .map(i -> new StoreItemDTO(i.getId(),
                        i.getProductName(),
                        i.getDescription(),
                        i.getProducer(),
                        //Convert Double to string with exponent 2
                        String.format("%.2f", i.getPrice()),
                        i.getStock(),
                        i.getImageUrl(),
                        i.getProductNumber(),
                        i.getCompatibleWith(),
                        i.getBarcodeUtc(),
                        i.getProductSpecifications()
                ));
    }

    public StoreItemDTO convertToSingleItem(final Product product){
        return new StoreItemDTO(product.getId(),
                product.getProductName(),
                product.getDescription(),
                product.getProducer(),
                //Convert Double to string with exponent 2
                String.format("%.2f", product.getPrice()),
                product.getStock(),
                product.getImageUrl(),
                product.getProductNumber(),
                product.getCompatibleWith(),
                product.getBarcodeUtc(),
                product.getProductSpecifications());
    }

    public GalleryDTO convertToGalleryItem(final Gallery gallery){
        return new GalleryDTO(gallery.getId(), gallery.getImageUrl());
    }

}
