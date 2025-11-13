package com.javaweb.service.impl.ProfileAndSetting;

import com.javaweb.entity.UserEntity;
import com.javaweb.entity.UserSettings;
import com.javaweb.model.dto.*;
import com.javaweb.model.dto.Profile.CombinedSettingsResponseDTO;
import com.javaweb.model.dto.User.UserSettingsDTO;
import com.javaweb.repository.IUserRepository;
import com.javaweb.repository.UserSettingsRepository;
import com.javaweb.service.FileStorageService;
import com.javaweb.service.ISettingsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;

@Service
public class ISettingsServiceImpl implements ISettingsService {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private UserSettingsRepository userSettingsRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private UserSettingsHelperService userSettingsHelperService;
    @Autowired
    private ModelMapper modelMapper;

    private UserEntity getUserByUsername(String identifier) {
        return userRepository.findByEmail(identifier)
                .or(() -> {
                    return userRepository.findByUsernameExact(identifier);
                })
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy User: " + identifier));
    }

    private UserSettingsDTO mapEntityToDTO(UserSettings settingsEntity) {
        UserSettingsDTO settingsDTO = new UserSettingsDTO();

        PreferenceSettingsDTO prefDTO = new PreferenceSettingsDTO();
        prefDTO.setTheme(settingsEntity.getTheme());
        prefDTO.setLanguage(settingsEntity.getLanguage());
        prefDTO.setTimeZone(settingsEntity.getTimeZone());
        settingsDTO.setPreferences(prefDTO);

        NotificationSettingsDTO notifDTO = new NotificationSettingsDTO();
        notifDTO.setEmail(settingsEntity.getNotificationEmail() != null ? settingsEntity.getNotificationEmail() : true);
        notifDTO.setPush(settingsEntity.getNotificationPush() != null ? settingsEntity.getNotificationPush() : true);
        notifDTO.setInApp(settingsEntity.getNotificationInApp() != null ? settingsEntity.getNotificationInApp() : true);
        settingsDTO.setNotifications(notifDTO);

        settingsDTO.setAccessibility(new AccessibilitySettingsDTO());

        settingsDTO.setProfileVisibility(settingsEntity.getProfileVisibility());

        return settingsDTO;
    }
    @Override
    @Transactional(readOnly = true)
    public CombinedSettingsResponseDTO getUserSettingsAndProfile(String username) {
        UserEntity user = getUserByUsername(username);

        UserSettings settingsEntity = userSettingsHelperService.findOrCreateSettings(user);

        ProfileInfoDTO profileInfo = new ProfileInfoDTO();
        profileInfo.setProfileImage(user.getAvatar());
        profileInfo.setFullName(user.getFullName());
        profileInfo.setUsername(user.getUsername());
        profileInfo.setEmail(user.getEmail());

        UserSettingsDTO settingsDTO = mapEntityToDTO(settingsEntity);

        CombinedSettingsResponseDTO response = new CombinedSettingsResponseDTO();
        response.setProfileInfo(profileInfo);
        response.setSettings(settingsDTO);

        return response;
    }

    @Override
    @Transactional
    public UserSettingsDTO updateUserSettings(String username, UserSettingsDTO settingsDTO) {
        UserEntity user = getUserByUsername(username);

        UserSettings settingsEntity = userSettingsRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Settings cho User: " + username));

        PreferenceSettingsDTO prefDTO = settingsDTO.getPreferences();
        if (prefDTO != null) {
            settingsEntity.setTheme(prefDTO.getTheme());
            settingsEntity.setLanguage(prefDTO.getLanguage());
            settingsEntity.setTimeZone(prefDTO.getTimeZone());
        }

        NotificationSettingsDTO notifDTO = settingsDTO.getNotifications();
        if (notifDTO != null) {
            settingsEntity.setNotificationEmail(notifDTO.getEmail());
            settingsEntity.setNotificationPush(notifDTO.getPush());
            settingsEntity.setNotificationInApp(notifDTO.getInApp());
        }

        settingsEntity.setProfileVisibility(settingsDTO.getProfileVisibility());
        UserSettings updatedSettings = userSettingsRepository.save(settingsEntity);

        return mapEntityToDTO(updatedSettings);
    }

    @Override
    @Transactional
    public String updateProfilePicture(String username, MultipartFile file) {
        UserEntity user = getUserByUsername(username);
        String fileUrl;

        try {
            fileUrl = fileStorageService.saveFile(file);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file ảnh đại diện", e);
        }

        user.setAvatar(fileUrl);
        userRepository.save(user);

        return fileUrl;
    }

    @Override
    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        System.out.println("DEBUG: Change password requested for user: " + username);
    }

    @Override
    @Transactional
    public void deleteAccount(String username, String password, String reason) {
        System.out.println("DEBUG: Delete account requested for user: " + username);
    }
}