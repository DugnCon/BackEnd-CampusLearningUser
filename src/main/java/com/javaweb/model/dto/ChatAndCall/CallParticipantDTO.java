package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CallParticipantDTO {
    private Long userID;
    private String username;
    private String fullName;
    private String profilePicture;
    private String status; // "calling", "joined", "rejected", "left"
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
}
