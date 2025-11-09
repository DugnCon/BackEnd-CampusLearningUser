package com.javaweb.entity.Post;

import com.javaweb.entity.UserEntity;
<<<<<<< HEAD
=======
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "postlikes")
<<<<<<< HEAD
=======
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
public class PostLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LikeID")
    private Long likeID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postID")
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private UserEntity user;

    @Column(name = "CreatedAt", nullable = false)
<<<<<<< HEAD
    private LocalDateTime createdAt;

=======
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "IsLiked")
    private Boolean isLiked;

    public Boolean getLiked() {
        return isLiked;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }

>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public PostEntity getPost() {
        return post;
    }

    public void setPost(PostEntity post) {
        this.post = post;
    }

    public Long getLikeID() {
        return likeID;
    }

    public void setLikeID(Long likeID) {
        this.likeID = likeID;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
<<<<<<< HEAD
=======

>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
}
