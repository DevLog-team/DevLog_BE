package com.project.devlog.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticRoutingConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/doc/**")
                .addResourceLocations("classpath:/static/docs/");

        registry.addResourceHandler("/swagger-ui.html", "/swagger-ui/**")
                .addResourceLocations("classpath:/static/swagger-ui/", "classpath:/static/");
    }
}
