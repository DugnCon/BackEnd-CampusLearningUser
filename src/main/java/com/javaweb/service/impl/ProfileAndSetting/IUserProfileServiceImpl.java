package com.javaweb.service.impl.ProfileAndSetting;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.entity.UserEntity;
import com.javaweb.entity.UserProfileEntity;
import com.javaweb.model.dto.Profile.EmailDTO;
import com.javaweb.model.dto.Profile.ProfileInformation.EducationDTO;
import com.javaweb.model.dto.Profile.ProfileInformation.WorkExperienceDTO;
import com.javaweb.model.dto.Profile.UserProfileDetailDTO;
import com.javaweb.repository.IUserRepository;
import com.javaweb.repository.UserProfileRepository;
import com.javaweb.service.IUserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class IUserProfileServiceImpl implements IUserProfileService {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserProfileHelperService userProfileHelperService;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ResponseEntity<Object> getUserProfile(Long userId) {
        Logger log = null;
        try {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

            UserProfileEntity userProfile = userProfileRepository.findByUser(user).orElse(null);

            UserProfileDetailDTO profileDTO = convertToDetailDTO(user, userProfile);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "profile", profileDTO
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Không thể lấy thông tin hồ sơ: " + e.getMessage()
            ));
        }
    }
    
    @Override
    public ResponseEntity<Object> getUserEmails(Long userId) {
        try {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

            List<EmailDTO> emailList = new ArrayList<>();

            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                emailList.add(new EmailDTO(
                        user.getEmail(),
                        user.isEmailVerified(),
                        true
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "emails", emailList
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Không thể lấy danh sách email: " + e.getMessage()
            ));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Object> updateProfile(Long userId, UserProfileDetailDTO profileDTO) {
        try {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

            UserProfileEntity userProfile = userProfileRepository.findByUser(user)
                    .orElse(new UserProfileEntity());
            userProfile.setUser(user);

            updateUserEntity(user, profileDTO);
            updateUserProfileEntity(userProfile, profileDTO);

            UserEntity savedUser = userRepository.save(user);
            UserProfileEntity savedProfile = userProfileRepository.save(userProfile);

            UserProfileDetailDTO responseDTO = convertToDetailDTO(savedUser, savedProfile);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật hồ sơ thành công",
                    "profile", responseDTO
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Không thể cập nhật hồ sơ: " + e.getMessage()
            ));
        }
    }

    private void updateUserEntity(UserEntity user, UserProfileDetailDTO profileDTO) {
        if (profileDTO.getFullName() != null) {
            user.setFullName(profileDTO.getFullName());
        }
        if (profileDTO.getEmail() != null) {
            user.setEmail(profileDTO.getEmail());
        }
        if (profileDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(profileDTO.getPhoneNumber());
        }
        if (profileDTO.getDateOfBirth() != null) {
            user.setDateOfBirth(profileDTO.getDateOfBirth());
        }
        if (profileDTO.getSchool() != null) {
            user.setSchool(profileDTO.getSchool());
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
        if (profileDTO.getBio() != null) {
            user.setBio(profileDTO.getBio());
        }
    }

    private void updateUserProfileEntity(UserProfileEntity userProfile, UserProfileDetailDTO profileDTO) {
        if (profileDTO.getEducation() != null) {
            userProfile.setEducation(profileDTO.getEducation());
        }
        if (profileDTO.getWorkExperience() != null) {
            userProfile.setWorkExperience(profileDTO.getWorkExperience());
        }
        if (profileDTO.getSkills() != null) {
            userProfile.setSkills(profileDTO.getSkills());
        }
        if (profileDTO.getInterests() != null) {
            userProfile.setInterests(profileDTO.getInterests());
        }
        if (profileDTO.getSocialLinks() != null) {
            userProfile.setSocialLinks(profileDTO.getSocialLinks());
        }
        if (profileDTO.getPreferredLanguage() != null) {
            userProfile.setPreferredLanguage(profileDTO.getPreferredLanguage());
        }
        if (profileDTO.getTimeZone() != null) {
            userProfile.setTimeZone(profileDTO.getTimeZone());
        }
        if (profileDTO.getNotificationPreferences() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                userProfile.setNotificationPreferencesJson(objectMapper.writeValueAsString(profileDTO.getNotificationPreferences()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private UserProfileDetailDTO convertToDetailDTO(UserEntity user, UserProfileEntity userProfile) {
        UserProfileDetailDTO dto = new UserProfileDetailDTO();

        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setSchool(user.getSchool());
        dto.setAddress(user.getAddress());
        dto.setCity(user.getCity());
        dto.setCountry(user.getCountry());
        dto.setBio(user.getBio());

        if (userProfile != null) {
            dto.setEducation(userProfile.getEducation());
            dto.setWorkExperience(userProfile.getWorkExperience());
            dto.setSkills(userProfile.getSkills());
            dto.setInterests(userProfile.getInterests());
            dto.setSocialLinks(userProfile.getSocialLinks());
            dto.setPreferredLanguage(userProfile.getPreferredLanguage());
            dto.setTimeZone(userProfile.getTimeZone());

            if (userProfile.getNotificationPreferencesJson() != null) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, Boolean> notificationPrefs = objectMapper.readValue(
                            userProfile.getNotificationPreferencesJson(),
                            new TypeReference<Map<String, Boolean>>() {}
                    );
                    dto.setNotificationPreferences(notificationPrefs);
                } catch (Exception e) {
                    Logger log = null;
                    log.warn("Failed to parse notification preferences: {}", e.getMessage());
                }
            }
        }

        return dto;
    }

    @Override
    @Transactional
    public ResponseEntity<Object> updateEducation(Long userId, List<EducationDTO> educationDTO) {
        try {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

            UserProfileEntity userProfile = userProfileRepository.findByUser(user)
                    .orElse(new UserProfileEntity());
            userProfile.setUser(user);

            userProfile.setEducation(educationDTO);
            userProfileRepository.save(userProfile);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Thông tin học vấn đã được cập nhật!"
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Không thể cập nhật thông tin học vấn: " + e.getMessage()
            ));
        }
    }
    @Override
    @Transactional
    public ResponseEntity<Object> updateWorkExperience(Long userId, List<WorkExperienceDTO> workExperienceDTO) {
        try {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

            UserProfileEntity userProfile = userProfileRepository.findByUser(user)
                    .orElse(new UserProfileEntity());
            userProfile.setUser(user);

            userProfile.setWorkExperience(workExperienceDTO);
            userProfileRepository.save(userProfile);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Thông tin kinh nghiệm làm việc đã được cập nhật!"
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Không thể cập nhật thông tin kinh nghiệm làm việc: " + e.getMessage()
            ));
        }
    }
}