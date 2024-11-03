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

package com.martinatanasov.computerstore.controllers;

import com.martinatanasov.computerstore.model.UserInfoDTO;
import com.martinatanasov.computerstore.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdministrationController {

    private final UserService userService;

    @GetMapping("/Administration")
    public String administration(Model model) {
        Iterable<UserInfoDTO> users = userService.getUsersInfo();
        if(users != null){
            model.addAttribute("users", users);
        }
        model.addAttribute("active", "Administration");
        return "Administration/administration";
    }

    @DeleteMapping("/Administration")
    public String deleteUser(@RequestParam final String email, Model model) {
        if (email != null && email.length() > 3 && email.length() <= 50) {
            userService.delete(email);
            model.addAttribute("status", "success");
        } else {
            model.addAttribute("status", "error");
        }
        return "Administration/administration";
    }

    @PatchMapping("/Administration/status")
    public String updateUserAccount(@RequestParam final String email,
                                    @RequestParam final Boolean enabled,
                                    Model model) {

        userService.setAccountStatus(email, enabled);
        model.addAttribute("status", "success");
        return "Administration/administration";
    }

    @PutMapping("/Administration/newpassword")
    public String setPassword(@RequestParam final String email,
                              @RequestParam final String newPassword,
                              Model model){

        boolean isNewPasswordCreated = userService.setNewPassword(email, newPassword);
        if(isNewPasswordCreated){
            model.addAttribute("status", "success");
        }else{
            model.addAttribute("status", "error");
        }
        return "Administration/administration";
    }


}
