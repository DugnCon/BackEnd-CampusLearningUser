package com.javaweb.model.dto.Post;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PostDetailDTO {
    private Long postID;
    private String content;
    private String type;
    private String visibility;
    private String location;
    private boolean isLiked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer likesCount;
    private Integer commentsCount;
    private Integer sharesCount;
    private Integer reportsCount;
    private String fullName;
    private String userName;
    private String userImage;
    List<PostCommentDTO> comment;
    Set<PostMediaDTO> media = new HashSet<>();

    public Long getPostID() {
        return postID;
    }

    public void setPostID(Long postID) {
        this.postID = postID;
    }

    public PostDetailDTO() {
    }

    public PostDetailDTO(String content, String type, String visibility, String location,
                         LocalDateTime createdAt, LocalDateTime updatedAt,
                         Integer likesCount, Integer commentsCount, Integer sharesCount,
                         Integer reportsCount, String fullName, String userName, String userImage) {
        this.content = content;
        this.type = type;
        this.visibility = visibility;
        this.location = location;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.sharesCount = sharesCount;
        this.reportsCount = reportsCount;
        this.fullName = fullName;
        this.userName = userName;
        this.userImage = userImage;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public Set<PostMediaDTO> getMedia() {
        return media;
    }

    public void setMedia(Set<PostMediaDTO> media) {
        this.media = media;
    }

    public List<PostCommentDTO> getComment() {
        return comment;
    }

    public void setComment(List<PostCommentDTO> comment) {
        this.comment = comment;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
