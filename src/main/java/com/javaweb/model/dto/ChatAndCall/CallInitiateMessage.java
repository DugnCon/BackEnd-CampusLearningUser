package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class CallInitiateMessage {
    private String callID;
    private String receiverID;
    private String type;           // "audio", "video"
    private String conversationID; // Giữ cho compatibility

    // Constructors
    public CallInitiateMessage() {}

    public CallInitiateMessage(String callID, String receiverID, String type) {
        this.callID = callID;
        this.receiverID = receiverID;
        this.type = type;
    }
}