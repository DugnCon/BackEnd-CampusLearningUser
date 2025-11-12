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

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public UserSettings findOrCreateSettings(UserEntity user) {
        return userSettingsRepository.findByUser(user)
                .orElseGet(() -> {
                    UserSettings newSettings = new UserSettings();
                    newSettings.setUser(user);
                    return userSettingsRepository.save(newSettings);
                });
    }
}