package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class HeartbeatMessage {
    private String callID;

    public HeartbeatMessage() {}

    public HeartbeatMessage(String callID) {
        this.callID = callID;
    }
}