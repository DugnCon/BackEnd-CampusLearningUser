package com.javaweb.model.dto.ChatAndCall;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InitiateCallRequest {
    private String conversationType;
    private Long conversationId;
    private Long receiverId;
    private String type;
    private List<Long> participantIds;
}