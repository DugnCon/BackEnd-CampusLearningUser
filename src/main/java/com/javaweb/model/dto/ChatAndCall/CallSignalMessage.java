package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class CallSignalMessage {
    private String fromUserID;     // Auto-set by server
    private Long toUserID;
    private Long callID;// Người nhận signal
    private WebRTCSignal signal;   // WebRTC signaling data

    // Constructor cho convenience
    public CallSignalMessage() {}
}