package com.javaweb.api.Settings;

import com.javaweb.model.dto.UserProfileDTO;
import com.javaweb.service.UserProfileService; // Dùng Service mới
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/profile") // Endpoint mới
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @GetMapping
    public ResponseEntity<UserProfileDTO> getProfile(Principal principal) {
        String username = principal.getName();
        UserProfileDTO dto = userProfileService.getProfile(username);
        return ResponseEntity.ok(dto);
    }

    @PutMapping
    public ResponseEntity<UserProfileDTO> updateProfile(
            Principal principal,
            @RequestBody UserProfileDTO profileDTO) {
        String username = principal.getName();
        UserProfileDTO updatedDto = userProfileService.updateProfile(username, profileDTO);
        return ResponseEntity.ok(updatedDto);
    }
}