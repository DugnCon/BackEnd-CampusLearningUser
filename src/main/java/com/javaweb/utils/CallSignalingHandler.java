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
@Slf4j
public class CallSignalingHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private IUserRepository userRepository;

    /**
     * WebRTC Signaling - Gửi offer/answer/candidate
     */
    @MessageMapping("/call.signal")
    public void handleCallSignal(CallSignalMessage message, Principal principal) {
        try {
            WebRTCSignal signal = message.getSignal();

            log.info("SOCKET - CALL SIGNAL: from={} → to={} (ID), callId={}, type={}",
                    principal.getName(), message.getToUserID(), message.getCallID(), signal.getType());

            if (signal == null || signal.getType() == null) {
                throw new IllegalArgumentException("Invalid signal data");
            }

            UserEntity targetUser = userRepository.findById(message.getToUserID()).orElse(null);
            if (targetUser == null || targetUser.getUsername() == null) {
                log.warn("Không tìm thấy username cho toUserID: {}", message.getToUserID());
                return;
            }

            String targetUsername = targetUser.getUsername();

            if ("offer".equals(signal.getType()) || "answer".equals(signal.getType())) {
                log.info("SDP {} - length: {}", signal.getType(), signal.getSdp() != null ? signal.getSdp().length() : 0);
            } else if ("candidate".equals(signal.getType())) {
                log.info("ICE Candidate gửi đi");
            }

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

    /**
     * Join call room
     */
    @MessageMapping("/call.join")
    public void handleJoinCall(JoinCallMessage message, Principal principal) {
        try {
            log.info("📡 SOCKET - CALL JOIN: user={}, callId={}",
                    principal.getName(), message.getCallID());

            // Notify others in the call
            Map<String, Object> response = new HashMap<>();
            response.put("type", "USER_JOINED");
            response.put("userID", principal.getName());
            response.put("userName", principal.getName());
            response.put("callID", message.getCallID());
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend(
                    "/topic/call." + message.getCallID() + ".participants",
                    response
            );

            log.info("USER_JOINED gửi đến call: {}", message.getCallID());

        } catch (Exception e) {
            log.error("Lỗi xử lý CALL_JOIN: {}", e.getMessage(), e);
        }
    }

    /**
     * Leave call room
     */
    @MessageMapping("/call.leave")
    public void handleLeaveCall(LeaveCallMessage message, Principal principal) {
        try {
            log.info("📡 SOCKET - CALL LEAVE: user={}, callId={}",
                    principal.getName(), message.getCallID());

            // Notify others in the call
            Map<String, Object> response = new HashMap<>();
            response.put("type", "USER_LEFT");
            response.put("userID", principal.getName());
            response.put("userName", principal.getName());
            response.put("callID", message.getCallID());
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend(
                    "/topic/call." + message.getCallID() + ".participants",
                    response
            );

            log.info("USER_LEFT gửi đến call: {}", message.getCallID());

        } catch (Exception e) {
            log.error("Lỗi xử lý CALL_LEAVE: {}", e.getMessage(), e);
        }
    }

    /**
     * Toggle media (audio/video)
     */
    @MessageMapping("/call.media.toggle")
    public void handleMediaToggle(MediaToggleMessage message, Principal principal) {
        try {
            log.info("📡 SOCKET - MEDIA TOGGLE: user={}, callId={}, type={}, enabled={}",
                    principal.getName(), message.getCallID(), message.getType(), message.isEnabled());

            Map<String, Object> response = new HashMap<>();
            response.put("type", "MEDIA_TOGGLED");
            response.put("userID", principal.getName());
            response.put("userName", principal.getName());
            response.put("mediaType", message.getType());
            response.put("enabled", message.isEnabled());
            response.put("callID", message.getCallID());
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend(
                    "/topic/call." + message.getCallID() + ".media",
                    response
            );

            log.info("MEDIA_TOGGLED gửi đến call: {}", message.getCallID());

        } catch (Exception e) {
            log.error("Lỗi xử lý MEDIA_TOGGLE: {}", e.getMessage(), e);
        }
    }

    /**
     * Call heartbeat - Kiểm tra call connection
     */
    @MessageMapping("/call.heartbeat")
    public void handleHeartbeat(HeartbeatMessage message, Principal principal) {
        try {
            log.debug("SOCKET - HEARTBEAT: user={}, callId={}",
                    principal.getName(), message.getCallID());

            Map<String, Object> response = new HashMap<>();
            response.put("type", "HEARTBEAT_ACK");
            response.put("callID", message.getCallID());
            response.put("timestamp", System.currentTimeMillis());
            response.put("status", "ALIVE");

            // Gửi ack về cho user
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/topic/call.heartbeat",
                    response
            );

        } catch (Exception e) {
            log.error("Lỗi xử lý HEARTBEAT: {}", e.getMessage(), e);
        }
    }
}