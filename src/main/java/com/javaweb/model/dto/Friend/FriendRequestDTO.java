package com.javaweb.model.dto.Friend;

import lombok.Data;

@Data
public class FriendRequestDTO {
    private String friendshipId;
    private String addresseeId;
    private String requesterId;
    private String timestamp;
}
