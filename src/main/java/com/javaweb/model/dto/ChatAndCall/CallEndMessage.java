package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class CallEndMessage {
    private Long callID;
    private Long endedByID;
    private String reason;
    private Long duration;

}