package com.javaweb.repository;

import com.javaweb.entity.UserEntity;
import com.javaweb.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Long> {
    // Phương thức tìm kiếm bằng UserEntity (dùng trong Service)
    Optional<UserProfileEntity> findByUser(UserEntity user);
    // Phương thức tìm kiếm bằng Username (dùng trong Controller/Service)
    Optional<UserProfileEntity> findByUser_Username(String username);
}