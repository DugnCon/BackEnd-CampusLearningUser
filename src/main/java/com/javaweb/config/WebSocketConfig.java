package com.javaweb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        StompWebSocketEndpointRegistration endpoint = registry.addEndpoint("/ws");

        endpoint.setAllowedOriginPatterns(
                "https://*.ngrok-free.app",  // Cho tất cả subdomain của ngrok
                "http://localhost:*",        // Cho mọi port localhost
                "http://127.0.0.1:*",        // Cho IPv4 local
                "https://localhost:*",       // HTTPS local
                "http://192.168.*:*",        // Cho mạng local
                "*"                          // Fallback (chỉ dùng trong dev)
        );

        endpoint
                .withSockJS()
                .setSupressCors(true)        //Bỏ qua CORS cho SockJS
                .setClientLibraryUrl("https://cdn.jsdelivr.net/npm/sockjs-client@1.6.1/dist/sockjs.min.js")
                .setSessionCookieNeeded(false)
                .setWebSocketEnabled(true)
                .setHeartbeatTime(25000);    // Heartbeat 25s

        System.out.println("✅ WebSocket endpoint registered at /ws with CORS for ngrok");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // SIMPLE BROKER - cho các topic/queue
        registry.enableSimpleBroker(
                "/topic",
                "/queue",
                "/user"
        );

        registry.setApplicationDestinationPrefixes("/app");

        registry.setUserDestinationPrefix("/user");

        System.out.println("Message broker configured with topics, queues and user destinations");
    }

    // THÊM CẤU HÌNH TRANSPORT (OPTIONAL)
    /*
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration
            .setMessageSizeLimit(128 * 1024) // 128KB max message size
            .setSendTimeLimit(20 * 1000)     // 20 seconds send timeout
            .setSendBufferSizeLimit(512 * 1024); // 512KB buffer
    }
    */
}