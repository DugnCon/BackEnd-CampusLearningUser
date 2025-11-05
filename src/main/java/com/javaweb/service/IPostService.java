package com.javaweb.service;

import com.javaweb.model.dto.Post.PostDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IPostService {
    ResponseEntity<Object> createPost(PostDTO postDTO, Long userId);
    void createMediaFromPost(Long postId, List<String> media);
    ResponseEntity<Object> getPostLimit(int limit, Long userId);
    ResponseEntity<Object> getSinglePost(Long userId, Long postId);
}
