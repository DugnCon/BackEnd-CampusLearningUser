package com.javaweb.utils;

import com.javaweb.model.dto.Post.PostCommentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CommentSocketHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Broadcast comment đến tất cả clients trong post
     * Nhận Map<String, Object> làm parameter
     */
    public void broadcastToPost(Long postId, Map<String, Object> payload) {
        try {
            String destination = "/topic/post." + postId + ".comments";
            messagingTemplate.convertAndSend(destination, payload);

            System.out.println("Đã broadcast comment đến " + destination + ": " + payload.get("type"));

        } catch (Exception e) {
            System.err.println("Lỗi broadcast comment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Overload method: hỗ trợ PostCommentDTO
     */
    public void broadcastToPost(Long postId, PostCommentDTO commentDTO) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "NEW_COMMENT");
            payload.put("data", commentDTO);
            payload.put("postId", postId);
            payload.put("timestamp", System.currentTimeMillis());

            broadcastToPost(postId, payload);

        } catch (Exception e) {
            System.err.println("Lỗi broadcast PostCommentDTO: " + e.getMessage());
        }
    }

    /**
     * Broadcast comment mới
     */
    public void broadcastNewComment(Long postId, PostCommentDTO commentDTO) {
        broadcastToPost(postId, commentDTO);
    }

    /**
     * Broadcast comment bị xóa
     */
    public void broadcastCommentDeleted(Long postId, Long commentId, boolean deletedByAuthor) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "COMMENT_DELETED");
            payload.put("data", Map.of(
                    "commentId", commentId,
                    "postId", postId,
                    "deletedByAuthor", deletedByAuthor
            ));
            payload.put("timestamp", System.currentTimeMillis());

            broadcastToPost(postId, payload);

        } catch (Exception e) {
            System.err.println("Lỗi broadcast comment deleted: " + e.getMessage());
        }
    }

    /**
     * Broadcast comment được like/unlike
     */
    public void broadcastCommentLiked(Long postId, Long commentId, Long userId, boolean isLiked, int likesCount) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "COMMENT_LIKED");
            payload.put("data", Map.of(
                    "commentId", commentId,
                    "userId", userId,
                    "isLiked", isLiked,
                    "likesCount", likesCount,
                    "postId", postId
            ));
            payload.put("timestamp", System.currentTimeMillis());

            broadcastToPost(postId, payload);

        } catch (Exception e) {
            System.err.println("Lỗi broadcast comment liked: " + e.getMessage());
        }
    }

    /**
     * Broadcast post được like/unlike
     */
    public void broadcastPostLiked(Long postId, Long userId, boolean isLiked, int likesCount) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "POST_LIKED");
            payload.put("data", Map.of(
                    "postId", postId,
                    "userId", userId,
                    "isLiked", isLiked,
                    "likesCount", likesCount
            ));
            payload.put("timestamp", System.currentTimeMillis());

            String destination = "/topic/post." + postId + ".likes";
            messagingTemplate.convertAndSend(destination, payload);

        } catch (Exception e) {
            System.err.println("Lỗi broadcast post liked: " + e.getMessage());
        }
    }

    /**
     * Broadcast số lượng comment thay đổi
     */
    public void broadcastCommentCount(Long postId, int commentsCount) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "COMMENT_COUNT_UPDATED");
            payload.put("data", Map.of(
                    "postId", postId,
                    "commentsCount", commentsCount
            ));
            payload.put("timestamp", System.currentTimeMillis());

            String destination = "/topic/post." + postId + ".count";
            messagingTemplate.convertAndSend(destination, payload);

        } catch (Exception e) {
            System.err.println("Lỗi broadcast comment count: " + e.getMessage());
        }
    }

    /**
     * Gửi thông báo đến specific user về comment
     */
    public void sendToUser(String userId, Map<String, Object> payload) {
        try {
            String destination = "/user/" + userId + "/queue/comments";
            messagingTemplate.convertAndSend(destination, payload);

        } catch (Exception e) {
            System.err.println("Lỗi send comment to user: " + e.getMessage());
        }
    }

    /**
     * Gửi thông báo mention trong comment
     */
    public void sendMentionNotification(String userId, Long postId, Long commentId, String mentionedBy) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "MENTION_IN_COMMENT");
            payload.put("data", Map.of(
                    "postId", postId,
                    "commentId", commentId,
                    "mentionedBy", mentionedBy,
                    "timestamp", System.currentTimeMillis()
            ));

            sendToUser(userId, payload);

        } catch (Exception e) {
            System.err.println("Lỗi send mention notification: " + e.getMessage());
        }
    }

    /**
     * Broadcast comment được edit
     */
    public void broadcastCommentEdited(Long postId, Long commentId, String newContent, Long editedBy) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "COMMENT_EDITED");
            payload.put("data", Map.of(
                    "postId", postId,
                    "commentId", commentId,
                    "newContent", newContent,
                    "editedBy", editedBy,
                    "timestamp", System.currentTimeMillis()
            ));

            broadcastToPost(postId, payload);

        } catch (Exception e) {
            System.err.println("Lỗi broadcast comment edited: " + e.getMessage());
        }
    }

    /**
     * Broadcast real-time comment statistics
     */
    public void broadcastCommentStats(Long postId, int totalComments, int recentComments) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "COMMENT_STATS_UPDATED");
            payload.put("data", Map.of(
                    "postId", postId,
                    "totalComments", totalComments,
                    "recentComments", recentComments,
                    "timestamp", System.currentTimeMillis()
            ));

            String destination = "/topic/post." + postId + ".stats";
            messagingTemplate.convertAndSend(destination, payload);

        } catch (Exception e) {
            System.err.println("Lỗi broadcast comment stats: " + e.getMessage());
        }
    }
}