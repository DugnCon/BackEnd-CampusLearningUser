package com.javaweb.service.impl;

import com.javaweb.entity.UserEntity;
import com.javaweb.entity.UserProfile;
import com.javaweb.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileHelperService { // 👈 Class mới

    @Autowired
    private UserProfileRepository userProfileRepository;

    /**
     * Phương thức Tìm hoặc Tạo Profile.
     * Sử dụng REQUIRES_NEW để đảm bảo:
     * 1. Có thể INSERT (vì nó không phải readOnly).
     * 2. Ngăn race condition (giống như logic Settings).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public UserProfile findOrCreateProfile(UserEntity user) {
        // Cố gắng tìm Profile dựa trên UserEntity
        return userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    // Nếu không tìm thấy, tạo mới và SAVE (INSERT) trong Transaction riêng này.
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    return userProfileRepository.save(newProfile);
                });
    }
}