package com.javaweb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Resource handler cho folder uploads
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(
                        // Nếu chạy local Windows
                        "file:C:/Esclipe_Web/campuslearning/uploads/",  
                        // Nếu chạy trong Docker container (ví dụ mount /app/uploads)
                        "file:/app/uploads/"
                );
    }
}
