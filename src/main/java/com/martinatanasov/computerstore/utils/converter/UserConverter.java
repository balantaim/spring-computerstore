/*
 * Copyright 2025 Martin Atanasov.
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

package com.martinatanasov.computerstore.utils.converter;


import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.model.UserDetailsDTO;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public UserDetailsDTO userToUserAdministrationDTO(final User user){
        return UserDetailsDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .country(user.getCountry())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .customerId(user.getCustomerId())
                .voucherId(user.getVoucherId())
                .attempts(user.getAttempts())
                .enabled(user.getEnabled())
                .verifiedProfile(user.getVerifiedProfile())
                .creationDate(user.getCreationDate())
                .modifyDate(user.getModifyDate())
                .lockDate(user.getLockDate())
                .roles(user.getRoles())
                .build();
    }

}
