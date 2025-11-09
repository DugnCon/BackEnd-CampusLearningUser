package com.javaweb.model.dto;

import lombok.Data;

@Data
public class PostLikeDTO {
    private Long postID;
    private Long userId;
    private boolean isLiked;
    private Integer likesCount;

    public PostLikeDTO(Long postID, Long userId, boolean isLiked, Integer likesCount) {
        this.postID = postID;
        this.userId = userId;
        this.isLiked = isLiked;
        this.likesCount = likesCount;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPostID() {
        return postID;
    }

    public void setPostID(Long postID) {
        this.postID = postID;
    }
}

