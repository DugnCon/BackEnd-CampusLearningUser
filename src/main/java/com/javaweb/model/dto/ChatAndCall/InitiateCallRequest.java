package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class InitiateCallRequest {
    private Long conversationID;
    private String receiverID;     // Thêm field này
    private String type;           // "audio", "video"

    // Constructors
    public InitiateCallRequest() {}

    public InitiateCallRequest(Long conversationID, String receiverID, String type) {
        this.conversationID = conversationID;
        this.receiverID = receiverID;
        this.type = type;
    }
}