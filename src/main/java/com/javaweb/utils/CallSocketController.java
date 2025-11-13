package com.javaweb.utils;

import com.javaweb.model.dto.ChatAndCall.CallAnswerMessage;
import com.javaweb.model.dto.ChatAndCall.CallEndMessage;
import com.javaweb.model.dto.ChatAndCall.CallInitiateMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class CallSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Xử lý khi có người khởi tạo cuộc gọi
     */
    @MessageMapping("/call.initiate")
    public void handleInitiateCall(CallInitiateMessage message) {
        try {
            log.info("SOCKET - CALL INITIATE: conversationId={}, callId={}, type={}",
                    message.getConversationID(), message.getCallID(), message.getType());

            // Gửi thông báo đến tất cả participants trong conversation
            Map<String, Object> response = new HashMap<>();
            response.put("type", "CALL_INITIATED");
            response.put("data", message);
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend(
                    "/topic/conversation." + message.getConversationID() + ".call",
                    response
            );

            log.info("Đã gửi CALL_INITIATED đến conversation: {}", message.getConversationID());

        } catch (Exception e) {
            log.error("Lỗi xử lý CALL_INITIATE: {}", e.getMessage(), e);
        }
    }

    /**
     * Xử lý khi có người trả lời cuộc gọi
     */
    @MessageMapping("/call.answer")
    public void handleAnswerCall(CallAnswerMessage message) {
        try {
            log.info("SOCKET - CALL ANSWER: callId={}, respondent={}",
                    message.getCallID(), message.getRespondentName());

            // Gửi thông báo đến tất cả participants trong call
            Map<String, Object> response = new HashMap<>();
            response.put("type", "CALL_ANSWERED");
            response.put("data", message);
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend(
                    "/topic/call." + message.getCallID(),
                    response
            );

            log.info("Đã gửi CALL_ANSWERED đến call: {}", message.getCallID());

        } catch (Exception e) {
            log.error("Lỗi xử lý CALL_ANSWER: {}", e.getMessage(), e);
        }
    }

    /**
     * Xử lý khi kết thúc cuộc gọi
     */
    @MessageMapping("/call.end")
    public void handleEndCall(CallEndMessage message) {
        try {
            log.info("SOCKET - CALL END: callId={}, endedBy={}, reason={}",
                    message.getCallID(), message.getEndedByName(), message.getReason());

            // Gửi thông báo đến tất cả participants trong call
            Map<String, Object> response = new HashMap<>();
            response.put("type", "CALL_ENDED");
            response.put("data", message);
            response.put("timestamp", System.currentTimeMillis());

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
    public void handleRejectCall(CallEndMessage message) {
        try {
            log.info("SOCKET - CALL REJECT: callId={}, rejectedBy={}",
                    message.getCallID(), message.getEndedByName());

            message.setReason("rejected");

            // Gửi thông báo đến tất cả participants trong call
            Map<String, Object> response = new HashMap<>();
            response.put("type", "CALL_REJECTED");
            response.put("data", message);
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend(
                    "/topic/call." + message.getCallID(),
                    response
            );

            log.info("Đã gửi CALL_REJECTED đến call: {}", message.getCallID());

        } catch (Exception e) {
            log.error("Lỗi xử lý CALL_REJECT: {}", e.getMessage(), e);
        }
    }
}