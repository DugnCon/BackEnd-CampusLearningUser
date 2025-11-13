package com.javaweb.service;

import com.javaweb.model.dto.Profile.CombinedSettingsResponseDTO;
import com.javaweb.model.dto.User.UserSettingsDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ISettingsService {

    CombinedSettingsResponseDTO getUserSettingsAndProfile(String username);

    UserSettingsDTO updateUserSettings(String username, UserSettingsDTO settingsDTO);

    String updateProfilePicture(String username, MultipartFile file);

    void changePassword(String username, String currentPassword, String newPassword);

    void deleteAccount(String username, String password, String reason);
}