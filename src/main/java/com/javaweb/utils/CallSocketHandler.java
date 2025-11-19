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
public class CallSocketHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private IUserRepository userRepository;

    @MessageMapping("/call.initiate")
    public void handleInitiateCall(CallInitiateMessage message, Principal principal) {
        try {
            log.info("SOCKET - CALL INITIATE: from={}, to={}, type={}, callId={}",
                    principal.getName(), message.getReceiverID(), message.getType(), message.getCallID());

            // Lấy user nhận cuộc gọi để lấy username
            UserEntity receiver = userRepository.findById(message.getReceiverID())
                    .orElse(null);

            if (receiver == null || receiver.getUsername() == null) {
                log.warn("Không tìm thấy user hoặc username cho receiverID: {}", message.getReceiverID());
                sendError(principal.getName(), "Người này hiện không online");
                return;
            }

            String receiverUsername = receiver.getUsername();

            // Lấy tên thật của người gọi (nếu có fullName thì dùng, không thì dùng username)
            String callerName = getCallerDisplayName(principal);

            Map<String, Object> response = new HashMap<>();
            response.put("type", "INCOMING_CALL");
            response.put("callID", message.getCallID());
            response.put("initiatorID", principal.getName());
            response.put("initiatorName", callerName);
            response.put("conversationID", message.getConversationID());
            response.put("callType", message.getType()); // FE hay dùng callType thay vì type
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSendToUser(
                    receiverUsername,
                    "/topic/call.incoming",
                    response
            );

            log.info("ĐÃ GỬI INCOMING_CALL đến user: {} (ID: {})", receiverUsername, message.getReceiverID());

        } catch (Exception e) {
            log.error("Lỗi xử lý CALL_INITIATE: {}", e.getMessage(), e);
            sendError(principal.getName(), "Không thể khởi tạo cuộc gọi");
        }
    }

    @MessageMapping("/call.answer")
    public void handleAnswerCall(CallAnswerMessage message, Principal principal) {
        try {
            UserEntity initiator = userRepository.findById(message.getInitiatorID()).orElse(null);
            if (initiator == null || initiator.getUsername() == null) {
                log.warn("Không tìm thấy initiator ID: {}", message.getInitiatorID());
                return;
            }

            Map<String, Object> response = new HashMap<>();
            response.put("type", "CALL_ANSWERED");
            response.put("callID", message.getCallID());
            response.put("accepted", message.isAccepted());
            response.put("respondentID", principal.getName());
            response.put("respondentName", getCallerDisplayName(principal));
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSendToUser(
                    initiator.getUsername(),
                    "/topic/call.answered",
                    response
            );

            log.info("ĐÃ GỬI CALL_ANSWERED đến user: {}", initiator.getUsername());

        } catch (Exception e) {
            log.error("Lỗi xử lý CALL_ANSWER: {}", e.getMessage(), e);
        }
    }

    @MessageMapping("/call.reject")
    public void handleRejectCall(CallRejectMessage message, Principal principal) {
        try {
            UserEntity initiator = userRepository.findById(message.getInitiatorID()).orElse(null);
            if (initiator == null || initiator.getUsername() == null) {
                log.warn("Không tìm thấy initiator ID: {}", message.getInitiatorID());
                return;
            }

            Map<String, Object> response = new HashMap<>();
            response.put("type", "CALL_REJECTED");
            response.put("callID", message.getCallID());
            response.put("rejectedByID", principal.getName());
            response.put("rejectedByName", getCallerDisplayName(principal));
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSendToUser(
                    initiator.getUsername(),
                    "/topic/call.rejected",
                    response
            );

            log.info("ĐÃ GỬI CALL_REJECTED đến user: {}", initiator.getUsername());

        } catch (Exception e) {
            log.error("Lỗi xử lý CALL_REJECT: {}", e.getMessage(), e);
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
            log.error("Lỗi xử lý CALL_END: {}", e.getMessage(), e);
        }
    }

    // Helper: Lấy tên hiển thị (fullName > username > Unknown)
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

    private void sendError(String username, String msg) {
        Map<String, Object> error = new HashMap<>();
        error.put("type", "CALL_ERROR");
        error.put("message", msg);
        messagingTemplate.convertAndSendToUser(username, "/topic/call.error", error);
    }
}