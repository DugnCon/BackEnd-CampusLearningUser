<<<<<<< HEAD
//package com.javaweb.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebMvcConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // Resource handler cho folder uploads
//        registry.addResourceHandler("/uploads/**")
//                .addResourceLocations(
//                        // Nếu chạy local Windows
//                        "file:C:/Esclipe_Web/campuslearning/uploads/",
//                        // Nếu chạy trong Docker container (ví dụ mount /app/uploads)
//                        "file:/app/uploads/"
//                );
//    }
//}
package com.javaweb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir);
    }
}

=======
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
                        "file:C:/Esclipe_Web/campuslearning-addmin/uploads/",
                        // Nếu chạy trong Docker container (ví dụ mount /app/uploads)
                        "file:/app/uploads/"
                );
    }
}
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
