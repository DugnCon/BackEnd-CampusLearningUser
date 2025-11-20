package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class CallInitiateMessage {
    private Long receiverID;
    private Long callID;
    private String type;
    private Long conversationID;
    private Long fromUserID;
}