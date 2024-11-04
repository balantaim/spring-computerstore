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

package com.martinatanasov.computerstore.services;

import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.model.AppUserDTO;
import com.martinatanasov.computerstore.model.UserInfoDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Set;

public interface UserService extends UserDetailsService {

    User findByUserName(final String email);

    User findByUserId(final Long id);

    void save(AppUserDTO appUserDTO);

    boolean changePassword(final String userName, final String oldPassword, final String newPassword);

    boolean setNewPassword(final String userName, final String newPassword);

    void delete(final String email);

    void setAccountStatus(final String email, final Boolean enabled);

    Set<UserInfoDTO> getUsersInfo();

}
