package com.javaweb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint chính cho web client
        StompWebSocketEndpointRegistration mainEndpoint = registry.addEndpoint("/ws");
        mainEndpoint.setAllowedOriginPatterns(
                "https://*.ngrok-free.app",
                "http://localhost:*",
                "http://127.0.0.1:*",
                "https://localhost:*",
                "http://192.168.*:*",
                "http://campuslearning.site",
                "https://campuslearning.site",
                "https://campuslearning.site/**",
                "http://campuslearning.site/**",
                "http://code.campuslearning.site",
                "https://code.campuslearning.site"
        );
        mainEndpoint
                .withSockJS()
                .setSupressCors(true)
                .setClientLibraryUrl("https://cdn.jsdelivr.net/npm/sockjs-client@1.6.1/dist/sockjs.min.js")
                .setSessionCookieNeeded(false)
                .setWebSocketEnabled(true)
                .setHeartbeatTime(25000);

        // Endpoint mới cho API calls (FE đang dùng)
        StompWebSocketEndpointRegistration apiEndpoint = registry.addEndpoint("/user/api/ws");
        apiEndpoint.setAllowedOriginPatterns(
                "https://*.ngrok-free.app",
                "http://localhost:*",
                "http://127.0.0.1:*",
                "https://localhost:*",
                "http://192.168.*:*",
                "http://campuslearning.site",
                "https://campuslearning.site",
                "https://campuslearning.site/**",
                "http://campuslearning.site/**",
                "http://code.campuslearning.site",
                "https://code.campuslearning.site"
        );
        apiEndpoint
                .withSockJS()
                .setSupressCors(true)
                .setClientLibraryUrl("https://cdn.jsdelivr.net/npm/sockjs-client@1.6.1/dist/sockjs.min.js")
                .setSessionCookieNeeded(false)
                .setWebSocketEnabled(true)
                .setHeartbeatTime(20000); // Ngắn hơn cho call real-time

        System.out.println("✅ WebSocket endpoints registered:");
        System.out.println("   - /ws (main)");
        System.out.println("   - /user/api/ws (API calls)");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable simple broker với các prefixes
        registry.enableSimpleBroker("/topic", "/queue", "/user");

        // Application destination prefix
        registry.setApplicationDestinationPrefixes("/app");

        // User destination prefix - QUAN TRỌNG cho user-specific messages
        registry.setUserDestinationPrefix("/user");

        System.out.println("✅ Message Broker configured:");
        System.out.println("   - Simple broker: /topic, /queue, /user");
        System.out.println("   - App prefix: /app");
        System.out.println("   - User prefix: /user");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration
                .setMessageSizeLimit(1024 * 1024) // Tăng lên 1MB cho WebRTC signaling
                .setSendTimeLimit(60 * 1000)      // Tăng timeout lên 60s cho call
                .setSendBufferSizeLimit(5 * 1024 * 1024); // Tăng buffer size

        System.out.println("✅ WebSocket Transport configured for call system");
    }

    // Cho concurrent calls - TĂNG cho call system
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.taskExecutor()
                .corePoolSize(20)    // Tăng core pool
                .maxPoolSize(50)     // Tăng max pool
                .queueCapacity(200); // Tăng queue capacity

        System.out.println("✅ Client Inbound Channel configured for high concurrency");
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor()
                .corePoolSize(20)    // Tăng core pool
                .maxPoolSize(50)     // Tăng max pool
                .queueCapacity(200); // Tăng queue capacity

        System.out.println("✅ Client Outbound Channel configured for high concurrency");
    }
}