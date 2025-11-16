package com.javaweb.model.dto.ChatAndCall;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KeepAliveMessage {
    private String userId;
    private String callId;
    private Long timestamp;
}