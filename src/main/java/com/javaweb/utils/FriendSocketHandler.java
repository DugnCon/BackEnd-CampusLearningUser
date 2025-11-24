package com.javaweb.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.javaweb.service.IFriendshipService;
import com.javaweb.service.IFriendshipSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class FriendSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;
    @Autowired
    private IFriendshipService friendshipService;
    @Autowired
    private IFriendshipSocketService friendshipSocketService;

    @MessageMapping("/friend/request")
    public void handleFriendRequest(JsonNode payload) {
        String requesterId = payload.get("requesterId").asText();
        String addresseeId = payload.get("addresseeId").asText();

        if (requesterId.equals(addresseeId)) return;

        Map<String, Object> data = Map.of("addresseeId",addresseeId);
        friendshipSocketService.sendFriendRequest(Long.valueOf(requesterId), Long.valueOf(addresseeId));
        Map<String, Object> notification = Map.of(
                "type", "FRIEND_REQUEST_RECEIVED",
                "request", Map.of(
                        "userID", requesterId,
                        "fullName", getUserInfo(requesterId, "fullName"),
                        "username", getUserInfo(requesterId, "username"),
                        "image", getUserInfo(requesterId, "image")
                )
        );

        messagingTemplate.convertAndSendToUser(
                addresseeId,
                "/queue/friend-requests",
                Map.of("data", notification)
        );
    }

    @MessageMapping("/friend/accept")
    public void handleAcceptFriend(JsonNode payload) {
        String accepterId = payload.get("accepterId").asText();
        String requesterId = payload.get("friendshipId").asText();

        friendshipSocketService.acceptFriendRequest(Long.valueOf(requesterId), Long.valueOf(accepterId));

        Map<String, Object> toRequester = Map.of(
                "type", "FRIEND_REQUEST_ACCEPTED",
                "friend", Map.of(
                        "userID", accepterId,
                        "fullName", getUserInfo(accepterId, "fullName"),
                        "username", getUserInfo(accepterId, "username"),
                        "image", getUserInfo(accepterId, "image"),
                        "status", "ONLINE"
                )
        );

        Map<String, Object> toAccepter = Map.of(
                "type", "FRIEND_REQUEST_ACCEPTED",
                "friend", Map.of(
                        "userID", requesterId,
                        "fullName", getUserInfo(requesterId, "fullName"),
                        "username", getUserInfo(requesterId, "username"),
                        "image", getUserInfo(requesterId, "image"),
                        "status", "ONLINE"
                )
        );

        messagingTemplate.convertAndSendToUser(requesterId, "/queue/friend-updates", Map.of("data", toRequester));
        messagingTemplate.convertAndSendToUser(accepterId, "/queue/friend-updates", Map.of("data", toAccepter));
    }

    @MessageMapping("/friend/reject")
    public void handleRejectFriend(JsonNode payload) {
        String rejecterId = payload.get("rejecterId").asText();
        String requesterId = payload.get("friendshipId").asText();

        //ResponseEntity<Object> response = friendshipService.rejectFriend(Long.valueOf(requesterId), Long.valueOf(rejecterId));
        friendshipSocketService.rejectFriendRequest(Long.valueOf(requesterId), Long.valueOf(rejecterId));
        Map<String, Object> notification = Map.of(
                "type", "FRIEND_REQUEST_REJECTED",
                "userId", rejecterId,
                "userName", getUserInfo(rejecterId, "fullName")
        );

        messagingTemplate.convertAndSendToUser(requesterId, "/queue/friend-updates", Map.of("data", notification));
    }

    @MessageMapping("/friend/cancel")
    public void handleCancelRequest(JsonNode payload) {
        String cancellerId = payload.get("cancellerId").asText();
        String addresseeId = payload.get("friendshipId").asText();

        //ResponseEntity<Object> response = friendshipService.acceptFriend(Long.valueOf(addresseeId), Long.valueOf(cancellerId));
        friendshipSocketService.cancelFriendRequest(Long.valueOf(cancellerId), Long.valueOf(addresseeId));
        Map<String, Object> notification = Map.of(
                "type", "FRIEND_REQUEST_CANCELLED",
                "userId", cancellerId,
                "userName", getUserInfo(cancellerId, "fullName")
        );

        messagingTemplate.convertAndSendToUser(addresseeId, "/queue/friend-updates", Map.of("data", notification));
    }

    @MessageMapping("/friend/remove")
    public void handleRemoveFriend(JsonNode payload, Principal principal) {
        String removerId = payload.get("removerId").asText();
        String removedId = payload.get("friendshipId").asText();

        //ResponseEntity<Object> response = friendshipService.deleteFrienḍ(Long.valueOf(removedId), Long.valueOf(removedId));
        friendshipSocketService.removeFriend(Long.valueOf(removerId),Long.valueOf(removedId));
        Map<String, Object> notification = Map.of(
                "type", "FRIEND_REMOVED",
                "userId", removerId,
                "userName", getUserInfo(removerId, "fullName")
        );

        messagingTemplate.convertAndSendToUser(removedId, "/queue/friend-updates", Map.of("data", notification));
    }

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        String userId = getUserId(event.getMessage());
        if (userId != null) {
            messagingTemplate.convertAndSend("/topic/online-users", Map.of(
                    "data", Map.of(
                            "type", "USER_STATUS_CHANGED",
                            "userId", userId,
                            "status", "ONLINE"
                    )
            ));
        }
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        String userId = getUserId(event.getMessage());
        if (userId != null) {
            messagingTemplate.convertAndSend("/topic/online-users", Map.of(
                    "data", Map.of(
                            "type", "USER_STATUS_CHANGED",
                            "userId", userId,
                            "status", "OFFLINE"
                    )
            ));
        }
    }

    private String getUserId(org.springframework.messaging.Message<?> message) {
        Principal principal = (Principal) message.getHeaders().get("simpUser");
        return principal != null ? principal.getName() : null;
    }

    private String getUserInfo(String userId, String field) {
        return switch (field) {
            case "fullName" -> "Tên Người Dùng";
            case "username" -> "username";
            case "image" -> null;
            default -> "";
        };
    }
}