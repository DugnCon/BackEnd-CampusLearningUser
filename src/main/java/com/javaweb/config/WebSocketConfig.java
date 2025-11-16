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
        StompWebSocketEndpointRegistration endpoint = registry.addEndpoint("/ws"); //Endpoint chính

        endpoint.setAllowedOriginPatterns(
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

        endpoint
                .withSockJS()
                .setSupressCors(true)
                .setClientLibraryUrl("https://cdn.jsdelivr.net/npm/sockjs-client@1.6.1/dist/sockjs.min.js")
                .setSessionCookieNeeded(false)
                .setWebSocketEnabled(true)
                .setHeartbeatTime(25000);

        System.out.println("WebSocket endpoint registered at /ws with CORS for ngrok");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue", "/user");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    //Bỏ comment và cấu hình
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration
                .setMessageSizeLimit(512 * 1024)
                .setSendTimeLimit(30 * 1000)
                .setSendBufferSizeLimit(1024 * 1024);
    }

    //Cho concurrent calls
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.taskExecutor()
                .corePoolSize(10)
                .maxPoolSize(20)
                .queueCapacity(100);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor()
                .corePoolSize(10)
                .maxPoolSize(20)
                .queueCapacity(100);
    }
}