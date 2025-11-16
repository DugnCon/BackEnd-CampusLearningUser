package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class CallEndMessage {
    private String callID;
    private String endedByID;
    private String reason;
    private Long duration;

}