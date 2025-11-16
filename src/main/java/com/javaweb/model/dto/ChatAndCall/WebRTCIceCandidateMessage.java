package com.javaweb.model.dto.ChatAndCall;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebRTCIceCandidateMessage {
    private String fromUserId;
    private String toUserId;
    private String callId;
    private Object candidate; // RTCIceCandidate
    private Long timestamp;
}