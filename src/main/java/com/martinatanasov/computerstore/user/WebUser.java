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

package com.martinatanasov.computerstore.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class WebUser {

//    @NotNull(message = "{email.notnull}")
    @NotNull(message = "Email is required")
    @Size(min = 3, max = 50, message = "Valid email is required")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$#!?@\\-])\\S{3,50}$")
    private String email;

    @Max(value = 50, message = "Full name with maximum 50 characters")
    private String fullName;
    @NotNull(message = "Valid Password is required")
    @Size(min = 6, max = 30,message = "Password should be between 6 and 30 characters")
    private String password;

    @Max(value = 30, message = "Repeat password should be less or equal than 30 characters")
    private String repeatPassword = "";
}
