package com.javaweb.service;

import com.javaweb.model.dto.UserProfileDTO;

public interface UserProfileService {
    UserProfileDTO getProfile(String username);
    UserProfileDTO updateProfile(String username, UserProfileDTO profileDTO);
}