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

package com.martinatanasov.computerstore.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WebUser {

    @NotNull(message = "Email is required")
    //@Size(min = 3, max = 50, message = "Valid email is required")
    //@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$#!?@\\-])\\S{2,50}$")
    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,50})$")
    private String email;

    @Size(max = 30, message = "Firstname maximum 30 characters")
    private String firstName;

    @Size(max = 30, message = "Lastname maximum 30 characters")
    private String lastName;

    @NotNull(message = "Valid Password is required")
    @Pattern(regexp = "^(?=.*?[a-zA-Z0-9#?!@$%^&*-]).{4,50}$",
            message = "Password should be at least 4 characters between a-z, uppercase letters or special symbols: #?!@$%^&*-")
    private String password;

    @NotNull(message = "Valid Re-password is required")
    @Pattern(regexp = "^(?=.*?[a-zA-Z0-9#?!@$%^&*-]).{4,50}$",
            message = "Repeat password should be at least 4 characters between a-z, uppercase letters or special symbols: #?!@$%^&*-")
    //This regex is for password with minimal 8 characters and at least 1 lower case, 1 upper case letter and at least 1 special symbol
    //@Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")
    private String repeatPassword;

}
