package com.javaweb.service.impl;

import com.javaweb.entity.UserEntity;
import com.javaweb.entity.UserSettings;
import com.javaweb.model.dto.*;
import com.javaweb.repository.IUserRepository;
import com.javaweb.repository.UserSettingsRepository;
import com.javaweb.service.FileStorageService;
import com.javaweb.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;

@Service
public class SettingsServiceImpl implements SettingsService {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private UserSettingsRepository userSettingsRepository;
    @Autowired
    private FileStorageService fileStorageService;
    // Đã thêm Helper Service để xử lý tạo Settings trong Transaction riêng
    @Autowired
    private UserSettingsHelperService userSettingsHelperService;

    // --- Các hàm Helper ---

    private UserEntity getUserByUsername(String identifier) {
        return userRepository.findUserEntityByEmail(identifier)
                .or(() -> {
                    return userRepository.findUserEntityByUsername(identifier);
                })
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy User: " + identifier));
    }

    private UserSettingsDTO mapEntityToDTO(UserSettings settingsEntity) {
        UserSettingsDTO settingsDTO = new UserSettingsDTO();

        // Map Preferences
        PreferenceSettingsDTO prefDTO = new PreferenceSettingsDTO();
        prefDTO.setTheme(settingsEntity.getTheme());
        prefDTO.setLanguage(settingsEntity.getLanguage());
        prefDTO.setTimeZone(settingsEntity.getTimeZone());
        settingsDTO.setPreferences(prefDTO);

        // Map Notifications (Dùng giá trị mặc định của DB nếu là null)
        NotificationSettingsDTO notifDTO = new NotificationSettingsDTO();
        notifDTO.setEmail(settingsEntity.getNotificationEmail() != null ? settingsEntity.getNotificationEmail() : true);
        notifDTO.setPush(settingsEntity.getNotificationPush() != null ? settingsEntity.getNotificationPush() : true);
        notifDTO.setInApp(settingsEntity.getNotificationInApp() != null ? settingsEntity.getNotificationInApp() : true);
        settingsDTO.setNotifications(notifDTO);

        // Map Accessibility (!! BỊ BỎ QUA vì DB thiếu cột !!)
        settingsDTO.setAccessibility(new AccessibilitySettingsDTO());

        settingsDTO.setProfileVisibility(settingsEntity.getProfileVisibility());

        return settingsDTO;
    }

    // --- TRIỂN KHAI 5 PHƯƠNG THỨC TỪ INTERFACE SETTINGSSERVICE ---

    @Override
    @Transactional(readOnly = true)
    public CombinedSettingsResponseDTO getUserSettingsAndProfile(String username) {
        UserEntity user = getUserByUsername(username);

        // GỌI HELPER: Tìm hoặc Tạo Settings trong transaction riêng
        UserSettings settingsEntity = userSettingsHelperService.findOrCreateSettings(user);

        // 3. Mapping và trả về
        ProfileInfoDTO profileInfo = new ProfileInfoDTO(user);
        UserSettingsDTO settingsDTO = mapEntityToDTO(settingsEntity);

        CombinedSettingsResponseDTO response = new CombinedSettingsResponseDTO();
        response.setProfileInfo(profileInfo);
        response.setSettings(settingsDTO);

        return response;
    }

    @Override
    @Transactional
    public UserSettingsDTO updateUserSettings(String username, UserSettingsDTO settingsDTO) {
        UserEntity user = getUserByUsername(username); // Lấy user

        // SỬA LỖI: Tìm Settings trực tiếp. Settings phải tồn tại nếu user đã truy cập trang Settings
        UserSettings settingsEntity = userSettingsRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Settings cho User: " + username));

        // Map Preferences
        PreferenceSettingsDTO prefDTO = settingsDTO.getPreferences();
        if (prefDTO != null) {
            settingsEntity.setTheme(prefDTO.getTheme());
            settingsEntity.setLanguage(prefDTO.getLanguage());
            settingsEntity.setTimeZone(prefDTO.getTimeZone());
        }

        // Map Notifications
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

    /**
     * STUB: Phương thức Change Password (Cần implement logic sau)
     */
    @Override
    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        System.out.println("DEBUG: Change password requested for user: " + username);
    }

    /**
     * STUB: Phương thức Delete Account (Cần implement logic sau)
     */
    @Override
    @Transactional
    public void deleteAccount(String username, String password, String reason) {
        System.out.println("DEBUG: Delete account requested for user: " + username);
    }
}