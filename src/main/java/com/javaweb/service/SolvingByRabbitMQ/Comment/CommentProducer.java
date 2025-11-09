package com.javaweb.service.SolvingByRabbitMQ.Comment;

import com.javaweb.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CommentProducer {
    private final RabbitTemplate rabbitTemplate;

    public CommentProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUpLoadTask(Long postId, Long userId,String content) {
        Map<String, Object> message = new HashMap<>();

        message.put("postId", postId);
        message.put("comment", content);
        message.put("userId", userId);

        rabbitTemplate.convertAndSend(RabbitMQConfig.COMMENT_UPLOAD_QUEUE, message);
        System.out.println("Sent upload task to queue for post for comment" + postId);
    }
}
