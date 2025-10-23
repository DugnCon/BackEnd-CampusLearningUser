package com.javaweb.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String MEDIA_UPLOAD_QUEUE = "media_upload_queue";

    @Bean
    public Queue mediaUploadQueue() {
        return new Queue(MEDIA_UPLOAD_QUEUE, false);
    }
}
