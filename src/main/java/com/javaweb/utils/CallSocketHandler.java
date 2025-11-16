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
public class CallSocketHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Xử lý khi có người khởi tạo cuộc gọi - GỬI DIRECT ĐẾN USER
     */
    @MessageMapping("/call.initiate")
    public void handleInitiateCall(CallInitiateMessage message, Principal principal) {
        try {
            log.info("📞 SOCKET - CALL INITIATE: from={}, to={}, type={}, callId={}",
                    principal.getName(), message.getReceiverID(), message.getType(), message.getCallID());

            // Gửi DIRECT đến user cụ thể
            Map<String, Object> response = new HashMap<>();
            response.put("type", "INCOMING_CALL");
            response.put("call", message);
            response.put("initiatorID", principal.getName());
            response.put("initiatorName", principal.getName());
            response.put("timestamp", System.currentTimeMillis());

            // Gửi đến user cụ thể thay vì broadcast
            messagingTemplate.convertAndSendToUser(
                    message.getReceiverID(),
                    "/topic/call.incoming",
                    response
            );

            log.info("✅ Đã gửi INCOMING_CALL đến user: {}", message.getReceiverID());

        } catch (Exception e) {
            log.error("❌ Lỗi xử lý CALL_INITIATE: {}", e.getMessage(), e);

            // Gửi lỗi về cho caller
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("type", "CALL_ERROR");
            errorResponse.put("message", "Failed to initiate call");
            errorResponse.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/topic/call.error",
                    errorResponse
            );
        }
    }

    /**
     * Xử lý khi có người trả lời cuộc gọi - GỬI DIRECT ĐẾN INITIATOR
     */
    @MessageMapping("/call.answer")
    public void handleAnswerCall(CallAnswerMessage message, Principal principal) {
        try {
            log.info("📞 SOCKET - CALL ANSWER: respondent={}, callId={}, accepted={}",
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
                    message.getInitiatorID(),
                    "/topic/call.answered",
                    response
            );

            log.info("✅ Đã gửi CALL_ANSWERED đến initiator: {}", message.getInitiatorID());

        } catch (Exception e) {
            log.error("❌ Lỗi xử lý CALL_ANSWER: {}", e.getMessage(), e);
        }
    }

    /**
     * Xử lý khi kết thúc cuộc gọi
     */
    @MessageMapping("/call.end")
    public void handleEndCall(CallEndMessage message, Principal principal) {
        try {
            log.info("📞 SOCKET - CALL END: callId={}, endedBy={}, reason={}",
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

            log.info("✅ Đã gửi CALL_ENDED đến call: {}", message.getCallID());

        } catch (Exception e) {
            log.error("❌ Lỗi xử lý CALL_END: {}", e.getMessage(), e);
        }
    }

    /**
     * Xử lý khi từ chối cuộc gọi
     */
    @MessageMapping("/call.reject")
    public void handleRejectCall(CallRejectMessage message, Principal principal) {
        try {
            log.info("📞 SOCKET - CALL REJECT: callId={}, rejectedBy={}",
                    message.getCallID(), principal.getName());

            Map<String, Object> response = new HashMap<>();
            response.put("type", "CALL_REJECTED");
            response.put("callID", message.getCallID());
            response.put("rejectedByID", principal.getName());
            response.put("rejectedByName", principal.getName());
            response.put("timestamp", System.currentTimeMillis());

            // Gửi đến initiator của cuộc gọi
            messagingTemplate.convertAndSendToUser(
                    message.getInitiatorID(),
                    "/topic/call.rejected",
                    response
            );

            log.info("✅ Đã gửi CALL_REJECTED đến initiator: {}", message.getInitiatorID());

        } catch (Exception e) {
            log.error("❌ Lỗi xử lý CALL_REJECT: {}", e.getMessage(), e);
        }
    }
}