package com.javaweb.entity.Post;

import com.fasterxml.jackson.annotation.JsonBackReference;
<<<<<<< HEAD
import com.javaweb.entity.UserEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
=======
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.javaweb.entity.UserEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
<<<<<<< HEAD
=======
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CommentID")
    private Long commentID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postID")
<<<<<<< HEAD
=======
    @JsonBackReference
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
<<<<<<< HEAD
=======
    @JsonBackReference
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
    private UserEntity user;

    @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<CommentLikeEntity> commentlike = new ArrayList<>();

    @Column(name = "Content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "LikesCount")
    private Integer likesCount = 0;

    @Column(name = "RepliesCount")
    private Integer repliesCount = 0;

    @Column(name = "CreatedAt")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "DeletedAt")
    private LocalDateTime deletedAt;

    @Column(name = "IsEdited")
    private Boolean isEdited = false;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

<<<<<<< HEAD
=======
    @Transient
    private Long userID;

    @Transient
    private String fullName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    @JsonBackReference
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
    public List<CommentLikeEntity> getCommentlike() {
        return commentlike;
    }

    public void setCommentlike(List<CommentLikeEntity> commentlike) {
        this.commentlike = commentlike;
    }

    public Long getCommentID() {
        return commentID;
    }

    public void setCommentID(Long commentID) {
        this.commentID = commentID;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Boolean getEdited() {
        return isEdited;
    }

    public void setEdited(Boolean edited) {
        isEdited = edited;
    }

<<<<<<< HEAD
=======
    @JsonBackReference
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

<<<<<<< HEAD
=======
    @JsonBackReference
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
    public PostEntity getPost() {
        return post;
    }

    public void setPost(PostEntity post) {
        this.post = post;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public Integer getRepliesCount() {
        return repliesCount;
    }

    public void setRepliesCount(Integer repliesCount) {
        this.repliesCount = repliesCount;
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
}
