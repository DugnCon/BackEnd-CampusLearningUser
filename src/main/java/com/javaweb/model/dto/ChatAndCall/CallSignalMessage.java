package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class CallSignalMessage {
    private String fromUserID;
    private Long toUserID;
    private Long callID;
    private WebRTCSignal signal;

    public CallSignalMessage() {}
}