package com.javaweb.service.impl.FriendshipService;

import com.javaweb.entity.Friend.FriendshipEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.model.dto.UserDTO;
import com.javaweb.model.dto.UserSuggestions.UserSuggestionDTO;
import com.javaweb.repository.IFriendshipRepository;
import com.javaweb.repository.IUserRepository;
import com.javaweb.service.IFriendshipService;
import com.javaweb.utils.MapUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FriendshipServiceImpl implements IFriendshipService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IFriendshipRepository friendshipRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Object> addFriend(Long userId, Map<String,Object> data) {
        Long friendId = MapUtils.getObject(data, "addresseeId", Long.class);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("can not find user in friendship"));
        assert friendId != null;
        UserEntity friend = userRepository.findById(friendId).orElseThrow(() -> new RuntimeException("can not find friend in friendship"));
        try {
            FriendshipEntity friendshipEntity = new FriendshipEntity();
            friendshipEntity.setFriend(friend);
            friendshipEntity.setUser(user);
            friendshipEntity.setStatus("pending");
            friendshipRepository.save(friendshipEntity);
            return ResponseEntity.ok(Map.of("friendship", friendshipEntity, "success", true, "user", user.getUserID(),"friend", friend.getUserID()));
        } catch (Exception e) {
            throw new RuntimeException(e + " can not addFriend");
        }
    }
    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> acceptFriend(Long friendId, Long userId) {
        //FriendshipEntity friendshipEntity = friendshipRepository.findById(friendId).orElseThrow(() -> new RuntimeException("not found friendship"));
        FriendshipEntity friendshipEntity = friendshipRepository.getSingleFriendship(friendId, userId);
        friendshipEntity.setStatus("accepted");
        friendshipRepository.save(friendshipEntity);
        return ResponseEntity.ok(Map.of("success", true));
    }
    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> rejectFriend(Long friendId , Long userId) {
        //FriendshipEntity friendshipEntity = friendshipRepository.findById(friendId).orElseThrow(() -> new RuntimeException("not found friendship"));
        FriendshipEntity friendshipEntity = friendshipRepository.getSingleFriendship(friendId, userId);;
        friendshipEntity.setStatus("pending");
        friendshipRepository.save(friendshipEntity);
        return ResponseEntity.ok(Map.of("success", true));
    }
    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> deleteFrienḍ(Long friendId, Long userId) {
        FriendshipEntity friendshipEntity = friendshipRepository.getSingleFriendship(userId, friendId);;
        friendshipRepository.deleteById(friendshipEntity.getFriendshipID());
        return ResponseEntity.ok(Map.of("success", true));
    }
    @Override
    public ResponseEntity<Object> getProfileFriend(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("can not find user's profile"));
        return ResponseEntity.ok(user);
    }
    @Override
    public ResponseEntity<Object> getFriendshipStatus(Long userId, Long friendId) {
        FriendshipEntity friendshipEntity = friendshipRepository.getFriendshipStatus(userId, friendId);
        return ResponseEntity.ok(Map.of("status", friendshipEntity.getStatus()));
    }
    @Override
    public ResponseEntity<Object> getAllFriendships(Long userId) {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        CompletableFuture<List<UserSuggestionDTO>> friendsFuture = CompletableFuture.supplyAsync(() -> getFriendAcceptedOfUser(userId), executor);
        CompletableFuture<List<UserSuggestionDTO>> pendingFuture = CompletableFuture.supplyAsync(() -> getFriendPendingOfUser(userId), executor);
        CompletableFuture<List<UserSuggestionDTO>> sentFuture = CompletableFuture.supplyAsync(() -> getUserPendingOfUser(userId), executor);

        CompletableFuture.allOf(friendsFuture,pendingFuture,sentFuture).join();

        executor.shutdown();

        return ResponseEntity.ok(Map.of(
                "friends", friendsFuture.join(),
                "pendingRequests", sentFuture.join(),
                "sentRequests", pendingFuture.join(),
                "success", true
        ));
    }

    public List<UserSuggestionDTO> getFriendAcceptedOfUser(Long userId) {
        List<FriendshipEntity> entities = friendshipRepository.getAllFriendshipAccepted(userId, "accepted");
        List<UserSuggestionDTO> dtos = entities.stream()
                .map(fs -> toUserDTO(fs, userId))
                .toList();
        return dtos;
    }

    public List<UserSuggestionDTO> getFriendPendingOfUser(Long userId) {
        List<FriendshipEntity> entities = friendshipRepository.getAllFriendshipPended(userId, "pending");
        List<UserSuggestionDTO> dtos = entities.stream()
                .map(fs -> toUserDTO(fs, userId))
                .toList();
        return dtos ;
    }


    public List<UserSuggestionDTO> getUserPendingOfUser(Long userId) {
        List<FriendshipEntity> entities = friendshipRepository.getAllUserPending(userId, "pending");
        List<UserSuggestionDTO> dtos = entities.stream()
                .map(fs -> toUserDTO(fs, userId))
                .toList();
        return dtos;
    }

    private UserSuggestionDTO toUserDTO(FriendshipEntity fs, Long currentUserId) {
        UserEntity target = fs.getUser().getUserID().equals(currentUserId)
                ? fs.getFriend()
                : fs.getUser();

        UserSuggestionDTO dto = modelMapper.map(target, UserSuggestionDTO.class);
        dto.setFriendshipID(fs.getFriendshipID());
        return dto;
    }


    @Override
    public ResponseEntity<Object> searchUser(Long userId, String query) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        /**
         * Cái supplyAsync: Chạy một tác vụ (một đoạn code) bất đồng bộ ở một luồng khác mà không chặn (block) luồng chính.
         * **/
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
                        getUserByUsername, getUserByFullname, getUserBySchool)// Gom 3 kết quả xử lý bất đồng bộ thành 1 luồng để xử lý
                .map(this::safeJoin)// Với mỗi phần tử trong Stream ta gọi để tìm cái nào null
                .flatMap(Collection::stream)// sẽ trải phẳng các List đó thành một luồng duy nhất
                .distinct()// Loại bỏ các phần tử trùng lặp trong Stream
                .collect(Collectors.toList())// Thu thập
        ).join();

        executor.shutdown();

        return ResponseEntity.ok(combinedResults);
    }

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
    public void stimulateDelay() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
