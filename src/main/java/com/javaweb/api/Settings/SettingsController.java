package com.javaweb.api.Settings;

import com.javaweb.model.dto.CombinedSettingsResponseDTO;
import com.javaweb.model.dto.UserSettingsDTO;
import com.javaweb.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Dùng Spring Security
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/settings") // Endpoint chung cho settings
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @GetMapping
    public ResponseEntity<CombinedSettingsResponseDTO> getSettings(Principal principal) {
        String username = principal.getName();
        CombinedSettingsResponseDTO data = settingsService.getUserSettingsAndProfile(username);
        return ResponseEntity.ok(data);
    }

    @PutMapping
    public ResponseEntity<UserSettingsDTO> updateSettings(Principal principal, @RequestBody UserSettingsDTO settingsDTO) {
        String username = principal.getName();
        UserSettingsDTO updatedSettings = settingsService.updateUserSettings(username, settingsDTO);
        return ResponseEntity.ok(updatedSettings);
    }

}