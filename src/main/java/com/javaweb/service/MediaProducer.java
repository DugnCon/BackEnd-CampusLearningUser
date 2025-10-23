package com.javaweb.service;

import com.javaweb.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class MediaProducer {
    @Autowired
    private FileStorageService fileStorageService;

    private final RabbitTemplate rabbitTemplate;

    public MediaProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    //gửi các message cần lưu riêng lên RabbitMQ
    public void sendUploadTask(Long postId, List<String> fileNames) {
        Map<String, Object> message = new HashMap<>();

        message.put("postId", postId);
        message.put("fileNames", fileNames);

        rabbitTemplate.convertAndSend(RabbitMQConfig.MEDIA_UPLOAD_QUEUE, message);
        System.out.println("Sent upload task to queue for post " + postId);
    }

}
