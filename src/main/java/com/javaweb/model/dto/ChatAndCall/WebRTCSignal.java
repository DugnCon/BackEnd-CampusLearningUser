package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

import java.util.Map;

@Data
public class WebRTCSignal {
    private String type;
    private String sdp;            // Dùng cho offer/answer
    private RTCIceCandidate candidate; // Dùng cho candidate

    // Helper methods
    public boolean isSDP() {
        return "offer".equals(type) || "answer".equals(type);
    }

    public boolean isCandidate() {
        return "candidate".equals(type);
    }
}