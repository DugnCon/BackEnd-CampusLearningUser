package com.javaweb.repository;

import com.javaweb.entity.UserEntity;
import com.javaweb.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    // Phương thức tìm kiếm bằng UserEntity (dùng trong Service)
    Optional<UserProfile> findByUser(UserEntity user);
    // Phương thức tìm kiếm bằng Username (dùng trong Controller/Service)
    Optional<UserProfile> findByUser_Username(String username);
}