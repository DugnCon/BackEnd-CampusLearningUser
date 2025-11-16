package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class LeaveCallMessage {
    private String callID;

    public LeaveCallMessage() {}

    public LeaveCallMessage(String callID) {
        this.callID = callID;
    }
}