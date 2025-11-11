package com.javaweb.service.impl.ConversationService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.entity.ChatAndCall.ConversationEntity;
import com.javaweb.entity.ChatAndCall.ConversationParticipantEntity;
import com.javaweb.entity.ChatAndCall.MessageEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.model.dto.ChatAndCall.ConversationDTO;
import com.javaweb.model.dto.UserSuggestions.UserSuggestionDTO;
import com.javaweb.repository.IConversationParticipantRepository;
import com.javaweb.repository.IConversationRepository;
import com.javaweb.repository.IMessageRepository;
import com.javaweb.repository.IUserRepository;
import com.javaweb.service.IConservationService;
import com.javaweb.service.SolvingByRabbitMQ.Message.MessageFileProducer;
import com.javaweb.service.SolvingByRabbitMQ.Message.MessageProducer;
import com.javaweb.utils.ChatSocketHandler;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import java.awt.print.Pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ConversationServiceImpl implements IConservationService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IConversationRepository conversationRepository;

    @Autowired
    private IConversationParticipantRepository conversationParticipantRepository;

    @Autowired
    private IMessageRepository messageRepository;

    @Autowired
    @Qualifier("conversationRedisTemplate")
    private RedisTemplate<String, Object> conversationRedisTemplate;

    @Autowired
    @Qualifier("messageRedisTemplate")
    private RedisTemplate<String, Object> messageRedisTemplate;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private MessageFileProducer messageFileProducer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChatSocketHandler chatSocketHandler;

    @Autowired
    @Qualifier("userLookupTaskExecutor")
    private TaskExecutor taskExecutor;

    private static final Logger log = LoggerFactory.getLogger(ConversationServiceImpl.class);

    // ============ CONSTANTS ============
    private static final String USER_CONVERSATIONS_KEY_PREFIX = "user_conversations:";
    private static final String CONVERSATION_KEY_PREFIX = "conversation:";
    private static final String DIRECT_CONVERSATION_KEY_PREFIX = "direct_conv:";
    private static final Duration CACHE_TTL = Duration.ofHours(2);

    // ============ CONVERSATION METHODS ============

    /**
     * Lấy tất cả conversations của user
     */
    @Override
    public ResponseEntity<Object> getAllConversation(Long currentUserId) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();
        String source = "cache";

        try {
            List<ConversationDTO> conversations = getAllConversationsWithFallback(currentUserId);

            long duration = System.currentTimeMillis() - startTime;

            // Xác định source
            if (duration > 50) {
                source = "database";
            }

            response.put("success", true);
            response.put("data", conversations);
            response.put("count", conversations.size());
            response.put("duration", duration + "ms");
            response.put("source", source);

            log.debug("Get all conversations for user {} - {}ms, {} conversations, source: {}",
                    currentUserId, duration, conversations.size(), source);

        } catch (Exception e) {
            log.error("Error getting conversations for user {}: {}", currentUserId, e.getMessage());
            response.put("success", false);
            response.put("data", new ArrayList<>());
            response.put("message", "Không thể tải danh sách cuộc trò chuyện");
            response.put("source", "fallback");
        }

        return ResponseEntity.ok(response);
    }

    // ============ CONVERSATION METHODS ============

    /**
     * Tạo conversation (cả private và group)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> createConversation(Long userId, Map<String, Object> conversationData) {
        long startTime = System.currentTimeMillis();

        try {
            List<Integer> participants = (List<Integer>) conversationData.get("participants");
            String type = (String) conversationData.get("type");
            String title = (String) conversationData.get("title");

            // Validate input
            if (type == null || (!"private".equals(type) && !"group".equals(type))) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Loại cuộc trò chuyện không hợp lệ. Chỉ hỗ trợ 'private' hoặc 'group'"
                ));
            }

            if (participants == null || participants.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Danh sách người tham gia không được để trống"
                ));
            }

            // Validate private conversation
            if ("private".equals(type) && participants.size() != 1) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Cuộc trò chuyện private chỉ được có 1 người tham gia"
                ));
            }

            // Validate group conversation
            if ("group".equals(type)) {
                if (title == null || title.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "message", "Tên nhóm không được để trống"
                    ));
                }
                if (participants.size() < 2) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "message", "Nhóm phải có ít nhất 2 người tham gia"
                    ));
                }
            }

            UserEntity currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            // Kiểm tra conversation đã tồn tại chưa (chỉ cho private)
            if ("private".equals(type)) {
                Long friendId = Long.valueOf(participants.get(0));
                ConversationEntity existingConversation = checkExistingPrivateConversation(userId, friendId);
                if (existingConversation != null) {
                    List<UserSuggestionDTO> existingParticipants = getConversationParticipantsAsync(existingConversation.getConversationID()).join();
                    ConversationDTO existingDTO = convertToConversationDTO(existingConversation, existingParticipants, userId);

                    log.info("Conversation already exists between {} and {}: {}", userId, friendId, existingConversation.getConversationID());
                    return ResponseEntity.ok(Map.of(
                            "success", true,
                            "data", existingDTO,
                            "message", "Cuộc trò chuyện đã tồn tại",
                            "existing", true
                    ));
                }
            }

            ConversationEntity conversationEntity = new ConversationEntity();

            conversationEntity.setUser(currentUser);
            conversationEntity.setType(type);
            conversationEntity.setIsActive(true);
            conversationEntity.setCreatedAt(LocalDateTime.now());

            // Tìm participants bất đồng bộ
            List<UserEntity> participantEntities = findUsersAsync(participants).join();

            if (participantEntities.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Không tìm thấy người dùng tham gia"
                ));
            }

            List<UserSuggestionDTO> userSuggestionDTOS = participantEntities.stream()
                    .map(user -> modelMapper.map(user, UserSuggestionDTO.class))
                    .collect(Collectors.toList());

            // Tạo participants
            List<ConversationParticipantEntity> conversationParticipants = createParticipants(conversationEntity, participantEntities, userId);

            // Xử lý theo loại conversation
            if ("private".equals(type)) {
                // Private conversation: set title là tên của người kia
                if (!participantEntities.isEmpty()) {
                    conversationEntity.setTitle(participantEntities.get(0).getFullName());
                }
            } else {
                // Group conversation: set title từ input
                conversationEntity.setTitle(title);
                // Có thể set avatar cho group nếu có
            }

            // Lưu conversation và participants
            ConversationEntity savedConversation = conversationRepository.save(conversationEntity);
            conversationParticipantRepository.saveAll(conversationParticipants);

            // Cache conversation
            cacheConversation(savedConversation, userSuggestionDTOS);

            // Cache direct conversation mapping (chỉ cho private)
            if ("private".equals(type) && !participantEntities.isEmpty()) {
                Long friendId = participantEntities.get(0).getUserID();
                cacheDirectConversation(userId, friendId, savedConversation);
            }

            // Invalidate cache cho tất cả participants
            List<Long> allUserIds = new ArrayList<>();
            allUserIds.add(userId);
            allUserIds.addAll(participantEntities.stream()
                    .map(UserEntity::getUserID)
                    .collect(Collectors.toList()));
            evictUserConversationsCache(allUserIds);

            ConversationDTO responseDTO = toConversationDTO(savedConversation, userSuggestionDTOS);

            log.info("Created {} conversation for user {} with {} participants - {}ms",
                    type, userId, conversationParticipants.size(), System.currentTimeMillis() - startTime);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", responseDTO,
                    "existing", false,
                    "type", type,
                    "message", "Tạo thành công cuộc họp"
            ));

        } catch (Exception e) {
            log.error("Error creating conversation for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Không thể tạo cuộc trò chuyện: " + e.getMessage()
            ));
        }
    }

    /**
     * Kiểm tra conversation private đã tồn tại chưa
     */
    private ConversationEntity checkExistingPrivateConversation(Long user1Id, Long user2Id) {
        // Thử lấy từ cache trước
        String cacheKey = generateDirectConversationKey(user1Id, user2Id);
        ConversationDTO cached = getConversationFromCache(cacheKey);
        if (cached != null) {
            return conversationRepository.findById(cached.getConversationID()).orElse(null);
        }

        // Tìm trong database
        ConversationEntity conversation = conversationRepository.findPrivateConversationBetweenUsers(user1Id, user2Id);

        if (conversation != null) {
            // Cache kết quả
            cacheDirectConversation(user1Id, user2Id, conversation);
        }

        return conversation;
    }

    /**
     * Tìm kiếm user
     */
    @Override
    public ResponseEntity<Object> searchUser(Long userId, String query) {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        CompletableFuture<List<UserSuggestionDTO>> getUserByUsername =
                CompletableFuture.supplyAsync(() -> searchByUsername(query), executor);
        CompletableFuture<List<UserSuggestionDTO>> getUserByFullname =
                CompletableFuture.supplyAsync(() -> searchByFullname(query), executor);
        CompletableFuture<List<UserSuggestionDTO>> getUserBySchool =
                CompletableFuture.supplyAsync(() -> searchBySchool(query), executor);

        CompletableFuture<Void> allDone = CompletableFuture.allOf(
                getUserByUsername, getUserByFullname, getUserBySchool
        );

        List<UserSuggestionDTO> combinedResults = allDone.thenApply(v -> Stream.of(
                        getUserByUsername, getUserByFullname, getUserBySchool)
                .map(this::safeJoin)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList())
        ).join();

        executor.shutdown();

        return ResponseEntity.ok(Map.of("data", combinedResults, "success", true));
    }

    // ============ PRIVATE METHODS - CACHE ============

    private List<ConversationDTO> getAllConversationsWithFallback(Long currentUserId) {
        try {
            List<ConversationDTO> cached = getConversationsFromCache(currentUserId);
            if (cached != null && !cached.isEmpty()) {
                return cached;
            }
        } catch (Exception e) {
            log.warn("Cache error for user {}, falling back to DB: {}", currentUserId, e.getMessage());
        }

        List<ConversationDTO> conversations = getConversationsFromDatabase(currentUserId);
        cacheConversationsAsync(currentUserId, conversations);
        return conversations;
    }

    @SuppressWarnings("unchecked")
    private List<ConversationDTO> getConversationsFromCache(Long userId) {
        try {
            String key = USER_CONVERSATIONS_KEY_PREFIX + userId;
            Object cached = conversationRedisTemplate.opsForValue().get(key);
            return (cached instanceof List) ? (List<ConversationDTO>) cached : null;
        } catch (Exception e) {
            throw new RuntimeException("Cache error", e);
        }
    }

    private void cacheConversationsAsync(Long userId, List<ConversationDTO> conversations) {
        CompletableFuture.runAsync(() -> {
            try {
                String key = USER_CONVERSATIONS_KEY_PREFIX + userId;
                conversationRedisTemplate.opsForValue().set(key, conversations, CACHE_TTL);
                log.debug("Cached {} conversations for user {}", conversations.size(), userId);
            } catch (Exception e) {
                log.warn("Failed to cache conversations for user {}: {}", userId, e.getMessage());
            }
        });
    }

    private void cacheConversation(ConversationEntity conversation, List<UserSuggestionDTO> participants) {
        try {
            String key = CONVERSATION_KEY_PREFIX + conversation.getConversationID();
            ConversationDTO cacheDTO = toConversationDTO(conversation, participants);
            conversationRedisTemplate.opsForValue().set(key, cacheDTO, CACHE_TTL);
            log.debug("Cached conversation: {}", key);
        } catch (Exception e) {
            log.warn("Failed to cache conversation: {}", e.getMessage());
        }
    }

    private void cacheDirectConversation(Long user1Id, Long user2Id, ConversationEntity conversation) {
        try {
            String key = generateDirectConversationKey(user1Id, user2Id);
            conversationRedisTemplate.opsForValue().set(key, conversation.getConversationID(), CACHE_TTL);
        } catch (Exception e) {
            log.warn("Failed to cache direct conversation: {}", e.getMessage());
        }
    }

    private ConversationDTO getConversationFromCache(String key) {
        try {
            Object cached = conversationRedisTemplate.opsForValue().get(key);
            if (cached instanceof Long) {
                // Nếu cache conversation ID, lấy conversation từ cache hoặc DB
                Long conversationId = (Long) cached;
                String convKey = CONVERSATION_KEY_PREFIX + conversationId;
                Object convCached = conversationRedisTemplate.opsForValue().get(convKey);
                return (convCached instanceof ConversationDTO) ? (ConversationDTO) convCached : null;
            }
            return (cached instanceof ConversationDTO) ? (ConversationDTO) cached : null;
        } catch (Exception e) {
            return null;
        }
    }

    private void evictUserConversationsCache(List<Long> userIds) {
        userIds.parallelStream().forEach(userId -> {
            try {
                String key = USER_CONVERSATIONS_KEY_PREFIX + userId;
                conversationRedisTemplate.delete(key);
                log.debug("Evicted user conversations cache: {}", userId);
            } catch (Exception e) {
                log.warn("Failed to evict cache for user {}: {}", userId, e.getMessage());
            }
        });
    }

    private String generateDirectConversationKey(Long user1Id, Long user2Id) {
        Long[] ids = {user1Id, user2Id};
        Arrays.sort(ids);
        return DIRECT_CONVERSATION_KEY_PREFIX + ids[0] + "_" + ids[1];
    }

    // ============ PRIVATE METHODS - DATABASE ============

    private List<ConversationDTO> getConversationsFromDatabase(Long currentUserId) {
        List<ConversationEntity> conversations = conversationRepository.findByParticipantUserId(currentUserId);

        return conversations.stream()
                .map(conv -> {
                    try {
                        List<UserSuggestionDTO> participants = getConversationParticipantsAsync(conv.getConversationID()).join();
                        return convertToConversationDTO(conv, participants, currentUserId);
                    } catch (Exception e) {
                        log.warn("Error converting conversation {}: {}", conv.getConversationID(), e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted((c1, c2) -> {
                    LocalDateTime t1 = c1.getLastMessageTime();
                    LocalDateTime t2 = c2.getLastMessageTime();
                    if (t1 == null && t2 == null) return 0;
                    if (t1 == null) return 1;
                    if (t2 == null) return -1;
                    return t2.compareTo(t1);
                })
                .collect(Collectors.toList());
    }

    private List<ConversationParticipantEntity> createParticipants(ConversationEntity conversation,
                                                                   List<UserEntity> friends, Long currentUserId) {
        List<ConversationParticipantEntity> participants = friends.stream()
                .map(friend -> {
                    ConversationParticipantEntity participant = new ConversationParticipantEntity();
                    participant.setUser(friend);
                    participant.setConversation(conversation);
                    participant.setJoinedAt(LocalDateTime.now());
                    return participant;
                })
                .collect(Collectors.toList());

        // Thêm current user
        UserEntity currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng hiện tại"));

        ConversationParticipantEntity currentUserParticipant = new ConversationParticipantEntity();
        currentUserParticipant.setUser(currentUser);
        currentUserParticipant.setConversation(conversation);
        currentUserParticipant.setJoinedAt(LocalDateTime.now());
        participants.add(currentUserParticipant);

        return participants;
    }

    // ============ PRIVATE METHODS - ASYNC OPERATIONS ============

    private CompletableFuture<List<UserSuggestionDTO>> getConversationParticipantsAsync(Long conversationId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Long> userIds = conversationParticipantRepository.findUserIdsByConversationId(conversationId);
                List<UserEntity> users = userRepository.findByIdIn(userIds);
                return users.stream()
                        .map(this::mapToUserSuggestionDTO)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.warn("Error getting participants for conversation {}: {}", conversationId, e.getMessage());
                return new ArrayList<>();
            }
        }, taskExecutor);
    }

    private CompletableFuture<List<UserEntity>> findUsersAsync(List<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        List<CompletableFuture<UserEntity>> futures = userIds.stream()
                .distinct()
                .map(this::findUserAsync)
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(future -> {
                            try {
                                return future.get();
                            } catch (Exception e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .exceptionally(ex -> {
                    log.error("Error in async user lookup: {}", ex.getMessage());
                    return new ArrayList<>();
                });
    }

    private CompletableFuture<UserEntity> findUserAsync(Integer userId) {
        return CompletableFuture.supplyAsync(() -> {
                    try {
                        return userRepository.findById(Long.valueOf(userId)).orElse(null);
                    } catch (Exception e) {
                        log.warn("Error finding user {}: {}", userId, e.getMessage());
                        return null;
                    }
                }, taskExecutor)
                .orTimeout(5, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    log.warn("Timeout or error finding user {}: {}", userId, ex.getMessage());
                    return null;
                });
    }

    // ============ PRIVATE METHODS - MAPPING & CONVERSION ============

    private ConversationDTO convertToConversationDTO(ConversationEntity conversation,
                                                     List<UserSuggestionDTO> participants,
                                                     Long currentUserId) {
        ConversationDTO dto = new ConversationDTO();
        dto.setConversationID(conversation.getConversationID());
        dto.setType(conversation.getType());
        dto.setParticipants(participants);

        // Xác định title và avatar dựa trên type và participants
        if ("private".equals(conversation.getType())) {
            setPrivateConversationInfo(dto, conversation, participants, currentUserId);
        } else {
            // Group conversation
            dto.setTitle(conversation.getTitle());
        }

        setLastMessageInfo(dto, conversation.getConversationID());
        setUnreadCount(dto, conversation.getConversationID(), currentUserId);

        return dto;
    }

    private void setPrivateConversationInfo(ConversationDTO dto, ConversationEntity conversation,
                                            List<UserSuggestionDTO> participants, Long currentUserId) {
        // Tìm người còn lại trong private conversation (không phải current user)
        Optional<UserSuggestionDTO> otherUser = participants.stream()
                .filter(p -> !p.getUserID().equals(currentUserId))
                .findFirst();

        if (otherUser.isPresent()) {
            UserSuggestionDTO user = otherUser.get();
            // Set title là tên của người kia
            dto.setTitle(user.getFullName());
            // Set avatar là avatar của người kia
            dto.setAvatar(user.getAvatar());
        } else {
            // Fallback: nếu không tìm thấy người kia (trường hợp lỗi)
            dto.setTitle(conversation.getTitle() != null ? conversation.getTitle() : "Private Chat");
        }
    }

    private ConversationDTO toConversationDTO(ConversationEntity conversation, List<UserSuggestionDTO> participants) {
        ConversationDTO dto = new ConversationDTO();
        dto.setConversationID(conversation.getConversationID());
        dto.setType(conversation.getType());
        dto.setParticipants(participants);
        dto.setLastMessageContent(null);
        dto.setLastMessageTime(null);
        dto.setUnreadCount(0);

        // Xác định title và avatar cho conversation mới
        if ("private".equals(conversation.getType()) && participants != null && !participants.isEmpty()) {
            // Private conversation: tìm người đầu tiên không phải người tạo
            participants.stream()
                    .filter(p -> !p.getUserID().equals(conversation.getUser().getUserID()))
                    .findFirst()
                    .ifPresent(otherUser -> {
                        dto.setTitle(otherUser.getFullName());
                        dto.setAvatar(otherUser.getAvatar());
                    });
        } else {
            // Group conversation hoặc fallback
            dto.setTitle(conversation.getTitle());
        }

        return dto;
    }

    private UserSuggestionDTO mapToUserSuggestionDTO(UserEntity user) {
        return modelMapper.map(user, UserSuggestionDTO.class);
    }

    private void setLastMessageInfo(ConversationDTO dto, Long conversationId) {
        try {
            Pageable pageable = (Pageable) PageRequest.of(0, 1);
            List<MessageEntity> messages = messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, (org.springframework.data.domain.Pageable) pageable);
            MessageEntity lastMessage = messages.isEmpty() ? null : messages.get(0);
            if (lastMessage != null) {
                dto.setLastMessageTime(lastMessage.getCreatedAt());
            }
        } catch (Exception e) {
            log.warn("Error getting last message for conversation {}: {}", conversationId, e.getMessage());
        }
    }

    private void setUnreadCount(ConversationDTO dto, Long conversationId, Long userId) {
        try {
            Integer unreadCount = conversationParticipantRepository.countUnreadMessages(conversationId, userId);
            dto.setUnreadCount(unreadCount != null ? unreadCount : 0);
        } catch (Exception e) {
            dto.setUnreadCount(0);
        }
    }

    private void setPrivateConversationAvatar(ConversationDTO dto, List<UserSuggestionDTO> participants, Long currentUserId) {
        participants.stream()
                .filter(p -> !p.getUserID().equals(currentUserId))
                .findFirst()
                .ifPresent(otherUser -> dto.setAvatar(otherUser.getAvatar()));
    }

    // ============ PRIVATE METHODS - SEARCH ============

    private List<UserSuggestionDTO> searchByUsername(String query) {
        List<UserEntity> entities = userRepository.findByUsername(query);
        return entities.stream()
                .map(user -> modelMapper.map(user, UserSuggestionDTO.class))
                .collect(Collectors.toList());
    }

    private List<UserSuggestionDTO> searchByFullname(String query) {
        List<UserEntity> entities = userRepository.findByFullName(query);
        return entities.stream()
                .map(user -> modelMapper.map(user, UserSuggestionDTO.class))
                .collect(Collectors.toList());
    }

    private List<UserSuggestionDTO> searchBySchool(String query) {
        List<UserEntity> entities = userRepository.findBySchool(query);
        return entities.stream()
                .map(user -> modelMapper.map(user, UserSuggestionDTO.class))
                .collect(Collectors.toList());
    }

    private <T> List<T> safeJoin(CompletableFuture<List<T>> future) {
        try {
            List<T> result = future.join();
            return result != null ? result : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ============ UTILITY METHODS ============

    public void stimulateDelay() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ============== TAO MESSAGE ============
    //Gọi POST
    @Override
    public ResponseEntity<Object> sendMessageToConversation(Long userId, Long conversationId, Map<String, Object> conversationData) {
        try {
            // Tạo tempMessageId duy nhất
            String tempMessageId = "temp_" + System.currentTimeMillis() + "_" +
                    UUID.randomUUID().toString().substring(0, 8);

            // Thêm tempMessageId và userId vào data gửi đến RabbitMQ
            conversationData.put("tempMessageId", tempMessageId);
            conversationData.put("userId", userId);
            conversationData.put("conversationId", conversationId);

            // Gửi đến RabbitMQ
            messageProducer.sendMessageTask(userId, conversationId, conversationData);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tin nhắn đang được xử lý");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Không thể gửi tin nhắn");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    //gọi GET
    @Override
    public ResponseEntity<Object> getMessages(Long userId, Long conversationId) {
        try {
            // Kiểm tra user có trong conversation không
            if (!conversationParticipantRepository.existsByConversationIdAndUserId(conversationId, userId)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "bạn không có quyền truy cập cuộc trò chuyện này");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }

            // Lấy tin nhắn từ Redis
            String conversationMessageKey = "conversation:" + conversationId + ":messages";
            List<Object> messageIds = messageRedisTemplate.opsForList().range(conversationMessageKey, 0, -1);

            List<Map<String, Object>> messages = new ArrayList<>();

            if (messageIds == null || messageIds.isEmpty()) {
                // Redis chưa có -> load từ DB
                return getMessagesFromDatabaseAndCache(userId, conversationId);
            }

            // Lấy chi tiết từng tin nhắn từ Redis
            for (Object messageIdObj : messageIds) {
                String messageId = (String) messageIdObj;

                // Bỏ qua các tin nhắn tạm (bắt đầu bằng "temp_")
                if (messageId.startsWith("temp_")) {
                    continue;
                }

                String messageDetailKey = "message:detail:" + messageId;
                String messageJson = (String) messageRedisTemplate.opsForValue().get(messageDetailKey);

                if (messageJson != null) {
                    try {
                        Map<String, Object> messageData = objectMapper.readValue(messageJson, Map.class);
                        messages.add(messageData);
                    } catch (Exception e) {
                        log.warn("lỗi parse JSON cho message {}: {}", messageId, e.getMessage());
                    }
                }
            }

            // Sắp xếp tin nhắn theo thời gian (từ cũ đến mới)
            messages.sort((a, b) -> {
                String timeA = (String) a.get("createdAt");
                String timeB = (String) b.get("createdAt");
                return timeA.compareTo(timeB);
            });

            // Lấy thông tin conversation
            ConversationEntity conversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new RuntimeException("không tìm thấy cuộc trò chuyện"));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", messages);
            response.put("pagination", Map.of(
                    "total", messages.size(),
                    "hasNext", false,
                    "hasPrevious", false
            ));
            response.put("conversationInfo", Map.of(
                    "conversationID", conversation.getConversationID(),
                    "title", conversation.getTitle(),
                    "type", conversation.getType()
            ));
            response.put("source", "redis");

            log.debug("lấy {} tin nhắn từ redis cho conversation {}", messages.size(), conversationId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("lỗi khi lấy tin nhắn từ redis cho conversation {}: {}", conversationId, e.getMessage());
            // Fallback: lấy từ database
            return getMessagesFromDatabaseAndCache(userId, conversationId);
        }
    }

    private ResponseEntity<Object> getMessagesFromDatabaseAndCache(Long userId, Long conversationId) {
        try {
            // Lấy tin nhắn từ database
            PageRequest pageable = PageRequest.of(0, 100, Sort.by("createdAt").descending());
            List<MessageEntity> messageEntities = messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable);

            // Đảo ngược để có thứ tự từ cũ đến mới
            Collections.reverse(messageEntities);

            List<Map<String, Object>> messages = new ArrayList<>();
            String conversationMessageKey = "conversation:" + conversationId + ":messages";

            // Cache từng tin nhắn vào Redis
            for (MessageEntity message : messageEntities) {
                try {
                    // Convert entity sang DTO
                    Map<String, Object> messageData = convertMessageToMap(message);
                    messages.add(messageData);

                    // Cache vào Redis
                    String messageDetailKey = "message:detail:" + message.getMessageID();
                    String messageJson = objectMapper.writeValueAsString(messageData);
                    messageRedisTemplate.opsForValue().set(messageDetailKey, messageJson);

                    // Thêm message ID vào list
                    messageRedisTemplate.opsForList().rightPush(conversationMessageKey, String.valueOf(message.getMessageID()));

                } catch (Exception e) {
                    log.warn("lỗi cache message {}: {}", message.getMessageID(), e.getMessage());
                }
            }

            // Giữ chỉ 100 tin nhắn mới nhất trong Redis
            messageRedisTemplate.opsForList().trim(conversationMessageKey, 0, 99);

            // Lấy thông tin conversation
            ConversationEntity conversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new RuntimeException("không tìm thấy cuộc trò chuyện"));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", messages);
            response.put("pagination", Map.of(
                    "total", messages.size(),
                    "hasNext", false,
                    "hasPrevious", false
            ));
            response.put("conversationInfo", Map.of(
                    "conversationID", conversation.getConversationID(),
                    "title", conversation.getTitle(),
                    "type", conversation.getType()
            ));
            response.put("source", "database");

            log.debug("lấy {} tin nhắn từ database và cache cho conversation {}", messages.size(), conversationId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("lỗi khi lấy tin nhắn từ database cho conversation {}: {}", conversationId, e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "không thể tải tin nhắn");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    private Map<String, Object> convertMessageToMap(MessageEntity message) {
        Map<String, Object> messageData = new HashMap<>();

        messageData.put("messageID", message.getMessageID());
        messageData.put("conversationID", message.getConversation().getConversationID());
        messageData.put("senderID", message.getSender().getUserID());
        messageData.put("senderName", message.getSender().getFullName());
        messageData.put("senderAvatar", message.getSender().getAvatar());
        messageData.put("content", message.getContent());
        messageData.put("type", message.getType());
        messageData.put("mediaUrl", message.getMediaUrl());
        messageData.put("isEdited", message.getIsEdited());
        messageData.put("isDeleted", message.getIsDeleted());

        // Đồng bộ format createdAt với Consumer (dùng LocalDateTime)
        if (message.getCreatedAt() instanceof LocalDateTime) {
            messageData.put("createdAt", message.getCreatedAt().toString());
        } else {
            messageData.put("createdAt", message.getCreatedAt().toString());
        }

        // Xử lý metadata/fileInfo nếu có
        if (message.getMediaUrl() != null) {
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("type", getFileTypeFromMediaUrl(message.getMediaUrl()));
            fileInfo.put("originalName", getFileNameFromUrl(message.getMediaUrl()));
            messageData.put("fileInfo", fileInfo);
        }

        return messageData;
    }

    private String getFileTypeFromMediaUrl(String mediaUrl) {
        if (mediaUrl == null) return "unknown";

        String extension = mediaUrl.substring(mediaUrl.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "jpg": case "jpeg": case "png": case "gif": case "webp":
                return "image";
            case "mp4": case "avi": case "mov": case "mkv":
                return "video";
            case "mp3": case "wav": case "ogg":
                return "audio";
            case "pdf":
                return "pdf";
            case "doc": case "docx":
                return "doc";
            default:
                return "file";
        }
    }

    private String getFileNameFromUrl(String url) {
        if (url == null) return "file";
        return url.substring(url.lastIndexOf("/") + 1);
    }

    // ================= XÓA MESSAGE =======================
    @Override
    public ResponseEntity<Object> deleteMessage(Long messageId, Long currentUserId, boolean deleteForEveryone) {
        try {
            //Tìm message
            MessageEntity message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tin nhắn"));

            //Kiểm tra quyền
            if (Boolean.TRUE.equals(deleteForEveryone)) {
                if (!message.getSender().getUserID().equals(currentUserId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of(
                                    "success", false,
                                    "error", "ACCESS_DENIED",
                                    "message", "Chỉ người gửi mới được xóa cho mọi người"
                            ));
                }
            }

            //Xử lý xóa
            message.setIsDeleted(true);
            message.setDeletedAt(LocalDateTime.now());
            MessageEntity savedMessage = messageRepository.save(message);

            //Xóa cache Redis
            deleteMessageFromRedis(messageId, message.getConversation().getConversationID());

            //Broadcast socket event
            broadcastDeleteEvent(message.getConversation().getConversationID(), messageId, deleteForEveryone, currentUserId);

            //Trả về response thành công
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", deleteForEveryone ? "Đã xóa tin nhắn cho mọi người" : "Đã xóa tin nhắn",
                    "data", Map.of(
                            "messageId", messageId,
                            "conversationId", message.getConversation().getConversationID(),
                            "deleteForEveryone", deleteForEveryone,
                            "deletedAt", LocalDateTime.now().toString()
                    )
            ));

        } catch (Exception e) {
            //Xử lý lỗi
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "error", e.getMessage(),
                            "message", "Không thể xóa tin nhắn"
                    ));
        }
    }

    private void deleteMessageFromRedis(Long messageId, Long conversationId) {
        try {
            String conversationMessageKey = "conversation:" + conversationId + ":messages";
            String messageDetailKey = "message:detail:" + messageId;

            // Xóa chi tiết tin nhắn
            messageRedisTemplate.delete(messageDetailKey);

            // Xóa message ID khỏi danh sách conversation
            messageRedisTemplate.opsForList().remove(conversationMessageKey, 1, String.valueOf(messageId));

            System.out.println("Đã xóa cache Redis cho message: " + messageId);

        } catch (Exception e) {
            System.err.println("Lỗi xóa cache Redis: " + e.getMessage());
        }
    }

    private void broadcastDeleteEvent(Long conversationId, Long messageId,
                                      Boolean deleteForEveryone, Long deletedBy) {
        try {
            Map<String, Object> deletePayload = new HashMap<>();
            deletePayload.put("type", "MESSAGE_DELETED");
            deletePayload.put("data", Map.of(
                    "messageId", messageId,
                    "conversationId", conversationId,
                    "deleteForEveryone", deleteForEveryone,
                    "deletedBy", deletedBy,
                    "deletedAt", LocalDateTime.now().toString(),
                    "timestamp", System.currentTimeMillis()
            ));

            chatSocketHandler.broadcastToConversation(conversationId, deletePayload);
            System.out.println("Đã broadcast delete event: " + messageId);

        } catch (Exception e) {
            System.err.println("Lỗi broadcast delete event: " + e.getMessage());
        }
    }

    //====================== CHỈNH SỬA TIN NHẮN ====================
    @Override
    public ResponseEntity<Object> updateMessage(Long messageId, Long userId, String content) {
        try {
            //  Tìm message
            MessageEntity message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tin nhắn"));

            //  Kiểm tra quyền - chỉ sender mới được sửa
            if (!message.getSender().getUserID().equals(userId)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Chỉ người gửi mới được chỉnh sửa tin nhắn");
                errorResponse.put("error", "ACCESS_DENIED");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }

            //  Kiểm tra loại tin nhắn - không cho sửa file
            if ("file".equals(message.getType())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Không thể chỉnh sửa tin nhắn file");
                errorResponse.put("error", "INVALID_MESSAGE_TYPE");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            //  Kiểm tra nội dung
            if (content == null || content.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Nội dung không được để trống");
                errorResponse.put("error", "EMPTY_CONTENT");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            //  Lưu nội dung cũ để rollback nếu cần
            String oldContent = message.getContent();
            Long conversationId = message.getConversation().getConversationID();

            try {
                //  Cập nhật message trong database
                message.setContent(content.trim());
                message.setIsEdited(true);
                message.setUpdatedAt(LocalDateTime.now());
                MessageEntity updatedMessage = messageRepository.save(message);

                //  Cập nhật Redis cache
                updateMessageInRedis(updatedMessage, conversationId, oldContent);

                //Broadcast socket event
                broadcastUpdateEvent(updatedMessage, conversationId);

                //Trả về response thành công
                Map<String, Object> responseData = convertMessageToMap(updatedMessage);

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Đã cập nhật tin nhắn");
                response.put("data", responseData);

                log.info("Đã cập nhật tin nhắn: {} bởi user: {}", messageId, userId);
                return ResponseEntity.ok(response);

            } catch (Exception e) {
                // Rollback nếu có lỗi
                rollbackMessageUpdate(message, oldContent);
                throw e;
            }

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "MESSAGE_NOT_FOUND");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            log.error("Lỗi khi cập nhật tin nhắn {}: {}", messageId, e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Không thể cập nhật tin nhắn");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    private void updateMessageInRedis(MessageEntity updatedMessage, Long conversationId, String oldContent) {
        try {
            String messageId = String.valueOf(updatedMessage.getMessageID());
            String messageDetailKey = "message:detail:" + messageId;
            String conversationMessageKey = "conversation:" + conversationId + ":messages";

            //Convert message thành Map
            Map<String, Object> messageData = convertMessageToMap(updatedMessage);

            // 2. Cập nhật chi tiết tin nhắn trong Redis
            String messageJson = objectMapper.writeValueAsString(messageData);
            messageRedisTemplate.opsForValue().set(messageDetailKey, messageJson);

            //Cập nhật trong danh sách messages của conversation
            //Tìm vị trí của message trong list
            Long listSize = messageRedisTemplate.opsForList().size(conversationMessageKey);
            if (listSize != null && listSize > 0) {
                for (long i = 0; i < listSize; i++) {
                    String currentMessageId = (String) messageRedisTemplate.opsForList().index(conversationMessageKey, i);
                    if (messageId.equals(currentMessageId)) {
                        // Đã tìm thấy, không cần làm gì vì detail đã được update
                        break;
                    }
                }
            }

            //Cập nhật last message cache nếu cần
            updateLastMessageCacheIfNeeded(updatedMessage, conversationId);

            log.debug("Đã cập nhật Redis cache cho message: {}", messageId);

        } catch (Exception e) {
            log.error("Lỗi cập nhật Redis cho message {}: {}", updatedMessage.getMessageID(), e.getMessage());
            throw new RuntimeException("Lỗi cache Redis");
        }
    }

    private void updateLastMessageCacheIfNeeded(MessageEntity message, Long conversationId) {
        try {
            String lastMessageKey = "conversation:" + conversationId + ":last_message";
            String lastMessageJson = (String) messageRedisTemplate.opsForValue().get(lastMessageKey);

            if (lastMessageJson != null) {
                Map<String, Object> lastMessage = objectMapper.readValue(lastMessageJson, Map.class);
                Long lastMessageId = ((Number) lastMessage.get("messageID")).longValue();

                // Nếu message được sửa là last message, update cache
                if (lastMessageId.equals(message.getMessageID())) {
                    Map<String, Object> updatedLastMessage = convertMessageToMap(message);
                    String updatedLastMessageJson = objectMapper.writeValueAsString(updatedLastMessage);
                    messageRedisTemplate.opsForValue().set(lastMessageKey, updatedLastMessageJson);

                    log.debug("Đã cập nhật last message cache cho conversation: {}", conversationId);
                }
            }
        } catch (Exception e) {
            log.warn("Lỗi cập nhật last message cache: {}", e.getMessage());
        }
    }
    private void rollbackMessageUpdate(MessageEntity message, String oldContent) {
        try {
            // Rollback database
            message.setContent(oldContent);
            message.setIsEdited(false);
            message.setUpdatedAt(null);
            messageRepository.save(message);

            // Rollback Redis
            String messageId = String.valueOf(message.getMessageID());
            String messageDetailKey = "message:detail:" + messageId;

            Map<String, Object> originalMessageData = convertMessageToMap(message);
            String originalMessageJson = objectMapper.writeValueAsString(originalMessageData);
            messageRedisTemplate.opsForValue().set(messageDetailKey, originalMessageJson);

            log.info("Đã rollback message {} về nội dung cũ", messageId);
        } catch (Exception e) {
            log.error("Lỗi rollback message {}: {}", message.getMessageID(), e.getMessage());
        }
    }
    private void broadcastUpdateEvent(MessageEntity message, Long conversationId) {
        try {
            Map<String, Object> updatePayload = new HashMap<>();
            updatePayload.put("type", "MESSAGE_UPDATED");
            updatePayload.put("data", Map.of(
                    "messageId", message.getMessageID(),
                    "conversationId", conversationId,
                    "content", message.getContent(),
                    "isEdited", true,
                    "editedAt", message.getUpdatedAt().toString(),
                    "timestamp", System.currentTimeMillis()
            ));

            // Gửi qua WebSocket
            chatSocketHandler.broadcastToConversation(conversationId, updatePayload);

            log.debug("Đã broadcast update event cho message: {}", message.getMessageID());

        } catch (Exception e) {
            log.error("Lỗi broadcast update event: {}", e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> sendMessageToConversationByFile(Long userId, Long conversationId, MultipartFile file) {
        try {
            messageFileProducer.sendUploadTask(userId, conversationId, file);
            return ResponseEntity.ok(Map.of("success", true, "message", "Tin nhắn đang được xủ lý"));
        } catch (Exception e) {
            throw new RuntimeException("can not create file");
        }
    }
}