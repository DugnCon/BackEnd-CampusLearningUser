package com.javaweb.entity.Post;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.javaweb.entity.UserEntity;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "posts")
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PostID")
    private Long postID;

    @OneToMany(mappedBy = "posts", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<PostMediaEntity> media = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    @JsonBackReference
    private UserEntity user;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<PostLikeEntity> postlike = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<CommentEntity> comment = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "posttags",
            schema = "campuslearning",
            joinColumns = @JoinColumn(name = "PostID"),
            inverseJoinColumns = @JoinColumn(name = "TagID")
    )
    private Set<TagsEntity> tags = new HashSet<>();



    @JsonManagedReference
    public Set<PostMediaEntity> getMedia() {
        return media;
    }
    @JsonBackReference
    public List<PostLikeEntity> getPostlike() {
        return postlike;
    }

    public List<CommentEntity> getComment() {
        return comment;
    }

    public void setComment(List<CommentEntity> comment) {
        this.comment = comment;
    }

    public void setPostlike(List<PostLikeEntity> postlike) {
        this.postlike = postlike;
    }

    public void setMedia(Set<PostMediaEntity> media) {
        this.media = media;
    }

    @Column(name = "Content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "Type", length = 20)
    private String type;

    @Column(name = "Visibility", length = 20)
    private String visibility;

    @Column(name = "Location", length = 255)
    private String location;

    @Column(name = "CreatedAt")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DeletedAt")
    private Date deletedAt;

    @Column(name = "LikesCount")
    private Integer likesCount = 0;

    @Column(name = "CommentsCount")
    private Integer commentsCount;

    @Column(name = "SharesCount")
    private Integer sharesCount;

    @Column(name = "ReportsCount")
    private Integer reportsCount;

    @Column(name = "IsFlagged")
    private Boolean isFlagged;

    @Column(name = "FlaggedReason", length = 255)
    private String flaggedReason;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FlaggedAt")
    private Date flaggedAt;

    @Column(name = "IsDeleted")
    private Boolean isDeleted;

    @Transient
    private boolean isLiked;

    @Transient
    private String fullName;

    @Transient
    private String userImage;

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public boolean getLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Set<TagsEntity> getTags() {
        return tags;
    }

    public void setTags(Set<TagsEntity> tags) {
        this.tags = tags;
    }

    public Long getPostID() {
        return postID;
    }

    public void setPostID(Long postID) {
        this.postID = postID;
    }

    public Boolean getFlagged() {
        return isFlagged;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public void setFlagged(Boolean flagged) {
        isFlagged = flagged;
    }
    @JsonBackReference
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    public Integer getSharesCount() {
        return sharesCount;
    }

    public void setSharesCount(Integer sharesCount) {
        this.sharesCount = sharesCount;
    }

    public Integer getReportsCount() {
        return reportsCount;
    }

    public void setReportsCount(Integer reportsCount) {
        this.reportsCount = reportsCount;
    }

    public Boolean getIsFlagged() {
        return isFlagged;
    }

    public void setIsFlagged(Boolean isFlagged) {
        this.isFlagged = isFlagged;
    }

    public String getFlaggedReason() {
        return flaggedReason;
    }

    public void setFlaggedReason(String flaggedReason) {
        this.flaggedReason = flaggedReason;
    }

    public Date getFlaggedAt() {
        return flaggedAt;
    }

    public void setFlaggedAt(Date flaggedAt) {
        this.flaggedAt = flaggedAt;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}