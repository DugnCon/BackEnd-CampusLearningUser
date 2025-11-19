package com.javaweb.utils;

import com.javaweb.entity.UserEntity;
import com.javaweb.model.dto.ChatAndCall.*;
import com.javaweb.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Component
@Controller
@Slf4j
public class CallSignalingHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private IUserRepository userRepository;

    @MessageMapping("/call.signal")
    public void handleCallSignal(CallSignalMessage message, Principal principal) {
        try {
            WebRTCSignal signal = message.getSignal();

            log.info("SOCKET - CALL SIGNAL: from={} → to={} (ID), callId={}, type={}",
                    principal.getName(), message.getToUserID(), message.getCallID(), signal.getType());

            if (signal == null || signal.getType() == null) {
                throw new IllegalArgumentException("Invalid signal data");
            }

            // QUAN TRỌNG: Lấy username của người nhận
            UserEntity targetUser = userRepository.findById(message.getToUserID()).orElse(null);
            if (targetUser == null || targetUser.getUsername() == null) {
                log.warn("Không tìm thấy username cho toUserID: {}", message.getToUserID());
                return;
            }

            String targetUsername = targetUser.getUsername();

            // Ghi log chi tiết signal
            if ("offer".equals(signal.getType()) || "answer".equals(signal.getType())) {
                log.info("SDP {} - length: {}", signal.getType(), signal.getSdp() != null ? signal.getSdp().length() : 0);
            } else if ("candidate".equals(signal.getType())) {
                log.info("ICE Candidate gửi đi");
            }

            // Gửi theo USERNAME – ĐÚNG 100%
            messagingTemplate.convertAndSendToUser(
                    targetUsername,
                    "/topic/call.signal",
                    message
            );

            log.info("ĐÃ CHUYỂN SIGNAL thành công đến: {} (ID: {})", targetUsername, message.getToUserID());

        } catch (Exception e) {
            log.error("Lỗi xử lý CALL_SIGNAL: {}", e.getMessage(), e);

            Map<String, Object> error = new HashMap<>();
            error.put("type", "SIGNAL_ERROR");
            error.put("message", "Không thể chuyển tín hiệu WebRTC");
            messagingTemplate.convertAndSendToUser(principal.getName(), "/topic/call.error", error);
        }
    }
}