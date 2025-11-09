package com.javaweb.service;

<<<<<<< HEAD
import com.javaweb.model.dto.PostDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
=======
import com.javaweb.model.dto.Post.PostDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c

import java.util.List;

@Service
public interface IPostService {
    ResponseEntity<Object> createPost(PostDTO postDTO, Long userId);
    void createMediaFromPost(Long postId, List<String> media);
<<<<<<< HEAD
    ResponseEntity<Object> getPostLimit(int limit);
=======
    ResponseEntity<Object> getPostLimit(int limit, Long userId);
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
    ResponseEntity<Object> getSinglePost(Long userId, Long postId);
}
