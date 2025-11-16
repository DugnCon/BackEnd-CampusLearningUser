package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class CallSignalMessage {
    private String fromUserID;     // Auto-set by server
    private String toUserID;       // Người nhận signal
    private WebRTCSignal signal;   // WebRTC signaling data

    // Constructor cho convenience
    public CallSignalMessage() {}

    public CallSignalMessage(String toUserID, WebRTCSignal signal) {
        this.toUserID = toUserID;
        this.signal = signal;
    }
}