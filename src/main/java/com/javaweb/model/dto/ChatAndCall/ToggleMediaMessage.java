package com.javaweb.model.dto.ChatAndCall;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ToggleMediaMessage {
    private String userId;
    private String callId;
    private boolean muted; // cho audio
    private boolean enabled; // cho video
    private Long timestamp;
}