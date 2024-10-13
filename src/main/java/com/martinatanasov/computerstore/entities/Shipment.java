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

package com.martinatanasov.computerstore.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "shipments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price_per_unit", precision = 9, scale = 2)
    private BigDecimal pricePerUnit;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "carrier", length = 50)
    private String carrier;

//    @Column(name = "county")
//    private String county;
//
//    @Column(name = "address")
//    private String address;
//
//    @Column(name = "phone_number")
//    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "shipment")
    @ToString.Exclude
    private Set<Order> orders = new HashSet<>();

}
