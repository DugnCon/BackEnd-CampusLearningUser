package com.javaweb.service.SolvingByRabbitMQ.Story;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.config.RabbitMQConfig;
import com.javaweb.entity.Story.StoryEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.model.dto.StoryDTO;
import com.javaweb.model.dto.User.UserSuggestions.UserSuggestionDTO;
import com.javaweb.repository.IStoryRepository;
import com.javaweb.repository.IUserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class StoryConsumer {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    @Qualifier("storyRedisTemplate")
    private RedisTemplate<String, Object> storyRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper; // Thêm ObjectMapper

    @Autowired
    private IStoryRepository storyRepository;

    @Autowired
    private IUserRepository userRepository;

    @RabbitListener(queues = RabbitMQConfig.STORY_UPLOAD_QUEUE)
    public void receiveUploadTask(Map<String, Object> message) {
        try {
            System.out.println("Received story upload task: " + message);

            // Extract data from message
            Long userId = Long.valueOf(message.get("userId").toString());
            String mediaUrl = (String) message.get("mediaFile");
            String mediaType = (String) message.get("mediaType");
            String textContent = (String) message.get("textContent");
            String backgroundColor = (String) message.get("backgroundColor");
            String fontStyle = (String) message.get("fontStyle");

            // Find user
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            UserSuggestionDTO userSuggestionDTO = modelMapper.map(user, UserSuggestionDTO.class);

            // Create and save StoryEntity
            StoryEntity story = new StoryEntity();
            story.setUser(user);
            story.setMediaType(mediaType);
            story.setMediaUrl(mediaUrl);
            story.setTextContent(textContent);
            story.setBackgroundColor(backgroundColor);
            story.setFontStyle(fontStyle);
            story.setDuration(15); // Default 15 seconds
            story.setCreatedAt(LocalDateTime.now());
            story.setExpiresAt(LocalDateTime.now().plusHours(24));

            StoryEntity savedStory = storyRepository.save(story);

            // Convert to DTO
            StoryDTO storyDTO = modelMapper.map(savedStory, StoryDTO.class);
            storyDTO.setUserFullName(user.getFullName());
            storyDTO.setUserID(userId);
            storyDTO.setUserImage(user.getAvatar());
            storyDTO.setViewCount(0L);
            storyDTO.setLikeCount(0L);
            storyDTO.setIsViewed(false);
            storyDTO.setIsLiked(false);
            storyDTO.setUser(userSuggestionDTO);

            System.out.println("Story saved to database with ID: " + savedStory.getStoryID());

            // Cache story DTO as JSON in Redis
            cacheStoryInRedis(storyDTO);

            System.out.println("Story processing completed for ID: " + savedStory.getStoryID());

        } catch (Exception e) {
            System.err.println("Error processing story upload task: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cacheStoryInRedis(StoryDTO storyDTO) {
        try {
            String redisKey = "story:" + storyDTO.getStoryID();
            String storyIdStr = storyDTO.getStoryID().toString(); // Convert to String

            // Convert DTO to JSON string
            String storyJson = objectMapper.writeValueAsString(storyDTO);

            // Cache story data as JSON
            storyRedisTemplate.opsForValue().set(
                    redisKey,
                    storyJson,
                    24,
                    TimeUnit.HOURS
            );

            // Cache story ID in user's story list - SỬA: dùng String
            String userStoriesKey = "user:" + storyDTO.getUserID() + ":stories";
            storyRedisTemplate.opsForList().leftPush(userStoriesKey, storyIdStr); // Lưu String
            storyRedisTemplate.expire(userStoriesKey, 24, TimeUnit.HOURS);

            // Cache to active stories list - SỬA: dùng String
            storyRedisTemplate.opsForList().leftPush("active:stories", storyIdStr); // Lưu String
            storyRedisTemplate.expire("active:stories", 24, TimeUnit.HOURS);

            System.out.println("Story cached in Redis with key: " + redisKey + ", ID: " + storyIdStr);

        } catch (Exception e) {
            System.err.println("Error caching story in Redis: " + e.getMessage());
            e.printStackTrace();
        }
    }
}