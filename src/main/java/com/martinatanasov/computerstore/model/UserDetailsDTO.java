/*
 * Copyright 2025-2026 Martin Atanasov.
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

package com.martinatanasov.computerstore.model;

import com.martinatanasov.computerstore.entities.Role;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

import java.sql.Timestamp;
import java.util.Collection;

@Builder
public record UserDetailsDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        String country,
        String address,
        String phoneNumber,
        String customerId,
        @Nullable Long voucherId,
        Byte attempts,
        Boolean enabled,
        Boolean accountNonLocked,
        Boolean verifiedProfile,
        Timestamp creationDate,
        Timestamp modifyDate,
        Timestamp lockDate,
        Collection<Role> roles
) {
}
