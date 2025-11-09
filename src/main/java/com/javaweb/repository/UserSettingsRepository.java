package com.javaweb.repository;

import com.javaweb.entity.UserEntity;
import com.javaweb.entity.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {

    // Phương thức đã dùng trước đó, giữ lại
    Optional<UserSettings> findByUser_Username(String username);

    // PHƯƠNG THỨC MỚI CẦN THIẾT CHO HELPER SERVICE
    Optional<UserSettings> findByUser(UserEntity user);
}