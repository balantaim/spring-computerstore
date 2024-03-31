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
import com.martinatanasov.computerstore.service.PasswordValidationService;
import com.martinatanasov.computerstore.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.logging.Logger;
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

    //private final Logger logger = java.util.logging.Logger.getLogger(getClass().getName());
    private final PasswordValidationService validatePassword;

    @Autowired
    public RegisterController(UserService userService, PasswordValidationService validatePassword) {
        this.userService = userService;
        this.validatePassword = validatePassword;
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/RegisterForm")
    public String register(Model model){
        model.addAttribute("active","Register");
        model.addAttribute("webUser", new WebUser());
        return "Register/register";
    }

    @PostMapping("/processRegistrationForm")
    public String processRegistrationForm(
            @Valid @ModelAttribute("webUser") WebUser webUser,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {

        String userName = webUser.getEmail();

        String errorMessage = "";
        if(!validatePassword.validateUserPassword(webUser)){
            errorMessage = "Password and Re-password does not match";
            ObjectError error = new ObjectError("globalError", errorMessage);
            bindingResult.addError(error);
        }
        // form validation
        if (bindingResult.hasErrors()){
            return "Register/register";
        }
        // check the database if user already exists
        User existing = userService.findByUserName(userName);
        if (existing != null){
            //model.addAttribute("webUser", new WebUser());
            errorMessage = "User already exist!";
            ObjectError error = new ObjectError("globalError", errorMessage);
            bindingResult.addError(error);
            return "Register/register";
        }

        // create user account and store in the database
        userService.save(webUser);

        // place user in the web http session for later use
        session.setAttribute("user", webUser);

        return "Register/register-confirm";
    }

}
