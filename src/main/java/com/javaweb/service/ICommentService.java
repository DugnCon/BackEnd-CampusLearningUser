package com.javaweb.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ICommentService {
    ResponseEntity<Object> createComment(Long postId, Long userId, Map<String, Object> data);
    //PostCommentDTO getCommentById(Long postId);
    ResponseEntity<Object> deleteComment(Long commentId, Long postId);
}
