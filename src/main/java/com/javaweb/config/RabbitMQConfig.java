package com.javaweb.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String MEDIA_UPLOAD_QUEUE = "media_upload_queue";
    public static final String COMMENT_UPLOAD_QUEUE = "comment_upload_queue";
    public static final String MESSAGE_UPLOAD_QUEUE = "message_upload_queue";

    @Bean
    public Queue mediaUploadQueue() {
        return new Queue(MEDIA_UPLOAD_QUEUE, false);
    }
    @Bean
    public Queue commentUploadQueue() {return new Queue(COMMENT_UPLOAD_QUEUE,false);}
    @Bean
    public Queue messageUploadQueue() {return new Queue(MESSAGE_UPLOAD_QUEUE, false);}
}
