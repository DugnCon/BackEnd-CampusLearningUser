package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class CallRejectMessage {
    private String callID;
    private String initiatorID;    // Người khởi tạo cuộc gọi

    public CallRejectMessage() {}

    public CallRejectMessage(String callID, String initiatorID) {
        this.callID = callID;
        this.initiatorID = initiatorID;
    }
}