package com.javaweb.entity.Post;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.javaweb.entity.UserEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "commentlikes")
public class CommentLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CommentLikeID")
    private Long commentLikeID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commentID")
    @JsonManagedReference
    private CommentEntity comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private UserEntity user;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
    public CommentEntity getComment() {
        return comment;
    }

    public void setComment(CommentEntity comment) {
        this.comment = comment;
    }

    public Long getCommentLikeID() {
        return commentLikeID;
    }

    public void setCommentLikeID(Long commentLikeID) {
        this.commentLikeID = commentLikeID;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
