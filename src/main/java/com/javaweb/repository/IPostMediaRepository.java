package com.javaweb.repository;

import com.javaweb.entity.Post.PostMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPostMediaRepository extends JpaRepository<PostMediaEntity, Long> {
}
