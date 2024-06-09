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

package com.martinatanasov.computerstore.entity;

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

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "county")
    private String county;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "verified_profile")
    private boolean verifiedProfile;

    @Column(name = "creation_date")
    private Timestamp creationDate;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;

    //Added Shipments oneToMany
    @OneToMany(mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @ToString.Exclude
    private Set<Shipment> shipments = new HashSet<>();

    //Added Carts oneToMany
    @OneToMany(mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @ToString.Exclude
    private Set<Cart> carts = new HashSet<>();

    //Added Payments oneToMany
    @OneToMany(mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @ToString.Exclude
    private Set<Payment> payments = new HashSet<>();

    public User(String email, String firstName, String lastName, String password, boolean enabled, boolean verifiedProfile) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.enabled = enabled;
        this.verifiedProfile = verifiedProfile;
    }

    public User(String email, String firstName, String lastName, String password, String county, String address, String phoneNumber, boolean enabled, boolean verifiedProfile, Timestamp creationDate, Collection<Role> roles) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.county = county;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.enabled = enabled;
        this.verifiedProfile = verifiedProfile;
        this.creationDate = creationDate;
        this.roles = roles;
    }

}
