package com.javaweb.api.posts;

import com.javaweb.model.dto.MyUserDetail;
import com.javaweb.model.dto.PostDTO;
import com.javaweb.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostAPI {
    @Autowired
    private IPostService postService;
    // Tạo bài đăng mới
    @PostMapping
    public ResponseEntity<Object> createPost(@ModelAttribute PostDTO postDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

        return postService.createPost(postDTO, userId);
    }

    // Lấy danh sách tất cả bài đăng (có phân trang + filter)
    @GetMapping
    public ResponseEntity<Object> getAllPosts(@RequestParam(value = "limit") int limit) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        String fullName = myUserDetail.getFullName();

        return postService.getPostLimit(userId, limit, fullName);
    }

    // Lấy chi tiết 1 bài đăng
    @GetMapping("/{postId}")
    public ResponseEntity<Object> getSinglePost(@PathVariable Long postId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

        return postService.getSinglePost(userId, postId);
    }

    // Lấy danh sách bài đăng của 1 người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok().body("List of user posts");
    }

    // Cập nhật bài đăng
    @PutMapping("/{postId}")
    public ResponseEntity<Object> updatePost(
            @PathVariable Long postId,
            @RequestBody Map<String, Object> postData) {
        return ResponseEntity.ok().body("Post updated successfully");
    }

    // Xóa bài đăng
    @DeleteMapping("/{postId}")
    public ResponseEntity<Object> deletePost(@PathVariable Long postId) {
        return ResponseEntity.ok().body("Post deleted successfully");
    }

    // Like / Unlike bài đăng
    @PostMapping("/{postId}/like")
    public ResponseEntity<Object> toggleLike(@PathVariable Long postId) {
        return ResponseEntity.ok().body("Toggled like for post");
    }

    // Lấy danh sách bình luận của bài đăng
    @GetMapping("/{postId}/comments")
    public ResponseEntity<Object> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Long parentId) {
        return ResponseEntity.ok().body("List of comments for post");
    }

    // Thêm bình luận cho bài đăng
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Object> addComment(
            @PathVariable Long postId,
            @RequestBody Map<String, Object> commentData) {
        return ResponseEntity.ok().body("Comment added successfully");
    }

    // Like / Unlike bình luận
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<Object> toggleCommentLike(@PathVariable Long commentId) {
        return ResponseEntity.ok().body("Toggled like for comment");
    }

    // Xóa bình luận
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long commentId) {
        return ResponseEntity.ok().body("Comment deleted successfully");
    }

    // Chia sẻ bài đăng
    @PostMapping("/{postId}/share")
    public ResponseEntity<Object> sharePost(
            @PathVariable Long postId,
            @RequestBody Map<String, Object> shareData) {
        return ResponseEntity.ok().body("Post shared successfully");
    }
}
