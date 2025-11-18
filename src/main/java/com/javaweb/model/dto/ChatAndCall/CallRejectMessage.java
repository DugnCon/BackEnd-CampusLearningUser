package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class CallRejectMessage {
    private Long callID;
    private Long initiatorID;    // Người khởi tạo cuộc gọi

    public CallRejectMessage() {}

}