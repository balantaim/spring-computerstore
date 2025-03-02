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

package com.martinatanasov.computerstore.controllers;

import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.model.Carrier;
import com.martinatanasov.computerstore.services.ProfileServiceImpl;
import com.martinatanasov.computerstore.utils.converter.AddressConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@PreAuthorize("hasRole('CUSTOMER')")
@RequestMapping("/Checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final ProfileServiceImpl profileService;
    private final AddressConverter addressConverter;

    @PostMapping("/step-1")
    public String initiateCheckoutInformation(Model model) {
        final User user = profileService.getUserData(getUserName());
        if(user != null) {
            model.addAttribute("address", addressConverter.userAddressToDTO(user));
        }
        return "Checkout/checkout-step-1";
    }

    @GetMapping("/step-1")
    public String refreshCheckoutInformation(Model model) {
        final User user = profileService.getUserData(getUserName());
        if(user != null) {
            model.addAttribute("address", addressConverter.userAddressToDTO(user));
        }
        return "Checkout/checkout-step-1";
    }

    @PostMapping("/step-2")
    public String initiateCheckoutPayment(@RequestParam("carrier") String carrier, Model model) {
        Carrier carrierName = Objects.equals(carrier, Carrier.ECONT.name()) ? Carrier.ECONT:Carrier.SPEEDY;
        model.addAttribute("carrier", carrierName);
        return "Checkout/checkout-step-2";
    }

    @PostMapping("/step-3")
    public String initiateCheckoutConfirmation(@RequestParam("carrier") Carrier carrier) {
        return "Checkout/checkout-step-3";
    }

    private String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
