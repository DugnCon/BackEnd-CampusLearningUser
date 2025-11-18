package com.javaweb.utils;

import com.javaweb.model.dto.ChatAndCall.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class CallSignalingController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * WebRTC Signaling - Gửi offer/answer/candidate
     */
    @MessageMapping("/call.signal")
    public void handleCallSignal(CallSignalMessage message, Principal principal) {
        try {
            log.info("SOCKET - CALL SIGNAL: from={}, to={}, type={}",
                    principal.getName(), message.getToUserID(), message.getSignal().getType());

            // Thêm thông tin người gửi
            message.setFromUserID(principal.getName());

            // Forward signal đến user đích
            messagingTemplate.convertAndSendToUser(
                    message.getToUserID(),
                    "/topic/call.signal",
                    message
            );

            log.info("Đã chuyển SIGNAL đến user: {}, type: {}",
                    message.getToUserID(), message.getSignal().getType());

        } catch (Exception e) {
            log.error("Lỗi xử lý CALL_SIGNAL: {}", e.getMessage(), e);

            // Gửi lỗi về cho sender
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("type", "SIGNAL_ERROR");
            errorResponse.put("message", "Failed to send signal");
            errorResponse.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/topic/call.error",
                    errorResponse
            );
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