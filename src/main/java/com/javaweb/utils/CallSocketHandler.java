package com.javaweb.utils;

import com.javaweb.entity.UserEntity;
import com.javaweb.model.dto.ChatAndCall.*;
import com.javaweb.repository.IUserRepository;
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
public class CallSocketHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private IUserRepository userRepository;

    /**
     * Xử lý khi có người khởi tạo cuộc gọi
     */
    @MessageMapping("/call.initiate")
    public void handleInitiateCall(CallInitiateMessage message, Principal principal) {
        try {
            log.info("SOCKET - CALL INITIATE: from={}, to={}, type={}, callId={}",
                    principal.getName(), message.getReceiverID(), message.getType(), message.getCallID());

            UserEntity user = userRepository.findById(message.getReceiverID()).orElseThrow(() -> new RuntimeException("Not found"));
            String receiverUsername = user.getUsername();

            if (receiverUsername == null) {
                log.warn("Không tìm thấy username cho receiverID: {}", message.getReceiverID());
                sendError(principal.getName(), "Người này hiện không online");
                return;
            }

            Map<String, Object> response = new HashMap<>();
            response.put("type", "INCOMING_CALL");
            response.put("callID", message.getCallID());
            response.put("initiatorID", principal.getName());
            response.put("initiatorName", getCurrentUsername(principal));
            response.put("conversationID", message.getConversationID());
            response.put("type", message.getType());
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSendToUser(
                    receiverUsername,
                    "/topic/call.incoming",
                    response
            );

            log.info("ĐÃ GỬI INCOMING_CALL đến user: {} (ID: {})", receiverUsername, message.getReceiverID());

        } catch (Exception e) {
            log.error("Lỗi xử lý CALL_INITIATE: {}", e.getMessage(), e);
            sendError(principal.getName(), "Hệ thống bận");
        }
    }

    private String getCurrentUsername(Principal principal) {
        return principal != null ? principal.getName() : "Unknown";
    }

    private void sendError(String username, String msg) {
        Map<String, Object> error = new HashMap<>();
        error.put("type", "CALL_ERROR");
        error.put("message", msg);
        messagingTemplate.convertAndSendToUser(username, "/topic/call.error", error);
    }

    /**
     * Xử lý khi có người trả lời cuộc gọi - GỬI DIRECT ĐẾN INITIATOR
     */
    @MessageMapping("/call.answer")
    public void handleAnswerCall(CallAnswerMessage message, Principal principal) {
        try {
            log.info("SOCKET - CALL ANSWER: respondent={}, callId={}, accepted={}",
                    principal.getName(), message.getCallID(), message.isAccepted());

            Map<String, Object> response = new HashMap<>();
            response.put("type", "CALL_ANSWERED");
            response.put("callID", message.getCallID());
            response.put("accepted", message.isAccepted());
            response.put("respondentID", principal.getName());
            response.put("respondentName", principal.getName());
            response.put("timestamp", System.currentTimeMillis());

            // Gửi đến initiator của cuộc gọi
            messagingTemplate.convertAndSendToUser(
                    message.getInitiatorID().toString(),
                    "/topic/call.answered",
                    response
            );

            log.info("Đã gửi CALL_ANSWERED đến initiator: {}", message.getInitiatorID());

        } catch (Exception e) {
            log.error("Lỗi xử lý CALL_ANSWER: {}", e.getMessage(), e);
        }
    }

    /**
     * Xử lý khi kết thúc cuộc gọi
     */
    @MessageMapping("/call.end")
    public void handleEndCall(CallEndMessage message, Principal principal) {
        try {
            log.info("SOCKET - CALL END: callId={}, endedBy={}, reason={}",
                    message.getCallID(), principal.getName(), message.getReason());

            Map<String, Object> response = new HashMap<>();
            response.put("type", "CALL_ENDED");
            response.put("callID", message.getCallID());
            response.put("endedByID", principal.getName());
            response.put("endedByName", principal.getName());
            response.put("reason", message.getReason());
            response.put("duration", message.getDuration());
            response.put("timestamp", System.currentTimeMillis());

            // Broadcast đến tất cả participants trong call
            messagingTemplate.convertAndSend(
                    "/topic/call." + message.getCallID(),
                    response
            );

            log.info("Đã gửi CALL_ENDED đến call: {}", message.getCallID());

        } catch (Exception e) {
            log.error("Lỗi xử lý CALL_END: {}", e.getMessage(), e);
        }
    }

    /**
     * Xử lý khi từ chối cuộc gọi
     */
    @MessageMapping("/call.reject")
    public void handleRejectCall(CallRejectMessage message, Principal principal) {
        try {
            log.info("SOCKET - CALL REJECT: callId={}, rejectedBy={}",
                    message.getCallID(), principal.getName());

            Map<String, Object> response = new HashMap<>();
            response.put("type", "CALL_REJECTED");
            response.put("callID", message.getCallID());
            response.put("rejectedByID", principal.getName());
            response.put("rejectedByName", principal.getName());
            response.put("timestamp", System.currentTimeMillis());

            // Gửi đến initiator của cuộc gọi
            messagingTemplate.convertAndSendToUser(
                    message.getInitiatorID().toString(),
                    "/topic/call.rejected",
                    response
            );

            log.info("Đã gửi CALL_REJECTED đến initiator: {}", message.getInitiatorID());

        } catch (Exception e) {
            log.error("Lỗi xử lý CALL_REJECT: {}", e.getMessage(), e);
        }
    }
}