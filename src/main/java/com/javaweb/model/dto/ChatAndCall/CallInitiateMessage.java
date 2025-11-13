package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class CallInitiateMessage {
    private Long conversationID;
    private Long callID;
    private String type; // "audio" or "video"
    private String initiatorId;
    private String initiatorName;
    private String initiatorPicture;
}