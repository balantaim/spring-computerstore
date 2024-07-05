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

package com.martinatanasov.computerstore.controller;


import com.martinatanasov.computerstore.entity.User;
import com.martinatanasov.computerstore.model.Country;
import com.martinatanasov.computerstore.model.ProfileAddress;
import com.martinatanasov.computerstore.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/Profile")
public class UserProfileController {

    private final ProfileService profileService;

    @Autowired
    public UserProfileController(ProfileService profileService){
        this.profileService = profileService;
    }

    @GetMapping("")
    public String profile(Model model){
        model.addAttribute("active","Profile");
        return "UserProfile/profile";
    }

    @GetMapping("/manage-password")
    public String managePassword(){
        return "UserProfile/manage-password";
    }

    @GetMapping("/address")
    public String addressInfo(Model model){
        //Get user name/email
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName(); // Get logged-in username
        User user = profileService.getUserData(userName);

        model.addAttribute("profileAddress", new ProfileAddress());
        model.addAttribute("countries", getSupportedCountries());
        model.addAttribute("user", user);
        return "UserProfile/manage-address";
    }

    @PostMapping("/update-address")
    public String updateOrderAddress(@Valid @ModelAttribute("profileAddress") ProfileAddress profileAddress,
                                     BindingResult bindingResult,
                                     Model model){
        //Get user name/email
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName(); // Get logged-in username
        User user = profileService.getUserData(userName);
        //Update user's data
        if(profileAddress == null || bindingResult.hasErrors()){
            model.addAttribute("status", "error");
        }else{
            user.setFirstName(profileAddress.getFirstName());
            user.setLastName(profileAddress.getLastName());
            user.setPhoneNumber(profileAddress.getPhoneNumber());
            user.setCountry(profileAddress.getCountyName());
            user.setAddress(profileAddress.getAddress());
            user.setModifyDate(new Timestamp(System.currentTimeMillis()));
            //Update user's address information
            profileService.updateUserAddress(user);
            //Add status success to model
            model.addAttribute("status", "success");
        }

        //Redirect to manage-address
        model.addAttribute("profileAddress", new ProfileAddress());
        model.addAttribute("countries", getSupportedCountries());
        model.addAttribute("user", user);
        return "UserProfile/manage-address";
    }

    private List<Country> getSupportedCountries(){
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("Bulgaria", "BG"));
        countries.add(new Country("USA", "US"));
        countries.add(new Country("England", "EN"));
        return countries;
    }

}
