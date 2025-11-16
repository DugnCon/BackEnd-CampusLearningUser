package com.javaweb.model.dto.ChatAndCall;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScreenShareMessage {
    private String userId;
    private String callId;
    private boolean active;
    private Long timestamp;
}