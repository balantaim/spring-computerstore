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

package com.martinatanasov.computerstore.repositories;

import com.martinatanasov.computerstore.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserDaoImplTest {

    @Autowired
    UserDao userDao;

    @Test
    @DisplayName(value = "Get basic user")
    void getCustomerDetails(){
        User user = userDao.findByUserName("abv@abv.bg");

        String role = user.getRoles().stream().findFirst().get().getAuthority();
        assertThat(user.getId()).isNotNull();
        assertThat(role).isEqualTo("ROLE_CUSTOMER");
        assertThat(user.getVerifiedProfile()).isTrue();
    }

    @Test
    @DisplayName(value = "Get manager user")
    void getManagerDetails(){
        User user = userDao.findByUserName("manager@abv.bg");

        String role = user.getRoles().stream().skip(1).findFirst().get().getAuthority();
        assertThat(user.getId()).isNotNull();
        assertThat(role).isEqualTo("ROLE_MANAGER");
        assertThat(user.getVerifiedProfile()).isTrue();
    }

    @Test
    @DisplayName(value = "Get admin user")
    void getAdminDetails(){
        User user = userDao.findByUserName("admin@abv.bg");

        String role = user.getRoles().stream().skip(2).findFirst().get().getAuthority();
        assertThat(user.getId()).isNotNull();
        assertThat(role).isEqualTo("ROLE_ADMIN");
        assertThat(user.getVerifiedProfile()).isTrue();
    }

}