package com.javaweb.repository;

import com.javaweb.entity.Post.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ICommentRepository extends JpaRepository<CommentEntity, Long> {

    //Lấy các comment được tạo mới hoặc update sau lastSyncTime
    List<CommentEntity> findByCreatedAtAfterOrUpdatedAtAfter(LocalDateTime createdAfter, LocalDateTime updatedAfter);

    //Lấy các comment bị đánh dấu deleted sau lastSyncTime
    List<CommentEntity> findByIsDeletedTrueAndUpdatedAtAfter(LocalDateTime updatedAfter);

    List<CommentEntity> findByPost_PostIDAndIsDeletedFalse(Long postId);

    List<CommentEntity> findByPost_PostIDOrderByCreatedAtDesc(Long postId);

    @Query("SELECT c FROM CommentEntity c WHERE c.post.postID = :postId ORDER BY c.createdAt DESC")
    List<CommentEntity> findCommentsByPostId(@Param("postId") Long postId);
}
