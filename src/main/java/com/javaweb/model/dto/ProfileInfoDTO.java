package com.javaweb.model.dto;
import com.javaweb.entity.UserEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileInfoDTO {
    private String fullName;
    private String profileImage;
    private String email;
    private String username;

    public ProfileInfoDTO(UserEntity user) {
        this.fullName = user.getFullName();
        this.profileImage = user.getAvatar(); // Map 'avatar' sang 'profileImage'
        this.email = user.getEmail();
        this.username = user.getUsername();
    }
}