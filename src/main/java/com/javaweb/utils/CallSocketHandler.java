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
            String callerId = principal.getName();
            log.info("CALL INITIATE từ user {} → user {}", callerId, message.getReceiverID());

            String callerName = getDisplayName(callerId);

            Map<String, Object> payload = new HashMap<>();
            payload.put("callID", message.getCallID());
            payload.put("initiatorID", callerId);
            payload.put("initiatorName", callerName);
            payload.put("conversationID", message.getConversationID());
            payload.put("callType", message.getType());
            payload.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSendToUser(
                    message.getReceiverID().toString(),
                    "/queue/call.incoming",
                    payload
            );

            log.info("Đã gửi INCOMING_CALL đến user {}", message.getReceiverID());
        } catch (Exception e) {
            log.error("Lỗi handleInitiateCall: {}", e.getMessage(), e);
            sendError(principal.getName(), "Không thể khởi tạo cuộc gọi");
        }
    }

    @MessageMapping("/call.answer")
    public void handleAnswerCall(CallAnswerMessage message, Principal principal) {
        try {
            String respondentId = principal.getName();
            log.info("CALL ANSWERED - callID: {}, accepted: {}", message.getCallID(), message.isAccepted());

            Map<String, Object> payload = new HashMap<>();
            payload.put("callID", message.getCallID());
            payload.put("accepted", message.isAccepted());
            payload.put("respondentID", respondentId);
            payload.put("respondentName", getDisplayName(respondentId));
            payload.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSendToUser(
                    message.getInitiatorID().toString(),
                    "/queue/call.answered",
                    payload
            );

            log.info("Đã gửi CALL_ANSWERED đến initiator {}", message.getInitiatorID());
        } catch (Exception e) {
            log.error("Lỗi handleAnswerCall: {}", e.getMessage(), e);
        }
    }

    @MessageMapping("/call.reject")
    public void handleRejectCall(CallRejectMessage message, Principal principal) {
        try {
            String rejectorId = principal.getName();
            log.info("CALL REJECTED - callID: {}", message.getCallID());

            Map<String, Object> payload = new HashMap<>();
            payload.put("callID", message.getCallID());
            payload.put("rejectedByID", rejectorId);
            payload.put("rejectedByName", getDisplayName(rejectorId));
            payload.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSendToUser(
                    message.getInitiatorID().toString(),
                    "/queue/call.rejected",
                    payload
            );

            log.info("Đã gửi CALL_REJECTED đến initiator {}", message.getInitiatorID());
        } catch (Exception e) {
            log.error("Lỗi handleRejectCall: {}", e.getMessage(), e);
        }
    }

    @MessageMapping("/call.end")
    public void handleEndCall(CallEndMessage message, Principal principal) {
        try {
            String enderId = principal.getName();
            String targetId = message.getTargetUserID().toString();

            log.info("CALL ENDED - callID: {}, bởi: {}", message.getCallID(), enderId);

            Map<String, Object> payload = new HashMap<>();
            payload.put("callID", message.getCallID());
            payload.put("endedByID", enderId);
            payload.put("endedByName", getDisplayName(enderId));
            payload.put("reason", message.getReason() != null ? message.getReason() : "normal");
            payload.put("duration", message.getDuration());
            payload.put("timestamp", System.currentTimeMillis());

            // GỬI CHO CẢ 2 BÊN - QUAN TRỌNG!!!
            messagingTemplate.convertAndSendToUser(enderId, "/queue/call.ended", payload);
            messagingTemplate.convertAndSendToUser(targetId, "/queue/call.ended", payload);

            log.info("Đã gửi CALL_ENDED cho cả 2 bên: {} và {}", enderId, targetId);
        } catch (Exception e) {
            log.error("Lỗi handleEndCall: {}", e.getMessage(), e);
        }
    }

    private String getDisplayName(String userId) {
        try {
            UserEntity user = userRepository.findById(Long.valueOf(userId)).orElse(null);
            if (user != null) {
                return user.getFullName() != null && !user.getFullName().trim().isEmpty()
                        ? user.getFullName() : user.getUsername();
            }
        } catch (Exception e) {
            log.warn("Lỗi lấy display name cho user {}: {}", userId, e.getMessage());
        }
        return "Người dùng";
    }

    private void sendError(String userId, String msg) {
        Map<String, Object> error = new HashMap<>();
        error.put("message", msg);
        error.put("timestamp", System.currentTimeMillis());

        messagingTemplate.convertAndSendToUser(userId, "/queue/call.error", error);
    }
}