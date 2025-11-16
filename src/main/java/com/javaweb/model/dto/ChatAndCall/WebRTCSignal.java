package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class WebRTCSignal {
    private String type;           // "offer", "answer", "candidate"
    private String sdp;            // Session Description Protocol
    private RTCIceCandidate candidate; // ICE candidate

    // Constructors
    public WebRTCSignal() {}

    public WebRTCSignal(String type, String sdp) {
        this.type = type;
        this.sdp = sdp;
    }

    public WebRTCSignal(String type, RTCIceCandidate candidate) {
        this.type = type;
        this.candidate = candidate;
    }
}