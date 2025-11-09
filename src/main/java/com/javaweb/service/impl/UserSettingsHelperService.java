package com.javaweb.service.impl;

import com.javaweb.entity.UserEntity;
import com.javaweb.entity.UserSettings;
import com.javaweb.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserSettingsHelperService {

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    // !! MỨC ĐỘ CÔ LẬP CAO NHẤT VÀ TÁCH BIỆT TRANSACTION !!
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public UserSettings findOrCreateSettings(UserEntity user) {
        // 1. Cố gắng tìm
        return userSettingsRepository.findByUser(user)
                .orElseGet(() -> {
                    // 2. Nếu không tìm thấy, tạo mới và SAVE (INSERT) trong transaction riêng
                    UserSettings newSettings = new UserSettings();
                    newSettings.setUser(user);
                    // Dòng SAVE này sẽ chạy và commit ngay lập tức nhờ REQUIRES_NEW
                    return userSettingsRepository.save(newSettings);
                });
    }
}