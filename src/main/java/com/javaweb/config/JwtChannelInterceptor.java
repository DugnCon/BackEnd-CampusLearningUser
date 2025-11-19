// src/main/java/com/javaweb/config/JwtChannelInterceptor.java

package com.javaweb.config;

import com.javaweb.entity.UserEntity;
import com.javaweb.repository.IUserRepository;
import com.javaweb.service.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private IUserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authHeaders = accessor.getNativeHeader("Authorization");

            if (authHeaders == null || authHeaders.isEmpty()) {
                System.out.println("WebSocket: Không có token → từ chối kết nối");
                return null;
            }

            String token = authHeaders.get(0).replace("Bearer ", "");

            try {
                Claims claims = jwtService.extractAllClaims(token);
                String email = claims.getSubject();

                UserEntity user = userRepository.findByEmail(email)
                        .orElse(null);

                if (user == null) {
                    System.out.println("WebSocket: Không tìm thấy user với email = " + email);
                    return null;
                }

                String userIdStr = String.valueOf(user.getUserID());

                // Tạo Principal có name = userId (dạng String)
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userIdStr,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

                accessor.setUser(auth);
                System.out.println("WebSocket xác thực thành công: userId = " + userIdStr + " (email: " + email + ")");

            } catch (Exception e) {
                System.out.println("WebSocket token lỗi: " + e.getMessage());
                return null;
            }
        }

        return message;
    }
}