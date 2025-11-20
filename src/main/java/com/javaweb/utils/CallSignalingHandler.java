package com.javaweb.utils;

import com.javaweb.model.dto.ChatAndCall.CallSignalMessage;
import com.javaweb.model.dto.ChatAndCall.WebRTCSignal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;

@Component
@Slf4j
public class CallSignalingHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/call.signal")
    public void handleCallSignal(CallSignalMessage message, Principal principal) {
        if (principal == null) {
            log.error("Principal null khi nhận signal");
            return;
        }

        String senderId = principal.getName();
        String targetId = message.getToUserID().toString();
        WebRTCSignal signal = message.getSignal();

        // Ngăn gửi cho chính mình
        if (senderId.equals(targetId)) {
            log.warn("User {} gửi signal cho chính mình → bỏ qua", senderId);
            return;
        }

        log.info("Chuyển WebRTC signal [{}] từ {} → {} (callID={})",
                signal.getType(), senderId, targetId, message.getCallID());

        try {
            messagingTemplate.convertAndSendToUser(
                    targetId,
                    "/queue/call.signal",  // ĐÚNG 100%
                    message
            );

            log.info("Đã chuyển signal thành công đến user {}", targetId);
        } catch (Exception e) {
            log.error("Lỗi chuyển signal đến user {}: {}", targetId, e.getMessage(), e);

            // Gửi lỗi về cho người gửi
            messagingTemplate.convertAndSendToUser(
                    senderId,
                    "/queue/call.error",
                    Map.of("message", "Không thể chuyển tín hiệu WebRTC", "timestamp", System.currentTimeMillis())
            );
        }
    }
}