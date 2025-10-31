package com.javaweb.service.impl.PostService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.model.dto.PostCommentDTO;
import com.javaweb.repository.ICommentRepository;
import com.javaweb.service.SolvingByRabbitMQ.Comment.CommentProducer;
import com.javaweb.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class CommentServiceImpl implements ICommentService {
    @Autowired
    private CommentProducer commentProducer;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ICommentRepository commentRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    @Qualifier("commentRedisTemplate")
    private RedisTemplate<String, String> commentRedisTemplate;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> createComment(Long postId, Long userId, Map<String, Object> data) {
        try {
            commentProducer.sendUpLoadTask(postId, userId, data.get("content").toString());
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            throw new RuntimeException(e + " can not create a comment for post");
        }
    }

    private PostCommentDTO getCommentById(Long commentId) {
        String sql = """
        SELECT 
            c.CommentID,
            c.UserID,
            c.PostID,
            u.FullName,
            c.Content,
            c.CreatedAt,
            c.LikesCount,
            0 AS isLiked
        FROM comments c
        JOIN users u ON c.UserID = u.UserID
        WHERE c.CommentID = :commentId
        """;

        try {
            return (PostCommentDTO) entityManager.createNativeQuery(sql, PostCommentDTO.class)
                    .setParameter("commentId", commentId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            throw new RuntimeException("Found multiple comments with ID: " + commentId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> deleteComment(Long commentId, Long postId) {
        try {
            //Xóa khỏi DB
            commentRepository.deleteById(commentId);

            //Xóa cache bất đồng bộ để không block request
            CompletableFuture.runAsync(() -> evictCommentCache(postId, commentId));

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Xóa thành công bình luận"
            ));

        } catch (Exception e) {
            throw new RuntimeException("Không thể xóa bình luận: " + e.getMessage(), e);
        }
    }

    private void evictCommentCache(Long postId, Long commentId) {
        try {
            String postKey = "post:" + postId + ":comments";
            String commentDetailKey = "comment:detail:" + commentId;

            //Xóa key chi tiết comment
            commentRedisTemplate.delete(commentDetailKey);

            //Xóa commentId khỏi danh sách comment của post
            //remove(key,count,value) => count là số phần tử cần xóa
            commentRedisTemplate.opsForList().remove(postKey, 0, String.valueOf(commentId));

            System.out.println("Đã xóa cache cho commentId " + commentId + " của postId " + postId);
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa cache comment: " + e.getMessage());
        }
    }

}
