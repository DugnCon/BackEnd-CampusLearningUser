package com.javaweb.service;

import com.javaweb.entity.Post.CommentEntity;
import com.javaweb.model.dto.PostCommentDTO;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ICommentService {
    ResponseEntity<Object> createComment(Long postId, Long userId, Map<String, Object> data);
    //PostCommentDTO getCommentById(Long postId);
    ResponseEntity<Object> deleteComment(Long commentId, Long postId);
}
