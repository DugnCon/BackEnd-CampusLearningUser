package com.javaweb.service.impl.StoryService;

import antlr.ASTFactory;
import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.entity.Friend.FriendshipEntity;
import com.javaweb.entity.Story.StoryEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.model.dto.ChatAndCall.ConversationDTO;
import com.javaweb.model.dto.StoryDTO;
import com.javaweb.model.dto.UserSuggestions.UserSuggestionDTO;
import com.javaweb.repository.IFriendshipRepository;
import com.javaweb.repository.IStoryRepository;
import com.javaweb.repository.IUserRepository;
import com.javaweb.service.FileStorageService;
import com.javaweb.service.IConservationService;
import com.javaweb.service.IStoryService;
import com.javaweb.service.SolvingByRabbitMQ.Story.StoryProducer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class StoryServiceImpl implements IStoryService {
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private StoryProducer storyProducer;
    @Autowired
    @Qualifier("storyRedisTemplate")
    private RedisTemplate<String, Object> storyRedisTemplate;
    @Autowired
    private IStoryRepository storyRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IFriendshipRepository friendshipRepository;
    @Autowired
    private IConservationService conservationService;
    @Override
    public ResponseEntity<Object> createStory(MultipartFile mediaFile, String mediaType, String textContent, String backgroundColor, String fontStyle, Long userId) throws IOException {
        try {
            String file = fileStorageService.saveFile(mediaFile);

            Map<String,Object> storyData = new HashMap<>();
            storyData.put("userId", userId);
            storyData.put("mediaFile", file);
            storyData.put("mediaType", mediaType);
            storyData.put("textContent", textContent);
            storyData.put("backgroundColor", backgroundColor);
            storyData.put("fontStyle", fontStyle);

            storyProducer.sendUpLoadTask(storyData);

            //Trả về response thành công
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Story đang được xử lý và sẽ sẵn sàng trong giây lát");
            response.put("mediaUrl", file);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            //Xử lý lỗi
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi tạo story: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
    @Override
    public ResponseEntity<Object> getAllStories(Long userId) {
        try {
            //Thử lấy từ Redis trước
            List<Object> cachedStoryIds = storyRedisTemplate.opsForList()
                    .range("active:stories", 0, -1);

            List<StoryDTO> validStories = new ArrayList<>();
            List<String> keysToDelete = new ArrayList<>();

            if (cachedStoryIds != null && !cachedStoryIds.isEmpty()) {
                System.out.println("Found " + cachedStoryIds.size() + " story IDs in Redis cache");

                // Lấy chi tiết từng story từ Redis và check expiration
                for (Object storyIdObj : cachedStoryIds) {
                    String storyId = storyIdObj.toString().trim(); // thêm .trim() để chắc chắn
                    String storyKey = "story:" + storyId;

                    System.out.println("Processing story ID: " + storyId + ", Key: " + storyKey);

                    // Lấy JSON string từ Redis
                    String storyJson = (String) storyRedisTemplate.opsForValue().get(storyKey);

                    if (storyJson != null) {
                        try {
                            StoryDTO cachedStory = objectMapper.readValue(storyJson, StoryDTO.class);
                            System.out.println("Loaded story from Redis: " + cachedStory.getStoryID() +
                                    ", Type: " + cachedStory.getMediaType());

                            // 3. Check nếu story còn hạn
                            if (isStoryValid(cachedStory)) {
                                validStories.add(cachedStory);
                                System.out.println("Story " + storyId + " is valid, added to list");
                            } else {
                                keysToDelete.add(storyKey);
                                storyRedisTemplate.opsForList().remove("active:stories", 1, storyId);
                                System.out.println("Story " + storyId + " has expired, marked for deletion");
                            }
                        } catch (Exception e) {
                            System.err.println("Error parsing story JSON for ID " + storyId + ": " + e.getMessage());
                            keysToDelete.add(storyKey);
                            storyRedisTemplate.opsForList().remove("active:stories", 1, storyId);
                        }
                    } else {
                        System.out.println("Story key not found in Redis: " + storyKey);
                        storyRedisTemplate.opsForList().remove("active:stories", 1, storyId);
                    }
                }

                //Xóa các story keys hết hạn
                if (!keysToDelete.isEmpty()) {
                    storyRedisTemplate.delete(keysToDelete);
                    System.out.println("Deleted " + keysToDelete.size() + " expired story keys from Redis");
                }

                System.out.println("Returning " + validStories.size() + " valid stories from Redis cache");

                //Nếu còn stories hợp lệ, trả về
                if (!validStories.isEmpty()) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("stories", validStories);
                    response.put("total", validStories.size());
                    response.put("source", "redis");
                    response.put("expiredRemoved", keysToDelete.size());

                    return ResponseEntity.ok(response);
                }
            } else {
                System.out.println("No story IDs found in Redis cache");
            }

            //Nếu Redis không có stories hợp lệ, lấy từ database
            System.out.println("Fetching stories from database...");
            List<StoryEntity> dbStoryEntities = storyRepository.findActiveStories(LocalDateTime.now());
            System.out.println("Found " + dbStoryEntities.size() + " stories in database");

            List<StoryDTO> validDbStories = new ArrayList<>();

            for (StoryEntity storyEntity : dbStoryEntities) {
                // Convert Entity to DTO
                UserEntity user = userRepository.findById(storyEntity.getUser().getUserID()).orElseThrow();
                UserSuggestionDTO userSuggestionDTO = modelMapper.map(user, UserSuggestionDTO.class);

                FriendshipEntity friendshipEntity = friendshipRepository.isFriend(user.getUserID(), userId,"pending", "accepted");

                if(Objects.equals(storyEntity.getUser().getUserID(), userId)) {
                    StoryDTO storyDTO = convertToDTO(storyEntity);
                    storyDTO.setUser(userSuggestionDTO);

                    if (isStoryValid(storyDTO)) {
                        validDbStories.add(storyDTO);
                        // Cache stories hợp lệ vào Redis
                        cacheStoryInRedis(storyDTO);
                        System.out.println("Cached story to Redis: " + storyDTO.getStoryID());
                    } else {
                        // Xóa story hết hạn khỏi database
                        storyRepository.delete(storyEntity);
                        System.out.println("Deleted expired story from database: " + storyEntity.getStoryID());
                    }
                } else if(friendshipEntity != null) {
                    StoryDTO storyDTO = convertToDTO(storyEntity);
                    storyDTO.setUser(userSuggestionDTO);

                    if (isStoryValid(storyDTO)) {
                        validDbStories.add(storyDTO);
                        // Cache stories hợp lệ vào Redis
                        cacheStoryInRedis(storyDTO);
                        System.out.println("Cached story to Redis: " + storyDTO.getStoryID());
                    } else {
                        // Xóa story hết hạn khỏi database
                        storyRepository.delete(storyEntity);
                        System.out.println("Deleted expired story from database: " + storyEntity.getStoryID());
                    }
                }
            }

            System.out.println("Returning " + validDbStories.size() + " valid stories from database");

            Map<String, Object> response = new HashMap<>();
            response.put("stories", validDbStories);
            response.put("total", validDbStories.size());
            response.put("source", "database");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error fetching stories: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch stories");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    // Method convert Entity to DTO
    private StoryDTO convertToDTO(StoryEntity entity) {
        StoryDTO dto = new StoryDTO();
        dto.setStoryID(entity.getStoryID());
        dto.setUserID(entity.getUser().getUserID());
        dto.setUserFullName(entity.getUser().getFullName());
        dto.setUserImage(entity.getUser().getAvatar());
        dto.setMediaType(entity.getMediaType());
        dto.setMediaUrl(entity.getMediaUrl());
        dto.setTextContent(entity.getTextContent());
        dto.setBackgroundColor(entity.getBackgroundColor());
        dto.setFontStyle(entity.getFontStyle());
        dto.setDuration(entity.getDuration());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setExpiresAt(entity.getExpiresAt());
        dto.setViewCount(0L); // Có thể tính từ repository nếu cần
        dto.setLikeCount(0L); // Có thể tính từ repository nếu cần
        dto.setIsViewed(false);
        dto.setIsLiked(false);
        return dto;
    }

    // Method cache story vào Redis - Lưu dưới dạng JSON
    private void cacheStoryInRedis(StoryDTO story) {
        try {
            if (!isStoryValid(story)) {
                System.out.println("Not caching expired story: " + story.getStoryID());
                return;
            }

            String storyId = story.getStoryID().toString();
            String redisKey = "story:" + storyId;

            String storyJson = objectMapper.writeValueAsString(story);

            storyRedisTemplate.opsForValue().set(
                    redisKey,
                    storyJson, // Lưu JSON string
                    24,
                    TimeUnit.HOURS
            );

            storyRedisTemplate.opsForList().leftPush("active:stories", storyId);
            storyRedisTemplate.expire("active:stories", 24, TimeUnit.HOURS);

            String userStoriesKey = "user:" + story.getUserID() + ":stories";
            storyRedisTemplate.opsForList().leftPush(userStoriesKey, storyId);
            storyRedisTemplate.expire(userStoriesKey, 24, TimeUnit.HOURS);

            System.out.println("Cached story " + storyId + " to Redis as JSON");

        } catch (Exception e) {
            System.err.println("Error caching story in Redis: " + e.getMessage());
        }
    }

    // Method check story còn hạn
    private boolean isStoryValid(StoryDTO story) {
        if (story == null || story.getExpiresAt() == null) {
            return false;
        }
        return story.getExpiresAt().isAfter(LocalDateTime.now());
    }

    /**
     * Reply lại story
     * **/
    @Override
    public ResponseEntity<Object> replyStory(Long storyId, Long userId, Map<String, Object> replyRequest) {
        try {
            //Validate
            StoryEntity story = storyRepository.findById(storyId)
                    .orElseThrow(() -> new RuntimeException("Story not found"));

            String message = (String) replyRequest.get("content");
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Tin nhắn không được để trống"
                ));
            }
            //Tạo conversation với story owner
            Long storyOwnerId = story.getUser().getUserID();

            Map<String, Object> conversationData = new HashMap<>();
            conversationData.put("participants", List.of(storyOwnerId.intValue()));
            conversationData.put("type", "private");
            conversationData.put("title", story.getUser().getFullName());

            ResponseEntity<Object> conversationResponse = conservationService.createConversation(userId, conversationData);
            Map<String, Object> conversationBody = (Map<String, Object>) conversationResponse.getBody();

            if (!conversationBody.get("success").equals(true)) {
                return conversationResponse;
            }

            //Gửi message
            ConversationDTO conversationDataResponse = (ConversationDTO) conversationBody.get("data");
            Long conversationId = Long.valueOf(conversationDataResponse.getConversationID());


            Map<String, Object> messageData = new HashMap<>();
            messageData.put("content", message);
            messageData.put("type", "text");
            messageData.put("storyReply", true);
            messageData.put("originalStoryId", storyId);

            conservationService.sendMessageToConversation(userId, conversationId, messageData);

            //Return success
            boolean isExisting = (Boolean) conversationBody.get("existing");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đã gửi tin nhắn trả lời story",
                    "conversationId", conversationId,
                    "existing", isExisting
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Không thể gửi tin nhắn trả lời story"
            ));
        }
    }
}
