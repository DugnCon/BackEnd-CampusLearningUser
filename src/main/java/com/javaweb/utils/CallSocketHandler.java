package com.javaweb.utils;

import com.javaweb.entity.UserEntity;
import com.javaweb.model.dto.ChatAndCall.*;
import com.javaweb.repository.IUserRepository;
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
public class CallSocketHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private IUserRepository userRepository;

    @MessageMapping("/call.initiate")
    public void handleInitiateCall(CallInitiateMessage message, Principal principal) {
        try {
            log.info("CALL INITIATE từ user {} → user {}", principal.getName(), message.getReceiverID());

            String callerName = getCallerDisplayName(principal);

            Map<String, Object> response = new HashMap<>();
            response.put("type", "INCOMING_CALL");
            response.put("callID", message.getCallID());
            response.put("initiatorID", principal.getName());
            response.put("initiatorName", callerName);
            response.put("conversationID", message.getConversationID());
            response.put("callType", message.getType());
            response.put("timestamp", System.currentTimeMillis());

            // DÙNG USER ID – CHẮC CHẮN 1000000%
            messagingTemplate.convertAndSend(
                    "/user/" + message.getReceiverID() + "/topic/call.incoming",
                    response
            );

            log.info("ĐÃ GỬI INCOMING_CALL đến userId {}", message.getReceiverID());

        } catch (Exception e) {
            log.error("Lỗi CALL_INITIATE: {}", e.getMessage(), e);
            sendError(principal.getName(), "Không thể khởi tạo cuộc gọi");
        }
    }

    @MessageMapping("/call.answer")
    public void handleAnswerCall(CallAnswerMessage message, Principal principal) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("type", "CALL_ANSWERED");
            response.put("callID", message.getCallID());
            response.put("accepted", message.isAccepted());
            response.put("respondentID", principal.getName());
            response.put("respondentName", getCallerDisplayName(principal));
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend(
                    "/user/" + message.getInitiatorID() + "/topic/call.answered",
                    response
            );

            log.info("ĐÃ GỬI CALL_ANSWERED đến userId {}", message.getInitiatorID());
        } catch (Exception e) {
            log.error("Lỗi CALL_ANSWER: {}", e.getMessage(), e);
        }
    }

    @MessageMapping("/call.reject")
    public void handleRejectCall(CallRejectMessage message, Principal principal) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("type", "CALL_REJECTED");
            response.put("callID", message.getCallID());
            response.put("rejectedByID", principal.getName());
            response.put("rejectedByName", getCallerDisplayName(principal));
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend(
                    "/user/" + message.getInitiatorID() + "/topic/call.rejected",
                    response
            );

            log.info("ĐÃ GỬI CALL_REJECTED đến userId {}", message.getInitiatorID());
        } catch (Exception e) {
            log.error("Lỗi CALL_REJECT: {}", e.getMessage(), e);
        }
    }

    @MessageMapping("/call.end")
    public void handleEndCall(CallEndMessage message, Principal principal) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("type", "CALL_ENDED");
            response.put("callID", message.getCallID());
            response.put("endedByID", principal.getName());
            response.put("endedByName", getCallerDisplayName(principal));
            response.put("reason", message.getReason());
            response.put("duration", message.getDuration());
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend("/topic/call." + message.getCallID(), response);
            log.info("ĐÃ GỬI CALL_ENDED cho callID: {}", message.getCallID());
        } catch (Exception e) {
            log.error("Lỗi CALL_END: {}", e.getMessage(), e);
        }
    }

    private String getCallerDisplayName(Principal principal) {
        if (principal == null) return "Unknown";
        try {
            UserEntity user = userRepository.findById(Long.valueOf(principal.getName())).orElse(null);
            if (user != null && user.getFullName() != null && !user.getFullName().trim().isEmpty()) {
                return user.getFullName();
            }
            return user != null ? user.getUsername() : principal.getName();
        } catch (Exception e) {
            return principal.getName();
        }
    }

    private void sendError(String userId, String msg) {
        Map<String, Object> error = new HashMap<>();
        error.put("type", "CALL_ERROR");
        error.put("message", msg);
        messagingTemplate.convertAndSend("/user/" + userId + "/topic/call.error", error);
    }
}