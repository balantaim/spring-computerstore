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

import com.martinatanasov.computerstore.services.CategoryServiceImpl;
import com.martinatanasov.computerstore.services.ProductServiceImpl;
import com.martinatanasov.computerstore.services.ReviewServiceImpl;
import com.martinatanasov.computerstore.utils.converter.ProductConverter;
import com.martinatanasov.computerstore.utils.converter.UserAuthentication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
//@WithMockUser(username = "abv@abv.bg", roles = {"CUSTOMER"})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryServiceImpl categoryService;

    @MockitoBean
    private ProductServiceImpl productService;

    @MockitoBean
    private ReviewServiceImpl reviewService;

    @MockitoBean
    private ProductConverter productConverter;

    @MockitoBean
    private UserAuthentication userAuthentication;

    @Test()
    public void shouldReturnProductsView() throws Exception {
        mockMvc.perform(get("/Products").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("Products/products"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

}