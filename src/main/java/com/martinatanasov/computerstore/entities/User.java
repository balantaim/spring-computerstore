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

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", length = 50, unique = true, nullable = false)
    private String email;

    @Column(name = "password", length = 60, columnDefinition = "CHAR(60)", nullable = false)
    private String password;

    @Column(name = "first_name", length = 30)
    private String firstName;

    @Column(name = "last_name", length = 30)
    private String lastName;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "address", length = 150)
    private String address;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "customer_id", length = 50)
    private String customerId;

    @Column(name = "voucher_id")
    private Long voucherId;

    @Column(name = "attempts")
    private Byte attempts;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "verified_profile")
    private Boolean verifiedProfile;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private Timestamp creationDate;

    @Column(name = "modify_date", nullable = false)
    private Timestamp modifyDate;

    @Column(name = "lock_date")
    private Timestamp lockDate;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;

    //Added Carts oneToMany
    @OneToMany(mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @ToString.Exclude
    private Set<Cart> carts = new HashSet<>();

    //Added Orders oneToMany
    @OneToMany(mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @ToString.Exclude
    private Set<Order> orders = new HashSet<>();

    //Added Reviews oneToMany
    @OneToMany(mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @ToString.Exclude
    private Set<Review> reviews = new HashSet<>();

    public User(String email, String firstName, String lastName, String password, Boolean enabled, Boolean verifiedProfile) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.enabled = enabled;
        this.verifiedProfile = verifiedProfile;
    }

    public User(String email, String firstName, String lastName, String password, String country, String address, String phoneNumber, Boolean enabled, Boolean verifiedProfile, Timestamp creationDate, Collection<Role> roles) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.country = country;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.enabled = enabled;
        this.verifiedProfile = verifiedProfile;
        this.creationDate = creationDate;
        this.roles = roles;
    }

}
