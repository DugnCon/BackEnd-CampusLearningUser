package com.javaweb.entity.ChatAndCall;

import com.javaweb.entity.UserEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversationparticipants")
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
public class ConversationParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ParticipantID")
    private Long participantID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversationID")
    private ConversationEntity conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private UserEntity user;

    @Column(name = "JoinedAt", nullable = false)
    @UpdateTimestamp
    private LocalDateTime joinedAt;

    @Column(name = "LeftAt")
    private LocalDateTime leftAt;

    @Column(name = "Role", length = 20)
    private String role; // "admin", "member", "owner"

    @Column(name = "LastReadMessageID")
    private Long lastReadMessageID;

    @Column(name = "IsAdmin")
    private Boolean isAdmin = false;

    @Column(name = "IsMuted")
    private Boolean isMuted = false;

    // Getters and Setters

    public Boolean getMuted() {
        return isMuted;
    }

    public void setMuted(Boolean muted) {
        isMuted = muted;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public ConversationEntity getConversation() {
        return conversation;
    }

    public void setConversation(ConversationEntity conversation) {
        this.conversation = conversation;
    }

    public Long getParticipantID() {
        return participantID;
    }

    public void setParticipantID(Long participantID) {
        this.participantID = participantID;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public LocalDateTime getLeftAt() {
        return leftAt;
    }

    public void setLeftAt(LocalDateTime leftAt) {
        this.leftAt = leftAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
        this.isAdmin = "admin".equals(role) || "owner".equals(role);
    }

    public Long getLastReadMessageID() {
        return lastReadMessageID;
    }

    public void setLastReadMessageID(Long lastReadMessageID) {
        this.lastReadMessageID = lastReadMessageID;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Boolean getIsMuted() {
        return isMuted;
    }

    public void setIsMuted(Boolean isMuted) {
        this.isMuted = isMuted;
    }

    // Utility methods
    public boolean isActive() {
        return leftAt == null;
    }

    public void leaveConversation() {
        this.leftAt = LocalDateTime.now();
    }

}