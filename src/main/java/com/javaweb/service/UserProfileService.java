package com.javaweb.service;

import com.javaweb.model.dto.Profile.ProfileInformation.EducationDTO;
import com.javaweb.model.dto.Profile.ProfileInformation.WorkExperienceDTO;
import com.javaweb.model.dto.Profile.UserProfileDetailDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserProfileService {
    ResponseEntity<Object> getUserProfile(Long userId);
    ResponseEntity<Object> updateEducation(Long userId, List<EducationDTO> educationDTO);
    ResponseEntity<Object> updateWorkExperience(Long userId, List<WorkExperienceDTO> workExperienceDTO);
    ResponseEntity<Object> getUserEmails(Long userId);
    ResponseEntity<Object> updateProfile(Long userId, UserProfileDetailDTO profileDTO);
}