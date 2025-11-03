package com.javaweb.service.SolvingByRabbitMQ.Story;

import com.javaweb.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
public class StoryProducer {
    private final RabbitTemplate rabbitTemplate;

    public StoryProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUpLoadTask(Map<String,Object> storyData) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.STORY_UPLOAD_QUEUE, storyData);
        System.out.println("Sent upload task to queue for story for user" + storyData.get("userId"));
    }
}
