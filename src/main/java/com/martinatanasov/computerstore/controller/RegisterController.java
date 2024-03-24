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
import com.martinatanasov.computerstore.model.WebUser;
import com.martinatanasov.computerstore.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {


    private final UserService userService;

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/Register")
    public String register(Model model){
        model.addAttribute("webUser", new WebUser());
        return "Register/register";
    }

    @PostMapping("/processRegistrationForm")
    public String processRegistrationForm(
            @Valid @ModelAttribute("webUser") WebUser webUser,
            BindingResult bindingResult,
            HttpSession session, Model model) {

        String userName = webUser.getEmail();
        //logger.info("Processing registration form for: " + userName);

        // form validation
        if (bindingResult.hasErrors()){
            model.addAttribute("webUser", new WebUser());
            model.addAttribute("registrationError", "User name already exists.");
            return "Register/register";
        }

        // check the database if user already exists
        User existing = userService.findByUserName(userName);
        if (existing != null){
            model.addAttribute("webUser", new WebUser());
            model.addAttribute("registrationError", "User name already exists.");

            //logger.warning("User name already exists.");
            return "Register/register";
        }

        // create user account and store in the database
        userService.save(webUser);

        //logger.info("Successfully created user: " + userName);

        // place user in the web http session for later use
        session.setAttribute("user", webUser);

        return "Register/register-confirm";
    }

}
