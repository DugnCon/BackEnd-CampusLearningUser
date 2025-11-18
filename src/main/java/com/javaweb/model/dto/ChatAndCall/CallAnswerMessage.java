package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class CallAnswerMessage {
    private Long callID;
    private Long initiatorID;    // Người khởi tạo cuộc gọi
    private boolean accepted;      // true = nhận cuộc gọi, false = từ chối

    // Constructors
    public CallAnswerMessage() {}
}