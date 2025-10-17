package com.javaweb;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.time.Instant;
import java.time.format.DateTimeFormatter;


@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class SpringBootWebApplication extends SpringBootServletInitializer {
//    private static final String dateFormat = "yyyy-MM-dd";
//    private static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss.SSS";
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SpringBootWebApplication.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpringBootWebApplication.class, args);
    }

//    @Bean
//    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer(){
//        return builder ->{
//            builder.simpleDateFormat(dateFormat);
//            builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(Instant.now().toString())));
//            builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
//        };
//    }
}
