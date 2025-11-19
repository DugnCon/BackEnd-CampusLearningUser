package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class CallRejectMessage {
    private Long callID;        // ID cuộc gọi
    private Long initiatorID;   // ID người khởi tạo cuộc gọi
    private Long fromUserID;    // ID người từ chối (optional)
}