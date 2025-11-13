package com.javaweb.api.Settings;

import com.javaweb.model.dto.Profile.CombinedSettingsResponseDTO;
import com.javaweb.model.dto.MyUserDetail;
import com.javaweb.model.dto.User.UserSettingsDTO;
import com.javaweb.service.ISettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Dùng Spring Security
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/settings") // Endpoint chung cho settings
public class SettingsAPI {

    @Autowired
    private ISettingsService ISettingsService;

    @GetMapping
    public ResponseEntity<CombinedSettingsResponseDTO> getSettings() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        String email = myUserDetail.getUsername();
        CombinedSettingsResponseDTO data = ISettingsService.getUserSettingsAndProfile(email);
        return ResponseEntity.ok(data);
    }

    @PutMapping
    public ResponseEntity<UserSettingsDTO> updateSettings(Principal principal, @RequestBody UserSettingsDTO settingsDTO) {
        String username = principal.getName();
        UserSettingsDTO updatedSettings = ISettingsService.updateUserSettings(username, settingsDTO);
        return ResponseEntity.ok(updatedSettings);
    }

}