package com.javaweb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.entity.Post.CommentEntity;
import com.javaweb.model.dto.Post.PostCommentDTO;
import com.javaweb.repository.ICommentRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class PostCommentService {

    @Autowired
    @Qualifier("commentRedisTemplate")
    private RedisTemplate<String, String> commentRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ICommentRepository commentRepository;

    public ResponseEntity<Object> getCommentsByPostId(Long postId, Long userId) {
        String postKey = "post:" + postId + ":comments"; // danh sách commentId
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            //Lấy danh sách commentId từ Redis
            List<String> commentIds = commentRedisTemplate.opsForList().range(postKey, 0, -1);

            if (commentIds != null && !commentIds.isEmpty()) {
                for (String commentId : commentIds) {
                    String detailKey = "comment:detail:" + commentId;
                    String json = commentRedisTemplate.opsForValue().get(detailKey);

                    if (json != null) {
                        Map<String, Object> map = objectMapper.readValue(json, Map.class);
                        result.add(map);
                    }
                }
            }

            //Nếu Redis trống thì fallback DB
            if (result.isEmpty()) {
                List<CommentEntity> comments = commentRepository.findByPost_PostIDOrderByCreatedAtDesc(postId);
                for (CommentEntity c : comments) {
                    PostCommentDTO dto = new PostCommentDTO();
                    BeanUtils.copyProperties(c, dto);
                    dto.setUserID(c.getUser().getUserID());
                    dto.setFullName(c.getUser().getFullName());

                    // Lưu DB sang Redis
                    String json = objectMapper.writeValueAsString(dto);

                    String commentDetailKey = "comment:detail:" + c.getCommentID();
                    commentRedisTemplate.opsForValue().set(commentDetailKey, json);

                    commentRedisTemplate.opsForList().leftPush(postKey, String.valueOf(c.getCommentID()));
                    result.add(objectMapper.readValue(json, Map.class));
                }

                // Giữ tối đa 100 comment mới nhất
                commentRedisTemplate.opsForList().trim(postKey, 0, 99);
                // Cache 1 giờ
                commentRedisTemplate.expire(postKey, 1, TimeUnit.HOURS);
            }

            return ResponseEntity.ok(Map.of("comments", result));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Lỗi server"));
        }
    }
}
