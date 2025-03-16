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

package com.martinatanasov.computerstore.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price_per_unit", precision = 9, scale = 2)
    private BigDecimal pricePerUnit;

    //FK referencing Order
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude
    private Order order;

    //FK referencing Product
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    private Product product;

}
