package com.javaweb.utils;

import com.javaweb.model.dto.ChatAndCall.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ChatSocketHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Broadcast message đến tất cả clients trong conversation
     * Nhận Map<String, Object> làm parameter
     */
    public void broadcastToConversation(Long conversationId, Map<String, Object> payload) {
        try {
            String destination = "/topic/conversation." + conversationId;
            messagingTemplate.convertAndSend(destination, payload);

            System.out.println("Đã broadcast đến " + destination + ": " + payload.get("type"));

        } catch (Exception e) {
            System.err.println("Lỗi broadcast: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Overload method: vẫn hỗ trợ MessageDTO cho backward compatibility
     */
    public void broadcastToConversation(Long conversationId, MessageDTO messageDTO) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "NEW_MESSAGE");
            payload.put("data", messageDTO);
            payload.put("conversationId", conversationId);
            payload.put("timestamp", System.currentTimeMillis());

            broadcastToConversation(conversationId, payload);

        } catch (Exception e) {
            System.err.println("Lỗi broadcast MessageDTO: " + e.getMessage());
        }
    }

    /**
     * Gửi message đến specific user
     */
    public void sendToUser(String userId, Map<String, Object> payload) {
        try {
            String destination = "/user/" + userId + "/queue/messages";
            messagingTemplate.convertAndSend(destination, payload);

        } catch (Exception e) {
            System.err.println("Lỗi send to user: " + e.getMessage());
        }
    }

    /**
     * Broadcast typing indicator
     */
    public void broadcastTyping(Long conversationId, Long userId, String username, boolean isTyping) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "TYPING");
            payload.put("data", Map.of(
                    "userId", userId,
                    "username", username,
                    "isTyping", isTyping,
                    "conversationId", conversationId
            ));
            payload.put("timestamp", System.currentTimeMillis());

            String destination = "/topic/conversation." + conversationId + ".typing";
            messagingTemplate.convertAndSend(destination, payload);

        } catch (Exception e) {
            System.err.println("Lỗi broadcast typing: " + e.getMessage());
        }
    }

    /**
     * Broadcast message deleted
     */
    public void broadcastMessageDeleted(Long conversationId, Long messageId, boolean deletedForEveryone) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "MESSAGE_DELETED");
            payload.put("data", Map.of(
                    "messageId", messageId,
                    "conversationId", conversationId,
                    "deletedForEveryone", deletedForEveryone
            ));
            payload.put("timestamp", System.currentTimeMillis());

            broadcastToConversation(conversationId, payload);

        } catch (Exception e) {
            System.err.println("Lỗi broadcast message deleted: " + e.getMessage());
        }
    }
}