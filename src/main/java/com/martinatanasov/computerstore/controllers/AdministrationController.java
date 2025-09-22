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
import com.martinatanasov.computerstore.model.UserDetailsDTO;
import com.martinatanasov.computerstore.services.UserService;
import com.martinatanasov.computerstore.utils.converter.UserConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.martinatanasov.computerstore.controllers.CustomErrorController.GLOBAL_ERROR_PAGE;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/Administration")
public class AdministrationController {

    private final UserService userService;
    private final UserConverter userConverter;

    @GetMapping("")
    public String administration(Model model) {
        final List<User> users = userService.getUsersDetailsInfo();
        if (users != null) {
            model.addAttribute("users", users.stream()
                    .map(userConverter::userToUserAdministrationDTO)
                    .sorted(Comparator.comparing(UserDetailsDTO::email))
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
        }
        model.addAttribute("active", "Administration");
        return "Administration/administration";
    }

    @GetMapping("/profile/{id}")
    public String userInfoPage(@PathVariable(value = "id") Long id,
                               Model model) {
        final User user = userService.findByUserId(id);
        if (user != null) {
            model.addAttribute("user", userConverter.userToUserAdministrationDTO(user));
        }
        return "Administration/user-info";
    }

    @DeleteMapping("")
    public String deleteUser(@RequestParam final String email, Model model) {
        if (email != null && email.length() > 3 && email.length() <= 50) {
            userService.delete(email);
            model.addAttribute("status", "success");
        } else {
            model.addAttribute("status", "error");
        }
        return "Administration/administration";
    }

    @PatchMapping("/status")
    public String updateUserAccount(@RequestParam final String email,
                                    @RequestParam final Boolean enabled,
                                    Model model) {

        userService.setAccountStatus(email, enabled);
        model.addAttribute("status", "success");
        return "Administration/administration";
    }

    @PostMapping("/disableOrEnableUser")
    public String disableOrEnableUser(@RequestParam("userId") Long userId,
                                      @RequestParam("enabled") Boolean enabled,
                                      @RequestParam("verified") Boolean verified,
                                      Model model) {
        if (userId != null && enabled != null && verified != null) {
            userService.disableOrEnableUser(userId, enabled, verified);

            final List<User> users = userService.getUsersDetailsInfo();
            if (users != null) {
                model.addAttribute("users", users.stream()
                        .map(userConverter::userToUserAdministrationDTO)
                        .sorted(Comparator.comparing(UserDetailsDTO::email))
                        .collect(Collectors.toCollection(LinkedHashSet::new)));
            }
            model.addAttribute("active", "Administration");
            model.addAttribute("status", "success");
            return "Administration/administration";
        }
        return GLOBAL_ERROR_PAGE;
    }

    @PutMapping("/new-password")
    public String setPassword(@RequestParam final String email,
                              @RequestParam final String newPassword,
                              Model model) {
        boolean isNewPasswordCreated = userService.setNewPassword(email, newPassword);
        if (isNewPasswordCreated) {
            model.addAttribute("status", "success");
        } else {
            model.addAttribute("status", "error");
        }
        return "Administration/administration";
    }


}
