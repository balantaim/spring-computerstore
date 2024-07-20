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

package com.martinatanasov.computerstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.CssLinkResourceTransformer;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import java.util.concurrent.TimeUnit;


@Configuration
@EnableWebMvc
@Profile(value = "prod")
public class WebConfig implements WebMvcConfigurer {

    /**
     * Registration for static resource
     * @param registry
     * @author Martin Atanasov
     */

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        //Register CSS
        registry.addResourceHandler("/css/**")
                .addResourceLocations("/public/", "classpath:/static/css/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
                .resourceChain(true)
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"))
                //Solve problem with imported css
                .addTransformer(new CssLinkResourceTransformer());

        //Register JS
        registry.addResourceHandler("/js/**")
                .addResourceLocations("/public/", "classpath:/static/js/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
                .resourceChain(true)
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));

        //Register Images
        registry.addResourceHandler("/images/**")
                .addResourceLocations("/public/", "classpath:/static/images/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
                .resourceChain(true)
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));

        //Register Other files
        registry.addResourceHandler("/other/**")
                .addResourceLocations("/public/", "classpath:/static/other/")
                .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS))
                .resourceChain(true)
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));

        //Register robots.txt
        registry.addResourceHandler("/robots.txt")
                .addResourceLocations("/public/", "classpath:/static/robots.txt")
                .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS))
                .resourceChain(true)
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));

    }
}
