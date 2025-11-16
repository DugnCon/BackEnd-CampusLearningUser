package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class CallAnswerMessage {
    private String callID;
    private String initiatorID;    // Người khởi tạo cuộc gọi
    private boolean accepted;      // true = nhận cuộc gọi, false = từ chối

    // Constructors
    public CallAnswerMessage() {}

    public CallAnswerMessage(String callID, String initiatorID, boolean accepted) {
        this.callID = callID;
        this.initiatorID = initiatorID;
        this.accepted = accepted;
    }
}