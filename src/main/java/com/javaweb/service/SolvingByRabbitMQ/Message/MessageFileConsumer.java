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
import com.javaweb.service.FileStorageService;
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
public class MessageFileConsumer {

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

    @Autowired
    private FileStorageService fileStorageService;

    @RabbitListener(queues = RabbitMQConfig.FILE_UPLOAD_QUEUE)
    public void receiveUploadTask(Map<String, Object> messageData) {
        String tempMessageId = null;
        Long conversationId = MapUtils.getObject(messageData, "conversationId", Long.class);
        Long userId = MapUtils.getObject(messageData, "userId", Long.class);

        try {
            // Extract data từ message
            String filePath = MapUtils.getObject(messageData, "file", String.class);
            tempMessageId = MapUtils.getObject(messageData, "tempMessageId", String.class);
            String caption = MapUtils.getObject(messageData, "caption", String.class);

            System.out.println("Đang xử lý file cho cuộc trò chuyện: " + conversationId);

            // Validation
            if (conversationId == null || userId == null || filePath == null) {
                throw new IllegalArgumentException("Thiếu các trường bắt buộc: conversationId, userId, file");
            }

            // Tìm conversation và user song song
            CompletableFuture<ConversationEntity> conversationFuture = findConversationAsync(conversationId);
            CompletableFuture<UserEntity> userFuture = findUserAsync(userId);

            CompletableFuture.allOf(conversationFuture, userFuture).join();

            ConversationEntity conversation = conversationFuture.get();
            UserEntity user = userFuture.get();

            // Lấy file info từ path
            Map<String, Object> fileInfo = extractFileInfo(filePath);

            // Tạo và lưu message entity cho file
            MessageEntity messageEntity = createFileMessageEntity(conversation, user, filePath, fileInfo, caption);
            MessageEntity savedMessage = messageRepository.save(messageEntity);

            // Convert to DTO
            MessageDTO messageDTO = convertFileToDTO(savedMessage, user, fileInfo);

            // Cache vào Redis
            cacheMessageToRedis(savedMessage, messageDTO, conversationId);

            // Cập nhật last message của conversation
            updateConversationLastMessage(conversation, savedMessage);

            // Broadcast real-time message
            broadcastRealTimeMessage(conversationId, messageDTO, tempMessageId);

            System.out.println("Xử lý file thành công: " + savedMessage.getMessageID() + " - " + filePath);

        } catch (Exception e) {
            System.err.println("Lỗi khi xử lý file: " + e.getMessage());
            e.printStackTrace();

            // Broadcast error message
            broadcastErrorMessage(conversationId, tempMessageId, e.getMessage());
        }
    }

    private Map<String, Object> extractFileInfo(String filePath) {
        Map<String, Object> fileInfo = new HashMap<>();

        try {
            // Extract file info từ filePath
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            String fileExtension = "";

            if (fileName.contains(".")) {
                fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            }

            // Determine file type
            String fileType = determineFileType(fileExtension);

            fileInfo.put("originalName", fileName);
            fileInfo.put("type", fileType);
            fileInfo.put("mimetype", getMimeType(fileExtension));
            fileInfo.put("size", getFileSize(filePath));
            fileInfo.put("extension", fileExtension);

        } catch (Exception e) {
            System.err.println("Lỗi extract file info: " + e.getMessage());
            // Default values
            fileInfo.put("originalName", "file");
            fileInfo.put("type", "file");
            fileInfo.put("mimetype", "application/octet-stream");
            fileInfo.put("size", 0);
            fileInfo.put("extension", "unknown");
        }

        return fileInfo;
    }

    private String determineFileType(String extension) {
        // Image types
        if (extension.matches("(jpg|jpeg|png|gif|bmp|webp|svg)")) {
            return "image";
        }
        // Video types
        else if (extension.matches("(mp4|avi|mov|wmv|flv|webm|mkv)")) {
            return "video";
        }
        // Audio types
        else if (extension.matches("(mp3|wav|ogg|flac|aac)")) {
            return "audio";
        }
        // Document types
        else if (extension.matches("(pdf|doc|docx|txt)")) {
            return "document";
        }
        else {
            return "file";
        }
    }

    private String getMimeType(String extension) {
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "mp4":
                return "video/mp4";
            case "avi":
                return "video/x-msvideo";
            case "mov":
                return "video/quicktime";
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "txt":
                return "text/plain";
            default:
                return "application/octet-stream";
        }
    }

    private Long getFileSize(String filePath) {
        try {
            // Implement method to get file size from storage service
            // This depends on your FileStorageService implementation
            return fileStorageService.getFileSize(filePath);
        } catch (Exception e) {
            return 0L;
        }
    }

    private MessageEntity createFileMessageEntity(ConversationEntity conversation, UserEntity user,
                                                  String filePath, Map<String, Object> fileInfo, String caption) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setSender(user);
        messageEntity.setConversation(conversation);

        // Nếu có caption thì dùng caption, không thì dùng tên file
        String content = caption != null && !caption.trim().isEmpty()
                ? caption.trim()
                : "Đã gửi một file: " + fileInfo.get("originalName");

        messageEntity.setContent(content);
        messageEntity.setType("file");

        //Lưu filePath vào MediaUrl
        messageEntity.setMediaUrl(filePath);

        //Lưu loại media vào MediaType (image, video, document, etc.)
        messageEntity.setMediaType((String) fileInfo.get("type"));

        messageEntity.setIsDeleted(false);
        messageEntity.setIsEdited(false);
        messageEntity.setCreatedAt(LocalDateTime.now());

        // Vẫn lưu metadata vào JSON field (nếu cần thêm info)
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("fileInfo", fileInfo);
        metadata.put("originalName", fileInfo.get("originalName"));
        metadata.put("size", fileInfo.get("size"));
        metadata.put("mimetype", fileInfo.get("mimetype"));

        return messageEntity;
    }

    private MessageDTO convertFileToDTO(MessageEntity message, UserEntity sender, Map<String, Object> fileInfo) {
        MessageDTO messageDTO = modelMapper.map(message, MessageDTO.class);

        messageDTO.setConversationID(message.getConversation().getConversationID());
        messageDTO.setSenderID(sender.getUserID());
        messageDTO.setSenderName(sender.getFullName());
        messageDTO.setSenderAvatar(sender.getAvatar());
        messageDTO.setCreatedAt(message.getCreatedAt());

        //Set MediaUrl từ entity
        messageDTO.setMediaUrl(message.getMediaUrl());
        messageDTO.setType("file");

        // Set file info từ metadata hoặc extract từ MediaUrl
        Map<String, Object> messageFileInfo = new HashMap<>();
        messageFileInfo.put("originalName", fileInfo.get("originalName"));
        messageFileInfo.put("type", message.getMediaType()); // Lấy từ MediaType
        messageFileInfo.put("mimetype", fileInfo.get("mimetype"));
        messageFileInfo.put("size", fileInfo.get("size"));
        messageFileInfo.put("extension", fileInfo.get("extension"));

        messageDTO.setFileInfo(messageFileInfo);

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

            System.out.println("Đã broadcast file message đến conversation: " + conversationId);

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

            System.out.println("Đã broadcast error message: " + errorMessage);

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