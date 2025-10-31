package com.javaweb.service.SolvingByRabbitMQ.Message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.config.RabbitMQConfig;
import com.javaweb.entity.ChatAndCall.ConversationEntity;
import com.javaweb.entity.ChatAndCall.MessageEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.model.dto.ChatAndCall.MessageDTO;
import com.javaweb.repository.IConversationRepository;
import com.javaweb.repository.IMessageRepository;
import com.javaweb.repository.IUserRepository;
import com.javaweb.utils.ChatSocketHandler;
import com.javaweb.utils.MapUtils;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class MessageConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("messageRedisTemplate")
    private RedisTemplate<String, Object> messageRedisTemplate;

    @Autowired
    private IMessageRepository messageRepository;

    @Autowired
    private IConversationRepository conversationRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ChatSocketHandler chatSocketHandler;

    @RabbitListener(queues = RabbitMQConfig.MESSAGE_UPLOAD_QUEUE)
    public void receiveUploadTask(Map<String, Object> messageData) {
        String tempMessageId = null;
        Long conversationId = MapUtils.getObject(messageData, "conversationId", Long.class);
        Long userId = MapUtils.getObject(messageData, "userId", Long.class);

        try {
            // Extract data từ message
            String content = MapUtils.getObject(messageData, "content", String.class);
            String type = MapUtils.getObject(messageData, "type", String.class);
            tempMessageId = MapUtils.getObject(messageData, "tempMessageId", String.class);

            System.out.println("Đang xử lý tin nhắn cho cuộc trò chuyện: " + conversationId);

            // Validation
            if (conversationId == null || userId == null || content == null) {
                throw new IllegalArgumentException("Thiếu các trường bắt buộc: conversationId, userId, content");
            }

            // Tìm conversation và user song song
            CompletableFuture<ConversationEntity> conversationFuture = findConversationAsync(conversationId);
            CompletableFuture<UserEntity> userFuture = findUserAsync(userId);

            CompletableFuture.allOf(conversationFuture, userFuture).join();

            ConversationEntity conversation = conversationFuture.get();
            UserEntity user = userFuture.get();

            // Tạo và lưu message entity
            MessageEntity messageEntity = createMessageEntity(conversation, user, content, type);
            MessageEntity savedMessage = messageRepository.save(messageEntity);

            // Convert to DTO
            MessageDTO messageDTO = convertToDTO(savedMessage, user);

            // Cache vào Redis
            cacheMessageToRedis(savedMessage, messageDTO, conversationId);

            // Cập nhật last message của conversation
            updateConversationLastMessage(conversation, savedMessage);

            // Broadcast real-time message
            broadcastRealTimeMessage(conversationId, messageDTO, tempMessageId);

            System.out.println("Xử lý tin nhắn thành công: " + savedMessage.getMessageID());

        } catch (Exception e) {
            System.err.println("Lỗi khi xử lý tin nhắn: " + e.getMessage());
            e.printStackTrace();

            // Broadcast error message
            broadcastErrorMessage(conversationId, tempMessageId, e.getMessage());
        }
    }

    private MessageEntity createMessageEntity(ConversationEntity conversation, UserEntity user,
                                              String content, String type) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setSender(user);
        messageEntity.setConversation(conversation);
        messageEntity.setContent(content);
        messageEntity.setType(type != null ? type : "text");
        messageEntity.setIsDeleted(false);
        messageEntity.setIsEdited(false);
        messageEntity.setCreatedAt(LocalDateTime.now());

        return messageEntity;
    }

    private MessageDTO convertToDTO(MessageEntity message, UserEntity sender) {
        MessageDTO messageDTO = modelMapper.map(message, MessageDTO.class);

        messageDTO.setConversationID(message.getConversation().getConversationID());
        messageDTO.setSenderID(sender.getUserID());
        messageDTO.setSenderName(sender.getFullName());
        messageDTO.setSenderAvatar(sender.getAvatar());
        messageDTO.setCreatedAt(message.getCreatedAt());

        return messageDTO;
    }

    private void cacheMessageToRedis(MessageEntity message, MessageDTO messageDTO, Long conversationId) {
        try {
            String conversationMessageKey = "conversation:" + conversationId + ":messages";
            String messageDetailKey = "message:detail:" + message.getMessageID();

            // Cache chi tiết tin nhắn
            String jsonMessage = objectMapper.writeValueAsString(messageDTO);
            messageRedisTemplate.opsForValue().set(messageDetailKey, jsonMessage);

            // Thêm vào danh sách tin nhắn của conversation
            messageRedisTemplate.opsForList().leftPush(conversationMessageKey,
                    String.valueOf(message.getMessageID()));

            // Giữ chỉ 100 tin nhắn mới nhất
            messageRedisTemplate.opsForList().trim(conversationMessageKey, 0, 99);

        } catch (Exception e) {
            System.err.println("Lỗi cache Redis: " + e.getMessage());
        }
    }

    private void updateConversationLastMessage(ConversationEntity conversation, MessageEntity message) {
        try {
            conversation.setLastMessageAt(LocalDateTime.now());
            conversation.setUpdatedAt(LocalDateTime.now());
            conversationRepository.save(conversation);

            System.out.println("Đã cập nhật last message time cho conversation: " + conversation.getConversationID());

        } catch (Exception e) {
            System.err.println("Lỗi cập nhật last message: " + e.getMessage());
        }
    }

    private void broadcastRealTimeMessage(Long conversationId, MessageDTO messageDTO, String tempMessageId) {
        try {
            // Tạo payload cho real-time message
            Map<String, Object> socketPayload = new HashMap<>();
            socketPayload.put("type", "NEW_MESSAGE");
            socketPayload.put("data", messageDTO);
            socketPayload.put("conversationId", conversationId);
            socketPayload.put("timestamp", System.currentTimeMillis());

            // Thêm tempMessageId để FE có thể replace tin nhắn tạm
            if (tempMessageId != null) {
                socketPayload.put("tempMessageId", tempMessageId);
            }

            // Broadcast đến tất cả clients trong conversation
            chatSocketHandler.broadcastToConversation(conversationId, socketPayload);

            System.out.println("Đã broadcast message đến conversation: " + conversationId);

        } catch (Exception e) {
            System.err.println("Lỗi broadcast message: " + e.getMessage());
            throw new RuntimeException("Broadcast failed", e);
        }
    }

    private void broadcastErrorMessage(Long conversationId, String tempMessageId, String errorMessage) {
        try {
            Map<String, Object> errorPayload = new HashMap<>();
            errorPayload.put("type", "MESSAGE_FAILED");
            errorPayload.put("data", Map.of(
                    "tempMessageId", tempMessageId,
                    "conversationId", conversationId,
                    "error", errorMessage,
                    "timestamp", System.currentTimeMillis()
            ));

            // Broadcast lỗi đến conversation
            chatSocketHandler.broadcastToConversation(conversationId, errorPayload);

        } catch (Exception e) {
            System.err.println("Lỗi broadcast error message: " + e.getMessage());
        }
    }

    private CompletableFuture<ConversationEntity> findConversationAsync(Long conversationId) {
        return CompletableFuture.supplyAsync(() ->
                conversationRepository.findById(conversationId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc trò chuyện: " + conversationId))
        );
    }

    private CompletableFuture<UserEntity> findUserAsync(Long userId) {
        return CompletableFuture.supplyAsync(() ->
                userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + userId))
        );
    }
}