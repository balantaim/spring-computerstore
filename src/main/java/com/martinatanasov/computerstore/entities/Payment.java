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

@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "payment_type", length = 20)
    private String paymentType;

    @Column(name = "amount", precision = 9, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_status", length = 20)
    private String paymentStatus;

    @Column(name = "transaction_id")
    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
