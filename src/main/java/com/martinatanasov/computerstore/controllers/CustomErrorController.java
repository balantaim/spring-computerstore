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

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController implements ErrorController {

    public static final String NOT_FOUND_PAGE = "error/404";
    public static final String GLOBAL_ERROR_PAGE = "error/global-error";

    @GetMapping("/error")
    public String handleError() {
        return GLOBAL_ERROR_PAGE;
    }

    @GetMapping("/403")
    public String forbiddenError() {
        return "error/403";
    }

    //Add request mapping for access denied - 401
    @GetMapping("/access-denied")
    public String showAccessDenied() {
        return "error/access-denied";
    }

}
