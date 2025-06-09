package com.statistics.statisticsbackend;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configure Spring Boot to serve files from the UPLOAD_DIR
        registry.addResourceHandler("/api/files/**")
                .addResourceLocations("file:/app/uploads/");
    }
}