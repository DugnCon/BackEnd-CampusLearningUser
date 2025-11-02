package com.javaweb.service.SolvingByRabbitMQ.Message;

import com.javaweb.config.RabbitMQConfig;
import com.javaweb.service.FileStorageService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class MessageFileProducer {
    @Autowired
    private FileStorageService fileStorageService;
    private final RabbitTemplate rabbitTemplate;

    public MessageFileProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUploadTask(Long userId, Long conversationId, MultipartFile file) throws IOException {
        Map<String, Object> message = new HashMap<>();
        message.put("conversationId", conversationId);
        message.put("userId", userId);

        String path = fileStorageService.saveFile(file);

        message.put("file", path);

        rabbitTemplate.convertAndSend(RabbitMQConfig.FILE_UPLOAD_QUEUE, message);
    }
}
