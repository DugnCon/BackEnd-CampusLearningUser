package com.javaweb.service.SolvingByRabbitMQ.Comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.config.RabbitMQConfig;
import com.javaweb.entity.Post.CommentEntity;
import com.javaweb.entity.Post.PostEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.model.dto.Post.PostCommentDTO;
import com.javaweb.repository.ICommentRepository;
import com.javaweb.repository.IPostRepository;
import com.javaweb.repository.IUserRepository;
import com.javaweb.utils.ChatSocketHandler;
import com.javaweb.utils.CommentSocketHandler;
import com.javaweb.utils.MapUtils;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CommentConsumer {

    @Autowired
    private IPostRepository postRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ICommentRepository commentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CommentSocketHandler commentSocketHandler;

    @Autowired
    @Qualifier("commentRedisTemplate")
    private RedisTemplate<String, String> commentRedisTemplate;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = RabbitMQConfig.COMMENT_UPLOAD_QUEUE)
    public void receiveUploadTask(Map<String, Object> message) throws JsonProcessingException {
        Long postId = MapUtils.getObject(message, "postId", Long.class);
        Long userId = MapUtils.getObject(message, "userId", Long.class);
        String content = MapUtils.getObject(message, "comment", String.class);

        System.out.println("Processing upload for comment of post " + postId);

        assert postId != null;
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        assert userId != null;
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String fullName = userEntity.getFullName();
        Long userID = userEntity.getUserID();
        String userImage = userEntity.getImage();

        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setUser(userEntity);
        commentEntity.setPost(postEntity);
        commentEntity.setContent(content);
        commentEntity.setIsDeleted(false);
        commentEntity.setIsEdited(false);
        commentEntity.setLikesCount(0);
        commentEntity.setRepliesCount(0);

        // Lưu vào MySQL
        commentRepository.save(commentEntity);

        // Tạo DTO
        PostCommentDTO postCommentDTO = modelMapper.map(commentEntity, PostCommentDTO.class);
        postCommentDTO.setFullName(fullName);
        postCommentDTO.setUserID(userID);
        postCommentDTO.setUserImage(userImage);

        // ---- Redis Key ----
        String postCommentsKey = "post:" + postId + ":comments"; // danh sách commentId của post
        String commentDetailKey = "comment:detail:" + commentEntity.getCommentID(); // chi tiết comment

        // ---- Chuyển DTO thành JSON ----
        String jsonComment = objectMapper.writeValueAsString(postCommentDTO);

        // ---- Lưu chi tiết comment riêng ----
        commentRedisTemplate.opsForValue().set(commentDetailKey, jsonComment);

        // ---- Thêm commentId vào danh sách post ----
        commentRedisTemplate.opsForList().leftPush(postCommentsKey, String.valueOf(commentEntity.getCommentID()));
        commentRedisTemplate.opsForList().trim(postCommentsKey, 0, 99); // giữ 100 comment mới nhất

        System.out.println("Upload complete for comment " + commentEntity.getCommentID());

        broadcastRealTimeComment(postCommentDTO, postId);
    }

    private void broadcastRealTimeComment(PostCommentDTO commentDTO, Long postId) {
        try {
            // Tạo message object giống như chat
            Map<String, Object> socketMessage = new HashMap<>();
            socketMessage.put("type", "NEW_COMMENT");
            socketMessage.put("data", commentDTO);
            socketMessage.put("postId", postId);
            socketMessage.put("timestamp", System.currentTimeMillis());

            commentSocketHandler.broadcastToPost(postId, socketMessage);

            System.out.println("REAL-TIME: WebSocket sent new comment to FE - CommentID: " + commentDTO.getCommentID());

        } catch (Exception e) {
            System.err.println("Failed to send WebSocket event: " + e.getMessage());
            e.printStackTrace();
        }
    }
}