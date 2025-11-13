package com.javaweb.entity.ChatAndCall;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.javaweb.entity.UserEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversations")
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class ConversationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ConversationID")
    private Long conversationID;

    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<ConversationParticipantEntity> conversationParticipant;

    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<CallEntity> call = new ArrayList<>();

    @Column(name = "Type", length = 20, nullable = false)
    private String type; // "private", "group"

    @Column(name = "Title", length = 255)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatedBy", referencedColumnName = "UserID")
    private UserEntity user;

    @Column(name = "CreatedAt", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "LastMessageAt")
    private LocalDateTime lastMessageAt;

    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = true;

    public List<CallEntity> getCall() {
        return call;
    }

    public void setCall(List<CallEntity> call) {
        this.call = call;
    }

    @JsonBackReference
    public List<ConversationParticipantEntity> getConversationParticipant() {
        return conversationParticipant;
    }

    public void setConversationParticipant(List<ConversationParticipantEntity> conversationParticipant) {
        this.conversationParticipant = conversationParticipant;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Long getConversationID() {
        return conversationID;
    }

    public void setConversationID(Long conversationID) {
        this.conversationID = conversationID;
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

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
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

    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    // Pre-update callback
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}