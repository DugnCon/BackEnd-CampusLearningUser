package com.javaweb.service;

import com.javaweb.entity.Post.PostEntity;
import com.javaweb.entity.Post.PostLikeEntity;
import com.javaweb.model.dto.Post.PostLikeDTO;
import com.javaweb.repository.IPostLikeRepository;
import com.javaweb.repository.IPostRepository;
import com.javaweb.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class PostLikeService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IPostLikeRepository postLikeRepository;

    @Autowired
    private IPostRepository postRepository;

    @Autowired
    private IUserRepository userRepository;


    private String likeKey(Long postId) { return "post:likes:" + postId; }
    private String likeCountKey(Long postId) { return "post:likes:count:" + postId; }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> toggleLike(Long postId, Long userId) throws Exception {
        String likeKey = likeKey(postId);
        String countKey = likeCountKey(postId);

        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        //Lấy count từ Redis, nếu chưa có thì set String
        String currentCountStr = (String) redisTemplate.opsForValue().get(countKey);
        if (currentCountStr == null) {
            redisTemplate.opsForValue().set(countKey, String.valueOf(postEntity.getLikesCount()));
            currentCountStr = String.valueOf(postEntity.getLikesCount());
        }

        boolean alreadyLiked = Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(likeKey, userId.toString())
        );

        int likesCount;

        if (alreadyLiked) {
            // Unlike
            redisTemplate.opsForSet().remove(likeKey, userId.toString());
            Long newCount = redisTemplate.opsForValue().decrement(countKey);
            if (newCount == null || newCount < 0) {
                newCount = 0L;
                redisTemplate.opsForValue().set(countKey, "0"); //Sau lưu hết String cho nó lành
            }
            likesCount = newCount.intValue();
            updateLikeDB(postEntity, userId, false, likesCount);

        } else {
            // Like
            redisTemplate.opsForSet().add(likeKey, userId.toString());
            Long newCount = redisTemplate.opsForValue().increment(countKey);
            if (newCount == null) {
                newCount = 1L;
                redisTemplate.opsForValue().set(countKey, "1"); // ← String
            }
            likesCount = newCount.intValue();
            updateLikeDB(postEntity, userId, true, likesCount);
        }

        PostLikeDTO dto = new PostLikeDTO(postId, userId, !alreadyLiked, likesCount);
        return ResponseEntity.ok(Map.of("data", dto, "success", true));
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    public void updateLikeDB(PostEntity postEntity, Long userId, boolean liked, int likesCount) {
        postEntity.setLikesCount(likesCount);
        postRepository.save(postEntity);

        PostLikeEntity existingLike = postLikeRepository.getPostLike(postEntity.getPostID(), userId);

        if (liked) {
            if (existingLike == null) {
                PostLikeEntity newLike = new PostLikeEntity();
                newLike.setPost(postEntity);
                newLike.setLiked(true);
                newLike.setUser(userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found")));
                postLikeRepository.save(newLike);
            }
        } else {
            if (existingLike != null) {
                postLikeRepository.delete(existingLike);
            }
        }
    }

    public boolean hasLiked(Long postId, Long userId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(likeKey(postId), userId.toString()));
    }
}
