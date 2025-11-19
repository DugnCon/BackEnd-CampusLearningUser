package com.javaweb.utils;

import com.javaweb.model.dto.ChatAndCall.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Component  // CHỈ DÙNG @Component, KHÔNG DÙNG @Controller!!!
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

            WebRTCSignal signal = message.getSignal();
            log.info("SIGNAL {} từ user {} → user {} (callId={})",
                    signal.getType(), principal.getName(), message.getToUserID(), message.getCallID());

            // DÙNG USER ID – CHẮC CHẮN ĐẾN ĐÚNG NGƯỜI 1000000%
            messagingTemplate.convertAndSend(
                    "/user/" + message.getToUserID() + "/topic/call.signal",
                    message
            );

            log.info("ĐÃ CHUYỂN SIGNAL thành công đến userId {}", message.getToUserID());

        } catch (Exception e) {
            log.error("Lỗi xử lý CALL_SIGNAL: {}", e.getMessage(), e);

            Map<String, Object> error = new HashMap<>();
            error.put("type", "SIGNAL_ERROR");
            error.put("message", "Không thể chuyển tín hiệu WebRTC");
            if (principal != null) {
                messagingTemplate.convertAndSend("/user/" + principal.getName() + "/topic/call.error", error);
            }
        }
    }

    // Các hàm join/leave/media/heartbeat giữ nguyên nếu cần, hoặc xóa nếu không dùng
    // (không ảnh hưởng đến call chính)
}