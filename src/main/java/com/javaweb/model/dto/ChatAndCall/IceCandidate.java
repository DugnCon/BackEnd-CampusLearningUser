package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class IceCandidate {
    private String candidate;
    private String sdpMid;
    private Integer sdpMLineIndex;
}