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

package com.martinatanasov.computerstore.util.converter;

import com.martinatanasov.computerstore.entity.Product;
import com.martinatanasov.computerstore.model.StoreItem;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductConverter {

    @Bean
    public List<StoreItem> convertToStoreItems(final List<Product> products){
        return products.stream()
                .map(i -> new StoreItem(i.getId(),
                        i.getProductName(),
                        i.getDescription(),
                        i.getProducer(),
                        //Convert Double to string with exponent 2
                        String.format("%.2f", i.getPrice()),
                        i.getStock(),
                        i.getImageUrl()))
                .collect(Collectors.toList());
    }

    @Bean
    public StoreItem convertToSingleItem(final Product product){
        return new StoreItem(product.getId(),
                product.getProductName(),
                product.getDescription(),
                product.getProducer(),
                //Convert Double to string with exponent 2
                String.format("%.2f", product.getPrice()),
                product.getStock(),
                product.getImageUrl());
    }

}
