package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class MediaToggleMessage {
    private String callID;
    private String type;       // "audio", "video"
    private boolean enabled;

    public MediaToggleMessage() {}

    public MediaToggleMessage(String callID, String type, boolean enabled) {
        this.callID = callID;
        this.type = type;
        this.enabled = enabled;
    }
}