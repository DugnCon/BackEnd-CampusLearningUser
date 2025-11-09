package com.javaweb.api.posts;

<<<<<<< HEAD
import com.javaweb.model.dto.MyUserDetail;
import com.javaweb.model.dto.PostDTO;
import com.javaweb.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
=======
import com.fasterxml.jackson.core.JsonProcessingException;
import com.javaweb.model.dto.MyUserDetail;
import com.javaweb.model.dto.Post.PostDTO;
import com.javaweb.service.ICommentService;
import com.javaweb.service.IPostService;
import com.javaweb.service.PostCommentService;
import com.javaweb.service.PostLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostAPI {
    @Autowired
    private IPostService postService;
<<<<<<< HEAD
=======
    @Autowired
    private ICommentService commentService;
    @Autowired
    private PostLikeService postLikeService;
    @Autowired
    private PostCommentService postCommentService;
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
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
<<<<<<< HEAD
        return postService.getPostLimit(limit);
=======
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

        return postService.getPostLimit(limit, userId);
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
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
<<<<<<< HEAD
    @GetMapping("/user/{userId}")
=======
    /*@GetMapping("/user/{userId}")
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
    public ResponseEntity<Object> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok().body("List of user posts");
<<<<<<< HEAD
    }

    /**
     * Xử lý bài đăng
=======
    }*/

    /**
     * Xử lý bài đăng của bài post
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
     * **/
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

    /**
     * Xử lý like bài viết thuộc PostLikeEntity
     * **/
    // Like / Unlike bài đăng
    @PostMapping("/{postId}/like")
<<<<<<< HEAD
    public ResponseEntity<Object> toggleLike(@PathVariable Long postId) {
        return ResponseEntity.ok().body("Toggled like for post");
    }

    /**
=======
    public ResponseEntity<Object> toggleLike(@PathVariable Long postId) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

        //Tạo bài post
        return postLikeService.toggleLike(postId, userId);
    }

    /**(Đã xong)
     * Xử lý thêm bình luận cho các bài đăng CommentEntity
     * **/
    // Thêm bình luận cho bài đăng
    @MessageMapping("/comment/create")
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Object> addComment(
            @PathVariable Long postId,
            @RequestBody Map<String, Object> commentData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

        return commentService.createComment(postId, userId, commentData);
    }

    /**(Ngày mai làm)
     * Phải xử lý bằng redis khi mà load lại trang cho tải nhanh
     * Lấy hết danh sách người đăng comment trên bài post
     * **/
    // Lấy danh sách bình luận của bài đăng
    @GetMapping("/{postId}/comments")
    public ResponseEntity<Object> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Long parentId) throws JsonProcessingException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

        return postCommentService.getCommentsByPostId(postId, userId);
    }

    /**(Ngày mai làm)
     * Phải xử lý ở thêm redis về like và unlike cho comment
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
     * Xử lý like bình luận CommentLikeEntity
     * **/
    // Like / Unlike bình luận
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<Object> toggleCommentLike(@PathVariable Long commentId) {
        return ResponseEntity.ok().body("Toggled like for comment");
    }

<<<<<<< HEAD
    /**
     * Xử lý thêm bình luận cho các bài đăng CommentEntity
     * **/
    // Thêm bình luận cho bài đăng
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Object> addComment(
            @PathVariable Long postId,
            @RequestBody Map<String, Object> commentData) {
        return ResponseEntity.ok().body("Comment added successfully");
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


    // Xóa bình luận
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long commentId) {
        return ResponseEntity.ok().body("Comment deleted successfully");
    }

=======
    /**(Ngày mai làm)
     * Lấy danh sách người đã comment
     * **/
    // Xóa bình luận
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long commentId, @PathVariable Long postId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

        return commentService.deleteComment(commentId, postId);
    }

    /**(Ngày mai làm)
     * Xử lý cho xong cái share
     * **/
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
    // Chia sẻ bài đăng
    @PostMapping("/{postId}/share")
    public ResponseEntity<Object> sharePost(
            @PathVariable Long postId,
            @RequestBody Map<String, Object> shareData) {
        return ResponseEntity.ok().body("Post shared successfully");
    }
}
