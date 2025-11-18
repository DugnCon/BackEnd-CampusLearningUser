package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class CallInitiateMessage {
    private Long callID;
    private Long receiverID;
    private String type;           // "audio", "video"
    private Long conversationID; // Giữ cho compatibility

    // Constructors
    public CallInitiateMessage() {}
}