package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class CallAnswerMessage {
    private Long callID;        // ID cuộc gọi
    private Long initiatorID;   // ID người khởi tạo cuộc gọi
    private boolean accepted;   // true nếu chấp nhận, false nếu từ chối
    private Long fromUserID;    // ID người trả lời (optional)
}