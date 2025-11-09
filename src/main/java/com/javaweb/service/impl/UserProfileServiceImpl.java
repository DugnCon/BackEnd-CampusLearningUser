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

    // Helper Service
    @Autowired
    private UserProfileHelperService userProfileHelperService;

    // --- Helper để tìm User ---
    // Trong file SettingsServiceImpl.java

// ... (Các imports và khai báo khác)

    private UserEntity getUserByUsername(String identifier) {
        // 1. Thử tìm bằng Email (Nếu identifier có thể là email)
        // Tên phương thức trong IUserRepository là findByEmail
        return userRepository.findByEmail(identifier)

                // 2. Nếu không tìm thấy bằng email, thử tìm bằng Username
                .or(() -> {
                    // Tên phương thức trong IUserRepository là findByUsernameExact
                    return userRepository.findByUsernameExact(identifier);
                })

                // 3. Nếu vẫn không tìm thấy, ném ngoại lệ
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy User: " + identifier));
    }

    // --- Helper Map Entity -> DTO ---
    private UserProfileDTO mapEntityToDTO(UserProfile profileEntity, UserEntity userEntity) {
        UserProfileDTO dto = new UserProfileDTO();

        // I. Map từ UserEntity (Bảng users)
        dto.setFullName(userEntity.getFullName());
        dto.setEmail(userEntity.getEmail());
        dto.setUsername(userEntity.getUsername());

        dto.setBio(userEntity.getBio());
        dto.setSchool(userEntity.getSchool());
        dto.setPhoneNumber(userEntity.getPhoneNumber());
        dto.setAddress(userEntity.getAddress());
        dto.setCity(userEntity.getCity());
        dto.setCountry(userEntity.getCountry());

        // SỬA LỖI: Dùng Getter/Setter kiểu String trực tiếp từ Entity
        dto.setDateOfBirth(userEntity.getDateOfBirth());

        // II. Map từ UserProfile (Bảng userprofiles - Xử lý JSON)
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

        // III. Map các trường String từ UserProfile
        dto.setPreferredLanguage(profileEntity.getPreferredLanguage());
        dto.setTimeZone(profileEntity.getTimeZone());

        return dto;
    }

    // --- TRIỂN KHAI GET PROFILE ---
    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getProfile(String username) {
        UserEntity user = getUserByUsername(username);
        UserProfile profileEntity = userProfileHelperService.findOrCreateProfile(user);
        return mapEntityToDTO(profileEntity, user);
    }

    // --- TRIỂN KHAI UPDATE PROFILE ---
    // File: service/impl/UserProfileServiceImpl.java

    // File: service/impl/UserProfileServiceImpl.java

    @Override
    @Transactional
    public UserProfileDTO updateProfile(String username, UserProfileDTO profileDTO) {
        UserEntity user = getUserByUsername(username);
        // Sử dụng findOrCreateProfile để đảm bảo profileEntity luôn tồn tại
        UserProfile profileEntity = userProfileHelperService.findOrCreateProfile(user);

        // 1. Cập nhật UserEntity (Lưu vào bảng users)

        // FIX: Chỉ cập nhật nếu giá trị từ DTO KHÔNG NULL VÀ KHÔNG RỖNG để bảo vệ NOT NULL fields
        if (profileDTO.getFullName() != null && !profileDTO.getFullName().trim().isEmpty()) {
            user.setFullName(profileDTO.getFullName());
        }

        // Áp dụng kiểm tra null cho các trường khác
        if (profileDTO.getBio() != null) {
            user.setBio(profileDTO.getBio());
        }
        if (profileDTO.getSchool() != null) {
            user.setSchool(profileDTO.getSchool());
        }

        // Các trường địa chỉ và liên hệ
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

        // DateOfBirth - Chỉ cập nhật nếu có giá trị được gửi
        if (profileDTO.getDateOfBirth() != null) {
            user.setDateOfBirth(profileDTO.getDateOfBirth());
        }

        userRepository.save(user); // LƯU TẤT CẢ THAY ĐỔI CỦA USERENTITY

        // 2. Cập nhật UserProfile Entity (Lưu vào bảng userprofiles)

        // Cập nhật các trường String (có thể NULL)
        if (profileDTO.getPreferredLanguage() != null) {
            profileEntity.setPreferredLanguage(profileDTO.getPreferredLanguage());
        }
        if (profileDTO.getTimeZone() != null) {
            profileEntity.setTimeZone(profileDTO.getTimeZone());
        }

        // Cập nhật các trường String đặc biệt
        


        // Cập nhật các trường List/Map (JSON)
        // Các trường này có thể được gán trực tiếp vì chúng được lưu dưới dạng JSON String
        // và không có ràng buộc NOT NULL trong Entity
        try {
            profileEntity.setEducationList(profileDTO.getEducation());
            profileEntity.setWorkExperienceList(profileDTO.getWorkExperience());
            profileEntity.setSocialLinksMap(profileDTO.getSocialLinks());
            profileEntity.setSkillsList(profileDTO.getSkills());
            profileEntity.setInterestsList(profileDTO.getInterests());
            profileEntity.setAchievementsList(profileDTO.getAchievements());
            profileEntity.setNotificationPreferencesMap(profileDTO.getNotificationPreferences());
        } catch (JsonProcessingException e) {
            // Ném ngoại lệ Runtime khi lỗi JSON, rollback transaction
            throw new RuntimeException("Lỗi xử lý JSON khi GHI UserProfile", e);
        }

        UserProfile updatedProfile = userProfileRepository.save(profileEntity);

        // 3. Trả về DTO đã cập nhật
        return mapEntityToDTO(updatedProfile, user);
    }
}