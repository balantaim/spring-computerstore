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


import com.martinatanasov.computerstore.model.Country;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/Profile")
public class UserProfileController {

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
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("Bulgaria", "BG"));
        countries.add(new Country("USA", "US"));
        countries.add(new Country("England", "EN"));
        model.addAttribute("countries", countries);
        return "UserProfile/manage-address";
    }

    @PostMapping("/update-address")
    public String updateOrderAddress(Model model){
//        TODO


        List<Country> countries = new ArrayList<>();
        countries.add(new Country("Bulgaria", "BG"));
        countries.add(new Country("USA", "US"));
        countries.add(new Country("England", "EN"));
        model.addAttribute("countries", countries);
        return "UserProfile/manage-address";
    }

//    @Bean
//    public void getCountries(){
//        String[] locales = Locale.getISOCountries();
//        Locale locale = null;
//        for(String country : locales) {
//            locale = new Locale("", country);
//            System.out.println(locale.getDisplayCountry());
//            System.out.println(locale.getCountry());
//            System.out.println(locale.getDisplayLanguage());
//            System.out.println(locale.getDisplayName() + "\n");
//        }
//    }

}
