package com.javaweb.scheduler;

import com.javaweb.entity.Post.PostEntity;
import com.javaweb.entity.Post.PostLikeEntity;
import com.javaweb.repository.IPostLikeRepository;
import com.javaweb.repository.IPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class LikeSyncScheduler {
    @Autowired
    private IPostLikeRepository postLikeRepository;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private IPostRepository postRepository;

    @PostConstruct
    public void init() {
        fallbackLikesToRedis();
    }

    @Scheduled(fixedRate = 60000) // mỗi 1 phút (có thể chỉnh)
    public void fallbackLikesToRedis() {
        List<PostLikeEntity> allLikes = postLikeRepository.findAll();
        if (allLikes.isEmpty()) return;

        // Gom nhóm userId theo postId
        Map<Long, Set<Long>> postLikesMap = new HashMap<>();

        for (PostLikeEntity like : allLikes) {
            Long postId = like.getPost().getPostID();
            Long userId = like.getUser().getUserID();
            postLikesMap.computeIfAbsent(postId, k -> new HashSet<>()).add(userId);
        }

        for (Map.Entry<Long, Set<Long>> entry : postLikesMap.entrySet()) {
            Long postId = entry.getKey();
            Set<Long> userIds = entry.getValue();

            String likeKey = "post:likes:" + postId; //1 key chứa toàn bộ userId
            String countKey = "post:likes:count:" + postId;

            // Xóa key cũ để tránh cache sai
            redisTemplate.delete(likeKey);
            redisTemplate.delete(countKey);

            // Nạp lại danh sách user đã like
            for (Long userId : userIds) {
                redisTemplate.opsForSet().add(likeKey, userId.toString());
            }
            redisTemplate.expire(likeKey, java.time.Duration.ofHours(6));

            // Nạp lại likeCount từ DB
            PostEntity postEntity = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));
            redisTemplate.opsForValue().set(countKey, String.valueOf(postEntity.getLikesCount()));
            redisTemplate.expire(countKey, java.time.Duration.ofHours(6));
        }

        System.out.println("Redis đã fallback dữ liệu like từ MySQL thành công!");
    }

}
