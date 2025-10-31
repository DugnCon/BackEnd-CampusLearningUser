package com.javaweb.service.SolvingByRabbitMQ.Message;

import com.javaweb.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MessageProducer {
    private final RabbitTemplate rabbitTemplate;

    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessageTask(Long userId, Long conversationId, Map<String, Object> messageData) {
        try {
            Map<String, Object> message = new HashMap<>();

            message.put("userId", userId);
            message.put("conversationId", conversationId);
            message.put("content", messageData.get("content"));
            message.put("type", messageData.get("type"));

            //THÊM tempMessageId - quan trọng!
            String tempMessageId = (String) messageData.get("tempMessageId");
            if (tempMessageId != null) {
                message.put("tempMessageId", tempMessageId);
            } else {
                // Tạo mới nếu chưa có (fallback)
                tempMessageId = "temp_" + System.currentTimeMillis() + "_" +
                        UUID.randomUUID().toString().substring(0, 8);
                message.put("tempMessageId", tempMessageId);
            }

            //SỬA QUEUE NAME: COMMENT_UPLOAD_QUEUE → MESSAGE_UPLOAD_QUEUE
            rabbitTemplate.convertAndSend(RabbitMQConfig.MESSAGE_UPLOAD_QUEUE, message);

            System.out.println("Đã gửi message task đến RabbitMQ cho conversation: " + conversationId);
            System.out.println("TempMessageId: " + tempMessageId);

        } catch (Exception e) {
            System.err.println("Failed to send message to RabbitMQ: " + e.getMessage());
            throw new RuntimeException("Message sending failed", e);
        }
    }
}