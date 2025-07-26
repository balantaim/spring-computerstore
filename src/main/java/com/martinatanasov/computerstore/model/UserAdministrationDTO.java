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

package com.martinatanasov.computerstore.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.sql.Timestamp;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAdministrationDTO {

    @NotNull
    private Long id;

    @NotBlank
    @NotNull(message = "Email is required")
    @Length(min = 4, max = 50)
    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,50})$")
    private String email;

    @Size(max = 30, message = "Firstname maximum 30 characters")
    private String firstName;

    @Size(max = 30, message = "Lastname maximum 30 characters")
    private String lastName;

    private String country;

    private String address;

    private String phoneNumber;

    private String customerId;

    private Long voucherId;

    private Byte attempts;

    private Boolean enabled;

    private Boolean verifiedProfile;

    private Timestamp creationDate;

    private Timestamp modifyDate;

    private Timestamp lockDate;

}
