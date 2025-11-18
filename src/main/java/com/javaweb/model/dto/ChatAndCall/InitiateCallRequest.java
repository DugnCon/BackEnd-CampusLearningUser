package com.javaweb.model.dto.ChatAndCall;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitiateCallRequest {
    private String conversationType;
    private Long conversationID;
    private String type;
    private List<Long> participantIds;
}