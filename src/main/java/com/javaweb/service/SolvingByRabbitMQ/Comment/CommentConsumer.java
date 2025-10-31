package com.javaweb.service.SolvingByRabbitMQ.Comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.config.RabbitMQConfig;
import com.javaweb.entity.Post.CommentEntity;
import com.javaweb.entity.Post.PostEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.model.dto.PostCommentDTO;
import com.javaweb.repository.ICommentRepository;
import com.javaweb.repository.IPostRepository;
import com.javaweb.repository.IUserRepository;
import com.javaweb.utils.MapUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
    @Qualifier("commentRedisTemplate")
    private RedisTemplate<String, String> commentRedisTemplate;

    @RabbitListener(queues = RabbitMQConfig.COMMENT_UPLOAD_QUEUE)
    public void receiveUploadTask(Map<String, Object> message) throws JsonProcessingException {
        Long postId = MapUtils.getObject(message, "postId", Long.class);
        Long userId = MapUtils.getObject(message, "userId", Long.class);
        String content = MapUtils.getObject(message, "comment", String.class);

        System.out.println("Processing upload for comment of post " + postId);

        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

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
        PostCommentDTO postCommentDTO = new PostCommentDTO();
        BeanUtils.copyProperties(commentEntity, postCommentDTO);
        postCommentDTO.setFullName(userEntity.getFullName());
        postCommentDTO.setUserID(userEntity.getUserID());

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
    }
}
