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
import com.martinatanasov.computerstore.model.AppUserDTO;
import com.martinatanasov.computerstore.services.UserService;
import com.martinatanasov.computerstore.utils.converter.TestNotificationsState;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/register")
public class RegisterController {

    private final UserService userService;
    private final TestNotificationsState testNotificationsState;

    @Autowired
    public RegisterController(UserService userService, TestNotificationsState testNotificationsState) {
        this.userService = userService;
        this.testNotificationsState = testNotificationsState;
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/RegisterForm")
    public String register(Model model) {
        model.addAttribute("active", "Register");
        model.addAttribute("appUserDTO", new AppUserDTO());
        return "Register/register";
    }

    @PostMapping("/processRegistrationForm")
    public String processRegistrationForm(
            @Valid @ModelAttribute("appUserDTO") AppUserDTO appUserDTO,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {

        String userName = appUserDTO.getEmail();

        String errorMessage = "";
        if (appUserDTO.getPassword() != null && appUserDTO.getRepeatPassword() != null) {
            if (!appUserDTO.getRepeatPassword().equals(appUserDTO.getPassword())) {
                errorMessage = "PassMatcher";
                ObjectError error = new ObjectError("globalError", errorMessage);
                bindingResult.addError(error);
            }
        }
        // form validation
        if (bindingResult.hasErrors()) {
            model.addAttribute("status", "error");
            return "Register/register";
        }
        // check the database if user already exists
        User existing = userService.findByUserName(userName);
        if (existing != null) {
            //model.addAttribute("webUser", new WebUser());
            errorMessage = "UserExist";
            ObjectError error = new ObjectError("globalError", errorMessage);
            bindingResult.addError(error);
            model.addAttribute("status", "error");
            if (testNotificationsState.isNotificationsActive()) {
                model.addAttribute("testNotification", true);
            }
            return "Register/register";
        }

        // create user account and store in the database
        userService.save(appUserDTO);

        // place user in the web http session for later use
        session.setAttribute("user", appUserDTO);
        return "Register/register-confirm";
    }

}
