package com.javaweb.model.dto.ChatAndCall;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebRTCAnswerMessage {
    private String fromUserId;
    private String toUserId;
    private String callId;
    private Object answer; // RTCSessionDescription
    private Long timestamp;
}