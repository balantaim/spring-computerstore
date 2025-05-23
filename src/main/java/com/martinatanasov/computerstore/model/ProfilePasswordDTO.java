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

package com.martinatanasov.computerstore.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
public class ProfilePasswordDTO {

    @NotBlank
    @NotNull(message = "Valid Password is required")
    @Pattern(regexp = "^(?=.*?[a-zA-Z0-9#?!@$%^&*-]).{4,50}$",
            message = "Password should be at least 4 characters between a-z, uppercase letters or special symbols: #?!@$%^&*-")
    private String oldPassword;

    @NotBlank
    @NotNull(message = "Valid Password is required")
    @Pattern(regexp = "^(?=.*?[a-zA-Z0-9#?!@$%^&*-]).{4,50}$",
            message = "Password should be at least 4 characters between a-z, uppercase letters or special symbols: #?!@$%^&*-")
    private String newPassword;

    @NotBlank
    @NotNull(message = "Valid Password is required")
    @Pattern(regexp = "^(?=.*?[a-zA-Z0-9#?!@$%^&*-]).{4,50}$",
            message = "Password should be at least 4 characters between a-z, uppercase letters or special symbols: #?!@$%^&*-")
    private String confirmPassword;
}
