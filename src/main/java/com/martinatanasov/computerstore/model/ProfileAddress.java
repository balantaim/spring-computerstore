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
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileAddress {

    @NotNull
    @Size(max = 30, message = "Lastname maximum 30 characters")
    private String firstName;

    @NotNull
    @Size(max = 30, message = "Lastname maximum 30 characters")
    private String lastName;

    // International "+12345678901234"
    // North American "+1 123-456-7890"
    // North American without country code "123-456-7890"
    // International "+44 7911 123456"
    // North American without country code "555-555-5555"
    //@Pattern(regexp = "^(?:\\+?[1-9]\\d{1,14}|\\+1\\s?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4})$", message = "Valid phone number is required")
    @NotNull
    @Size(max = 20, message = "Phone number maximum 20 characters")
    private String phoneNumber;

    @NotBlank
    @NotNull(message = "Country code is required")
    @Size(min = 2, max = 3)
    private String countyName;

    @Pattern(regexp = "^.{0,150}$", message = "Max characters for address 150")
    private String address;
}
