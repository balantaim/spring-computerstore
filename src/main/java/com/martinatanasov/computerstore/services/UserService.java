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

package com.martinatanasov.computerstore.services;

import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.model.AppUserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    User findByUserName(final String email);

    User findByUserId(final Long id);

    User findByCustomerId(final String customerId);

    void save(AppUserDTO appUserDTO);

    void save(User user);

    boolean changePassword(final String userName, final String oldPassword, final String newPassword);

    boolean setNewPassword(final String userName, final String newPassword);

    void updateAddressInformation(final String username, final String firstName, final String lastName, final String phone, final String country, final String address);

    void delete(final String email);

    void setAccountStatus(final String email, final Boolean enabled);

    List<User> getUsersDetailsInfo();

    void disableOrEnableUser(Long userId, boolean enabled, boolean verified);

}
