package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;
import java.util.List;

@Data
public class InitiateCallRequest {
    private Long conversationID;
    private String type; // "audio" or "video"
    private List<String> participantIds; // for group calls

    public List<String> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<String> participantIds) {
        this.participantIds = participantIds;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getConversationID() {
        return conversationID;
    }

    public void setConversationID(Long conversationID) {
        this.conversationID = conversationID;
    }
}