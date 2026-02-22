/*
 * Copyright 2024-2026 Martin Atanasov.
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

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
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
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final Environment environment;

    /**
     * Registration for static resource
     * @param registry add browser cache for static assets
     * @author Martin Atanasov
     */

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        long cssMaxAge = Long.parseLong(environment.getProperty("static.asset.cache.css.days", "30")),
        jsMaxAge = Long.parseLong(environment.getProperty("static.asset.cache.js.days", "365")),
        imagesMaxAge = Long.parseLong(environment.getProperty("static.asset.cache.img.days", "365")),
        otherMaxAge = Long.parseLong(environment.getProperty("static.asset.cache.other.days", "30")),
        robotsMaxAge = Long.parseLong(environment.getProperty("static.asset.cache.robots.days", "365"));

        //Register CSS
        registry.addResourceHandler("/css/**")
                .addResourceLocations("/public/", "classpath:/static/css/")
                .setCacheControl(CacheControl.maxAge(cssMaxAge, TimeUnit.DAYS))
                .resourceChain(true)
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"))
                //Solve problem with imported css
                .addTransformer(new CssLinkResourceTransformer());

        //Register JS
        registry.addResourceHandler("/js/**")
                .addResourceLocations("/public/", "classpath:/static/js/")
                .setCacheControl(CacheControl.maxAge(jsMaxAge, TimeUnit.DAYS))
                .resourceChain(true)
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));

        //Register Images
        registry.addResourceHandler("/images/**")
                .addResourceLocations("/public/", "classpath:/static/images/")
                .setCacheControl(CacheControl.maxAge(imagesMaxAge, TimeUnit.DAYS))
                .resourceChain(true)
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));

        //Register Other files
        registry.addResourceHandler("/other/**")
                .addResourceLocations("/public/", "classpath:/static/other/")
                .setCacheControl(CacheControl.maxAge(otherMaxAge, TimeUnit.DAYS))
                .resourceChain(true)
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));

        //Register robots.txt
        registry.addResourceHandler("/robots.txt")
                .addResourceLocations("/public/", "classpath:/static/")
                .setCacheControl(CacheControl.maxAge(robotsMaxAge, TimeUnit.DAYS));

    }
}
