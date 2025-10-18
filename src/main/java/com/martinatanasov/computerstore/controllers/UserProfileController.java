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

package com.martinatanasov.computerstore.controllers;

import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.model.ProfileAddressDTO;
import com.martinatanasov.computerstore.model.ProfilePasswordDTO;
import com.martinatanasov.computerstore.model.ReviewDTO;
import com.martinatanasov.computerstore.services.ReviewService;
import com.martinatanasov.computerstore.services.UserService;
import com.martinatanasov.computerstore.utils.converter.AddressConverter;
import com.martinatanasov.computerstore.utils.converter.UserAuthentication;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/Profile")
public class UserProfileController {

    private final UserService userService;
    private final AddressConverter addressConverter;
    private final ReviewService reviewService;
    private final UserAuthentication userAuthentication;

    @GetMapping("")
    public String profile(Model model) {
        model.addAttribute("active", "Profile");
        return "UserProfile/profile";
    }

    @GetMapping("/manage-password")
    public String managePassword(Model model) {
        model.addAttribute("profilePassword", new ProfilePasswordDTO());
        return "UserProfile/manage-password";
    }

    @GetMapping("/address")
    public String addressInfo(Model model) {
        //Get username/email
        final String userName = userAuthentication.getUsernameFromAuthentication();
        User user = userService.findByUserName(userName);

        model.addAttribute("profileAddress", new ProfileAddressDTO());
        model.addAttribute("countries", addressConverter.getSupportedCountries());
        model.addAttribute("user", user);
        return "UserProfile/manage-address";
    }

    @PostMapping("/update-address")
    public String updateOrderAddress(@Valid @ModelAttribute("profileAddress") ProfileAddressDTO profileAddressDTO,
            BindingResult bindingResult,
            Model model) {
        //Get username/email
        final String userName = userAuthentication.getUsernameFromAuthentication();
        final User user = userService.findByUserName(userName);
        //Update user's data
        if (profileAddressDTO == null || bindingResult.hasErrors()) {
            model.addAttribute("status", "error");
        } else {
            userService.updateAddressInformation(
                    userName,
                    profileAddressDTO.getFirstName(),
                    profileAddressDTO.getLastName(),
                    profileAddressDTO.getPhoneNumber(),
                    addressConverter.getCountryNameFromCountryCode(profileAddressDTO.getCountyCode()),
                    profileAddressDTO.getAddress()
            );
            //Add status success to model
            model.addAttribute("status", "success");
        }

        //Redirect to manage-address
        model.addAttribute("profileAddress", new ProfileAddressDTO());
        model.addAttribute("countries", addressConverter.getSupportedCountries());
        model.addAttribute("user", user);
        return "UserProfile/manage-address";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("profilePassword") ProfilePasswordDTO profilePasswordDTO,
            BindingResult bindingResult,
            Model model) {
        //Check for validation errors or global errors
        if (bindingResult.hasErrors()) {
            model.addAttribute("status", "error");
            return "UserProfile/manage-password";
        }
        //Check if the newPassword is different from the oldPassword
        if (profilePasswordDTO.getNewPassword().equals(profilePasswordDTO.getOldPassword())) {
            ObjectError error = new ObjectError("globalError", "SameAsOld");
            bindingResult.addError(error);
            return "UserProfile/manage-password";
        }
        //Check if newPassword and confirmPassword are not equal
        if (!profilePasswordDTO.getNewPassword().equals(profilePasswordDTO.getConfirmPassword())) {
            ObjectError error = new ObjectError("globalError", "ConfirmPass");
            bindingResult.addError(error);
            return "UserProfile/manage-password";
        }
        //Get username
        final String userName = userAuthentication.getUsernameFromAuthentication();
        boolean isPasswordUpdated = userService.changePassword(userName, profilePasswordDTO.getOldPassword(), profilePasswordDTO.getNewPassword());
        //Check if the password is updated successfully
        if (!isPasswordUpdated) {
            model.addAttribute("status", "PassMatcher");
            return "UserProfile/manage-password";
        }
        model.addAttribute("status", "pass-updated");
        return "UserProfile/profile";
    }

    @GetMapping("/reviews")
    public String getReviewsByUser(Model model) {
        final String userName = userAuthentication.getUsernameFromAuthentication();
        if (userName != null) {
            // Retrieve all reviews from the database
            final List<ReviewDTO> reviews = reviewService.findReviewsByUser(userName);
            if (!reviews.isEmpty()) {
                model.addAttribute("reviews", reviews);
            }
        }
        return "UserProfile/reviews";
    }


}
