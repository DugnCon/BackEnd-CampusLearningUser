package com.javaweb.entity.ChatAndCall;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.javaweb.entity.UserEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "messages")
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MessageID")
    private Long messageID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversationID")
    private ConversationEntity conversation;

    @ManyToOne
    @JoinColumn(name = "SenderID", referencedColumnName = "UserID")
    private UserEntity sender;

    @Column(name = "Type", length = 20, nullable = false)
    private String type; // "text", "image", "file", "video", "audio"

    @Column(name = "Content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "MediaUrl", length = 255)
    private String mediaUrl;

    @Column(name = "MediaType", length = 20)
    private String mediaType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReplyToMessageID")
    @JsonBackReference
    private MessageEntity repliedTo; // tin nhắn gốc

    @OneToMany(mappedBy = "repliedTo", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<MessageEntity> replies; // danh sách các tin nhắn trả lời tin này

    @Column(name = "IsEdited")
    private Boolean isEdited = false;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    @Column(name = "CreatedAt", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "DeletedAt")
    private LocalDateTime deletedAt;

    // Getters and Setters
    public Long getMessageID() {
        return messageID;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public void setMessageID(Long messageID) {
        this.messageID = messageID;
    }

    public Boolean getEdited() {
        return isEdited;
    }

    public void setEdited(Boolean edited) {
        isEdited = edited;
    }

    public UserEntity getSender() {
        return sender;
    }

    public void setSender(UserEntity sender) {
        this.sender = sender;
    }

    public ConversationEntity getConversation() {
        return conversation;
    }

    public void setConversation(ConversationEntity conversation) {
        this.conversation = conversation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public List<MessageEntity> getReplies() {
        return replies;
    }

    public void setReplies(List<MessageEntity> replies) {
        this.replies = replies;
    }

    public MessageEntity getRepliedTo() {
        return repliedTo;
    }

    public void setRepliedTo(MessageEntity repliedTo) {
        this.repliedTo = repliedTo;
    }

    public Boolean getIsEdited() {
        return isEdited;
    }

    public void setIsEdited(Boolean isEdited) {
        this.isEdited = isEdited;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    // Utility methods
    public void markAsEdited() {
        this.isEdited = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsDeleted() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isMediaMessage() {
        return "image".equals(type) || "file".equals(type) || "video".equals(type) || "audio".equals(type);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}