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

package com.martinatanasov.computerstore.repositories;

import com.martinatanasov.computerstore.entities.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class UserDaoImplIT {

    @Autowired
    UserDao userDao;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName(value = "Get basic user")
    void getCustomerDetails() {
        User user = userDao.findByUserName("abv@abv.bg");

        String role = user.getRoles().stream().findFirst().get().getAuthority();
        assertThat(user.getId()).isNotNull();
        assertThat(role).isEqualTo("ROLE_CUSTOMER");
        assertThat(user.getVerifiedProfile()).isTrue();
    }

    @Test
    @DisplayName(value = "Get manager user")
    void getManagerDetails() {
        User user = userDao.findByUserName("manager@abv.bg");

        String role = user.getRoles().stream().skip(1).findFirst().get().getAuthority();
        assertThat(user.getId()).isNotNull();
        assertThat(role).isEqualTo("ROLE_MANAGER");
        assertThat(user.getVerifiedProfile()).isTrue();
    }

    @Test
    @DisplayName(value = "Get admin user")
    void getAdminDetails() {
        User user = userDao.findByUserName("admin@abv.bg");

        String role = user.getRoles().stream().skip(2).findFirst().get().getAuthority();
        assertThat(user.getId()).isNotNull();
        assertThat(role).isEqualTo("ROLE_ADMIN");
        assertThat(user.getVerifiedProfile()).isTrue();
    }

    @Test
    @Order(1)
    @DisplayName(value = "Create customer user")
    void createCustomerUser() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        User user = new User();
        user.setEmail("testcustomer.default@abv.bg");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setFirstName("First name");
        user.setLastName("Last name");
        user.setEnabled(true);
        user.setAttempts((byte) 0);
        //The default new profile is set to verified by email
        user.setVerifiedProfile(true);
        //Set creation date
        user.setCreationDate(timestamp);
        user.setModifyDate(timestamp);
        user.setLockDate(timestamp);

        userDao.save(user);
        User returedUser = userDao.findByUserName(user.getEmail());

        assertThat(returedUser).isNotNull();
        assertThat(returedUser.getEnabled()).isTrue();
        assertThat(user.getEmail()).isEqualTo(returedUser.getEmail());
        assertThat(returedUser.getId()).isNotNull();
    }

    @Test
    @Order(2)
    @DisplayName(value = "Delete customer account")
    void deleteCustomerUser() {
        final String email = "testcustomer.default@abv.bg";
        userDao.deleteByUserEmail(email);
        User returedUser = userDao.findByUserName(email);

        assertThat(returedUser).isNull();
    }

}
