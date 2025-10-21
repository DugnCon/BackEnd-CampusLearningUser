package com.javaweb.api.posts;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostAPI {

    // Tạo bài đăng mới
    @PostMapping
    public ResponseEntity<Object> createPost(@RequestBody Map<String, Object> postData) {
        return ResponseEntity.ok().body("Post created successfully");
    }

    // Lấy danh sách tất cả bài đăng (có phân trang + filter)
    @GetMapping
    public ResponseEntity<Object> getAllPosts(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok().body("List of posts");
    }

    // Lấy chi tiết 1 bài đăng
    @GetMapping("/{postId}")
    public ResponseEntity<Object> getSinglePost(@PathVariable Long postId) {
        return ResponseEntity.ok().body("Single post details");
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
