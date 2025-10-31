package com.javaweb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // endpoint để FE kết nối
                .setAllowedOriginPatterns("*") // Cho phép mọi domain (dễ test hơn)
                .withSockJS(); // hỗ trợ fallback cho trình duyệt cũ
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // prefix cho nơi client sẽ "subscribe" để nhận tin
        registry.enableSimpleBroker("/topic", "/queue");

        // prefix cho nơi client sẽ "send" tin nhắn lên server
        registry.setApplicationDestinationPrefixes("/app");
    }
}
