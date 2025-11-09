package com.javaweb.entity.ChatAndCall;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messagestatus")
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class MessageStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StatusID")
    private Long statusID;

    @Column(name = "MessageID", nullable = false)
    private Long messageID;

    @Column(name = "UserID", nullable = false)
    private Long userID;

    @Column(name = "Status", length = 20, nullable = false)
    private String status; // "sent", "delivered", "read", "failed"

    @Column(name = "UpdatedAt", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getStatusID() {
        return statusID;
    }

    public void setStatusID(Long statusID) {
        this.statusID = statusID;
    }

    public Long getMessageID() {
        return messageID;
    }

    public void setMessageID(Long messageID) {
        this.messageID = messageID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Utility methods
    public boolean isRead() {
        return "read".equals(status);
    }

    public boolean isDelivered() {
        return "delivered".equals(status) || "read".equals(status);
    }

    public boolean isSent() {
        return "sent".equals(status) || "delivered".equals(status) || "read".equals(status);
    }

    public void markAsRead() {
        setStatus("read");
    }

    public void markAsDelivered() {
        setStatus("delivered");
    }

    public void markAsSent() {
        setStatus("sent");
    }

    public void markAsFailed() {
        setStatus("failed");
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}