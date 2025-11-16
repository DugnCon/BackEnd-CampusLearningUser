package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class JoinCallMessage {
    private String callID;

    public JoinCallMessage() {}

    public JoinCallMessage(String callID) {
        this.callID = callID;
    }
}