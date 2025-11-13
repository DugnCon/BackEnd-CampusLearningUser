package com.javaweb.api.profile;

import com.javaweb.entity.UserEntity;
import com.javaweb.model.dto.MyUserDetail;
import com.javaweb.model.dto.User.UserSuggestions.UserSuggestionDTO;
import com.javaweb.repository.IUserRepository;
import com.javaweb.service.IProfileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping
public class ProfileAPI {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IProfileService profileService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/auth/me")
    public ResponseEntity<?> getCurrentUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("not found"));

        UserSuggestionDTO userDTO = modelMapper.map(user, UserSuggestionDTO.class);

        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/users/profile")
    public ResponseEntity<?> getUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("not found"));

        UserSuggestionDTO userDTO = modelMapper.map(user, UserSuggestionDTO.class);

        return ResponseEntity.ok(Map.of("success", true, "data", userDTO));
    }

    @GetMapping("/posts/user/{targetUserId}")
    public ResponseEntity<?> getUserMedia(@PathVariable Long targetUserId, @RequestParam(value = "limit") int limit) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

        return profileService.getMediaUser(userId, targetUserId, limit);
    }

    @PostMapping("/posts/{postId}/bookmark")
    public ResponseEntity<?> bookmarkPost(@PathVariable Long postId) {
        return null;
    }

    @GetMapping("/posts/bookmarks")
    public ResponseEntity<?> getBookmarkedPosts() {
        return null;
    }

    @PutMapping("/users/update")
    public ResponseEntity<?> updateUserProfile(@RequestBody UpdateUserRequest request) {
        return null;
    }

    //file avatar
    @PostMapping("/settings/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("image") MultipartFile file) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        return profileService.updateAvatar(file, userId);
    }

    @GetMapping("/profile/{userId}/extended")
    public ResponseEntity<?> getExtendedProfile(@PathVariable Long userId) {
        return null;
    }

    @PutMapping("/profile/education")
    public ResponseEntity<?> updateEducation(@RequestBody EducationUpdateRequest request) {
        return null;
    }

    @PutMapping("/profile/work-experience")
    public ResponseEntity<?> updateWorkExperience(@RequestBody WorkExperienceUpdateRequest request) {
        return null;
    }

    public static class FriendRequest {
        private Long friendId;

        public Long getFriendId() { return friendId; }
        public void setFriendId(Long friendId) { this.friendId = friendId; }
    }

    public static class UpdatePostRequest {
        private String content;

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public static class UpdateUserRequest {
        private String phoneNumber;
        private String dateOfBirth;
        private String school;
        private String address;
        private String city;

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

        public String getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

        public String getSchool() { return school; }
        public void setSchool(String school) { this.school = school; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
    }

    public static class EducationUpdateRequest {
        private Object educationData;

        public Object getEducationData() { return educationData; }
        public void setEducationData(Object educationData) { this.educationData = educationData; }
    }

    public static class WorkExperienceUpdateRequest {
        private Object workExperienceData;

        public Object getWorkExperienceData() { return workExperienceData; }
        public void setWorkExperienceData(Object workExperienceData) { this.workExperienceData = workExperienceData; }
    }
}
