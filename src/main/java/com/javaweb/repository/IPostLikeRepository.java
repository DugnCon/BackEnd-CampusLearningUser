package com.javaweb.repository;

import com.javaweb.entity.Post.PostLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IPostLikeRepository extends JpaRepository<PostLikeEntity, Long> {
    @Query("select pl from PostLikeEntity pl join fetch pl.post p join fetch pl.user u " +
            "where p.postID = :postId and u.UserID = :userId")
    boolean existsPostLike(@Param("postId") Long postId, @Param("userId") Long userId);
    @Query("select pl from PostLikeEntity pl join fetch pl.post p join fetch pl.user u " +
            "where p.postID = :postId and u.UserID = :userId")
    PostLikeEntity getPostLike(@Param("postId") Long postId, @Param("userId") Long userId);
}
