package com.javaweb.utils;

import com.javaweb.model.dto.ChatAndCall.CallSignalMessage;
import com.javaweb.model.dto.ChatAndCall.WebRTCSignal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CallSignalingHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/call.signal")
    public void handleCallSignal(CallSignalMessage message, Principal principal) {
        try {
            if (principal == null) {
                log.error("Principal null trong CallSignalingHandler");
                return;
            }

            String sender = principal.getName();
            String target = message.getToUserID().toString();

            // Nếu gửi nhầm cho chính mình thì bỏ qua và log
            if (sender.equals(target)) {
                log.warn("Signal target is same as sender ({}). Ignoring.", sender);
                return;
            }

            WebRTCSignal signal = message.getSignal();
            log.info("SIGNAL {} từ user {} → user {} (callId={})",
                    signal.getType(), sender, target, message.getCallID());

            messagingTemplate.convertAndSendToUser(
                    target,
                    "/queue/call.signal",
                    message
            );

            log.info("Đã chuyển signal đến user: {}", target);
        } catch (Exception e) {
            log.error("Lỗi xử lý call signal: {}", e.getMessage(), e);

            Map<String, Object> error = new HashMap<>();
            //error.put("type", "SIGNAL_ERROR");
            error.put("message", "Không thể chuyển tín hiệu WebRTC");

            if (principal != null) {
                messagingTemplate.convertAndSendToUser(
                        principal.getName(),
                        "/queue/call.error",
                        error
                );
            }
        }
    }
}