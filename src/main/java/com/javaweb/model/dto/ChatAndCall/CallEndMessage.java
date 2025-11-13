package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class CallEndMessage {
    private Long callID;
    private Long endedByID;
    private String endedByName;
    private String reason; // "normal", "timeout", "rejected", "cancelled"
}