package com.javaweb.model.dto.ChatAndCall;

import com.javaweb.model.dto.UserSuggestions.UserSuggestionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationDTO {
    private Long conversationID;
    private String title;
    private String type;
    private String avatar; //Người kia
    private LocalDateTime lastMessageContent;
    private LocalDateTime lastMessageTime;
    private Integer unreadCount;
    private List<UserSuggestionDTO> participants;

    public List<UserSuggestionDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserSuggestionDTO> participants) {
        this.participants = participants;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public LocalDateTime getLastMessageContent() {
        return lastMessageContent;
    }

    public void setLastMessageContent(LocalDateTime lastMessageContent) {
        this.lastMessageContent = lastMessageContent;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getConversationID() {
        return conversationID;
    }

    public void setConversationID(Long conversationID) {
        this.conversationID = conversationID;
    }
}
