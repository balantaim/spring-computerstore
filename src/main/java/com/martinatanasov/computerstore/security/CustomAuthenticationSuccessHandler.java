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

package com.martinatanasov.computerstore.security;

import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.services.CartService;
import com.martinatanasov.computerstore.services.OrderService;
import com.martinatanasov.computerstore.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private final UserService userService;
    @Autowired
    private final OrderService orderService;
    @Autowired
    private final CartService cartService;

    //Save to cache the previous URL if login is required
    @Bean
    public HttpSessionRequestCache requestCache() {
        return new HttpSessionRequestCache();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        final String userName = authentication.getName();
        final User user = userService.findByUserName(userName);

        //Place in the session
        HttpSession session = request.getSession();
        //Set user in the session
        session.setAttribute("user", user);
        //Set Cart count and Order count required in top navigation bar to the session
        final int orderCount = orderService.getUnfinishedOrdersCount(userName);
        final int cartCount = cartService.getCartItemsCount(user.getId());
        session.setAttribute("orders-count", orderCount > 0 ? orderCount : null);
        session.setAttribute("cart-items-count", cartCount > 0 ? cartCount : null);


        //Check if there is saved url in session request cache
        HttpSessionRequestCache requestCache = requestCache();
        var savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            // Redirect to saved URL
            String targetUrl = savedRequest.getRedirectUrl();
            response.sendRedirect(targetUrl);
        } else {
            // Forward to home page
            response.sendRedirect(request.getContextPath() + "/Profile");
        }
    }

}
