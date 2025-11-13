package com.javaweb.service.impl.ProfileAndSetting;

import com.javaweb.entity.UserEntity;
import com.javaweb.entity.UserProfileEntity;
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

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public UserProfileEntity findOrCreateProfile(UserEntity user) {
        return userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfileEntity newProfile = new UserProfileEntity();
                    newProfile.setUser(user);
                    return userProfileRepository.save(newProfile);
                });
    }
}