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
import com.martinatanasov.computerstore.model.Country;
import com.martinatanasov.computerstore.model.DeliveryAddressDTO;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class AddressConverter {

    public DeliveryAddressDTO getDeliveryAddress(final User user) {
        return new DeliveryAddressDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                getCodeFromCountryName(user.getCountry()),
                user.getCountry(),
                user.getAddress()
        );
    }

    public String getCountryNameFromCountryCode(final String code) {
        if (code != null) {
            return switch (code) {
                case "BG" -> "Bulgaria";
                case "US" -> "USA";
                case "EN" -> "England";
                default -> throw new IllegalStateException("Country code is not found! Unexpected value: " + code);
            };
        } else {
            return null;
        }
    }

    public String getCodeFromCountryName(final String countryName) {
        if (countryName != null) {
            return switch (countryName) {
                case "Bulgaria" -> "BG";
                case "USA" -> "US";
                case "England" -> "EN";
                default -> throw new IllegalStateException("Country name is not found! Unexpected value: " + countryName);
            };
        } else {
            return null;
        }
    }

    public Set<Country> getSupportedCountries(){
        Set<Country> countries = new LinkedHashSet<>();
        countries.add(new Country("Bulgaria", "BG"));
        countries.add(new Country("USA", "US"));
        countries.add(new Country("England", "EN"));
        return countries;
    }
}
