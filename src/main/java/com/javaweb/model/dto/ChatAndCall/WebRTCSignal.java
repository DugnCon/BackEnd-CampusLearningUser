package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class WebRTCSignal {
    private String type;        // "offer", "answer", "candidate"
    private String sdp;         // SDP description (cho offer/answer)
    private IceCandidate candidate; // ICE candidate data
}