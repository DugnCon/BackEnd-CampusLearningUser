package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class CallInitiateMessage {
    private Long receiverID;       // ID người nhận cuộc gọi
    private Long callID;           // ID cuộc gọi
    private String type;           // "audio" hoặc "video"
    private Long conversationID;   // ID cuộc trò chuyện
    private Long fromUserID;       // ID người gọi (optional)
}