package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class RTCIceCandidate {
    private String candidate;
    private String sdpMid;
    private Integer sdpMLineIndex;
    private String usernameFragment;

    public RTCIceCandidate() {}

    public RTCIceCandidate(String candidate, String sdpMid, Integer sdpMLineIndex) {
        this.candidate = candidate;
        this.sdpMid = sdpMid;
        this.sdpMLineIndex = sdpMLineIndex;
    }
}