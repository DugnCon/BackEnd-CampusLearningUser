package com.javaweb.repository;

import com.javaweb.entity.Post.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface IPostRepository extends JpaRepository<PostEntity, Long> {
    @Query("select p from PostEntity p join fetch p.user u order by p.createdAt desc")
    List<PostEntity> getPostLimit(Pageable pageable);
    @Query("select p from PostEntity p join fetch p.user u where u.UserID = :userId and p.postID = :postId")
    PostEntity getSinglePost(@Param("userId") Long userId, @Param("postId") Long postId);
    @Query("select p from PostEntity p join fetch p.user u where u.UserID = :userId order by p.createdAt desc")
    List<PostEntity> getProfilePostLimit(@Param("userId") Long userId, Pageable pageable);
}