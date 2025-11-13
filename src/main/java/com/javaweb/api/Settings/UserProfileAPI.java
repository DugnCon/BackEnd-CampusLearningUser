package com.javaweb.api.Settings;

import com.javaweb.model.dto.MyUserDetail;
import com.javaweb.model.dto.Profile.ProfileInformation.EducationDTO;
import com.javaweb.model.dto.Profile.ProfileInformation.WorkExperienceDTO;
import com.javaweb.model.dto.Profile.UserProfileDetailDTO;
import com.javaweb.service.UserProfileService; // Dùng Service mới
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping
public class UserProfileAPI {

    @Autowired
    private UserProfileService userProfileService;

    /*@GetMapping("/users/profile")
    public ResponseEntity<Object> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        return userProfileService.getUserProfile(userId);
    }*/

    @PutMapping("/users/profile")
    public ResponseEntity<Object> updateProfile(
            @RequestBody UserProfileDetailDTO profileDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        return userProfileService.updateProfile(userId, profileDTO);
    }


    @GetMapping("/users/emails")
    public ResponseEntity<Object> getEmails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        return userProfileService.getUserEmails(userId);
    }

    @PutMapping("/users/education")
    public ResponseEntity<Object> updateEducation(
            @RequestBody List<EducationDTO> education) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        return userProfileService.updateEducation(userId, education);
    }

    @PutMapping("/users/work-experience")
    public ResponseEntity<Object> updateWorkExperience(
            @RequestBody List<WorkExperienceDTO> workExperience) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        return userProfileService.updateWorkExperience(userId, workExperience);
    }
}