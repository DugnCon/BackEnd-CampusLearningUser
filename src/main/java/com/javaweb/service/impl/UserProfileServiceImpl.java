package com.javaweb.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.javaweb.entity.UserEntity;
import com.javaweb.entity.UserProfile;
import com.javaweb.model.dto.UserProfileDTO;
import com.javaweb.repository.IUserRepository;
import com.javaweb.repository.UserProfileRepository;
import com.javaweb.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserProfileHelperService userProfileHelperService;


    private UserEntity getUserByUsername(String identifier) {
        return userRepository.findByEmail(identifier)
                .or(() -> {
                    return userRepository.findByUsernameExact(identifier);
                })
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy User: " + identifier));
    }
    private UserProfileDTO mapEntityToDTO(UserProfile profileEntity, UserEntity userEntity) {
        UserProfileDTO dto = new UserProfileDTO();

        dto.setFullName(userEntity.getFullName());
        dto.setEmail(userEntity.getEmail());
        dto.setUsername(userEntity.getUsername());
        //dto.setImage(userEntity.getImage());
        //dto.setAvatar(userEntity.getAvatar());
        dto.setBio(userEntity.getBio());
        dto.setSchool(userEntity.getSchool());
        dto.setPhoneNumber(userEntity.getPhoneNumber());
        dto.setAddress(userEntity.getAddress());
        dto.setCity(userEntity.getCity());
        dto.setCountry(userEntity.getCountry());
        try {
            dto.setEducation(profileEntity.getEducationList());
            dto.setWorkExperience(profileEntity.getWorkExperienceList());
            dto.setSocialLinks(profileEntity.getSocialLinksMap());
            dto.setSkills(profileEntity.getSkillsList());
            dto.setInterests(profileEntity.getInterestsList());
            dto.setAchievements(profileEntity.getAchievementsList());
            dto.setNotificationPreferences(profileEntity.getNotificationPreferencesMap());
        } catch (IOException e) {
            System.err.println("Lỗi chuyển đổi JSON khi đọc UserProfile: " + e.getMessage());
        }
        dto.setPreferredLanguage(profileEntity.getPreferredLanguage());
        dto.setTimeZone(profileEntity.getTimeZone());

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getProfile(String username) {
        UserEntity user = getUserByUsername(username);
        UserProfile profileEntity = userProfileHelperService.findOrCreateProfile(user);
        return mapEntityToDTO(profileEntity, user);
    }

    @Override
    @Transactional
    public UserProfileDTO updateProfile(String username, UserProfileDTO profileDTO) {
        UserEntity user = getUserByUsername(username);

        UserProfile profileEntity = userProfileHelperService.findOrCreateProfile(user);

        if (profileDTO.getFullName() != null && !profileDTO.getFullName().trim().isEmpty()) {
            user.setFullName(profileDTO.getFullName());
        }

        if (profileDTO.getBio() != null) {
            user.setBio(profileDTO.getBio());
        }
        if (profileDTO.getSchool() != null) {
            user.setSchool(profileDTO.getSchool());
        }

        if (profileDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(profileDTO.getPhoneNumber());
        }
        if (profileDTO.getAddress() != null) {
            user.setAddress(profileDTO.getAddress());
        }
        if (profileDTO.getCity() != null) {
            user.setCity(profileDTO.getCity());
        }
        if (profileDTO.getCountry() != null) {
            user.setCountry(profileDTO.getCountry());
        }

        if (profileDTO.getDateOfBirth() != null) {
            user.setDateOfBirth(profileDTO.getDateOfBirth());
        }

        userRepository.save(user);

        if (profileDTO.getPreferredLanguage() != null) {
            profileEntity.setPreferredLanguage(profileDTO.getPreferredLanguage());
        }
        if (profileDTO.getTimeZone() != null) {
            profileEntity.setTimeZone(profileDTO.getTimeZone());
        }

        try {
            profileEntity.setEducationList(profileDTO.getEducation());
            profileEntity.setWorkExperienceList(profileDTO.getWorkExperience());
            profileEntity.setSocialLinksMap(profileDTO.getSocialLinks());
            profileEntity.setSkillsList(profileDTO.getSkills());
            profileEntity.setInterestsList(profileDTO.getInterests());
            profileEntity.setAchievementsList(profileDTO.getAchievements());
            profileEntity.setNotificationPreferencesMap(profileDTO.getNotificationPreferences());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi xử lý JSON khi GHI UserProfile", e);
        }

        UserProfile updatedProfile = userProfileRepository.save(profileEntity);

        return mapEntityToDTO(updatedProfile, user);
    }
}