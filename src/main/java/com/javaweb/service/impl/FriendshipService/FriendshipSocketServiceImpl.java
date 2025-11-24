package com.javaweb.service.impl.FriendshipService;// File: src/main/java/com/javaweb/service/impl/FriendshipSocketImpl.java


import com.javaweb.entity.Friend.FriendshipEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.repository.IFriendshipRepository;
import com.javaweb.repository.IUserRepository;
import com.javaweb.service.IFriendshipSocketService;
import com.javaweb.utils.MapUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FriendshipSocketServiceImpl implements IFriendshipSocketService {
    @Autowired
    private IFriendshipRepository friendshipRepository;
    @Autowired
    private IUserRepository userRepository;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendFriendRequest(Long requesterId, Long addresseeId) {
        // TODO: logic gửi lời mời (tạo Friendship status = PENDING)
        ExecutorService executor = Executors.newFixedThreadPool(2);

        CompletableFuture<UserEntity> userAsync = CompletableFuture.supplyAsync(() -> getUserAndFriend(requesterId), executor);
        CompletableFuture<UserEntity> friendAsync = CompletableFuture.supplyAsync(() -> getUserAndFriend(addresseeId), executor);
        CompletableFuture.allOf(userAsync, friendAsync).join();

        UserEntity user = userAsync.join();
        UserEntity friend = friendAsync.join();

        FriendshipEntity friendshipEntity = new FriendshipEntity();
        friendshipEntity.setFriend(friend);
        friendshipEntity.setUser(user);
        friendshipEntity.setStatus("pending");

        friendshipRepository.save(friendshipEntity);
    }

    public UserEntity getUserAndFriend(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("can not find friend in friendship"));
    }

    @Override
    public void acceptFriendRequest(Long requesterId, Long accepterId) {
        // TODO: tìm friendship và set status = ACCEPTED
        //FriendshipEntity friendshipEntity = friendshipRepository.findById(friendId).orElseThrow(() -> new RuntimeException("not found friendship"));
        FriendshipEntity friendshipEntity = friendshipRepository.AcceptOrRejectFriend(accepterId, requesterId);
        friendshipEntity.setStatus("accepted");
        friendshipRepository.save(friendshipEntity);
    }

    @Override
    public void rejectFriendRequest(Long requesterId, Long rejecterId) {
        // TODO: tìm và xóa lời mời (hoặc set status = REJECTED)
        //FriendshipEntity friendshipEntity = friendshipRepository.findById(friendId).orElseThrow(() -> new RuntimeException("not found friendship"));
        FriendshipEntity friendshipEntity = friendshipRepository.AcceptOrRejectFriend(rejecterId, rejecterId);;
        friendshipEntity.setStatus("rejected");
        friendshipRepository.save(friendshipEntity);
    }

    @Override
    public void cancelFriendRequest(Long cancellerId, Long addresseeId) {
        // TODO: xóa lời mời đang pending
        FriendshipEntity friendshipEntity = friendshipRepository.DeleteFriend(cancellerId, addresseeId);;
        friendshipRepository.deleteById(friendshipEntity.getFriendshipID());
    }

    @Override
    public void removeFriend(Long removerId, Long removedId) {
        // TODO: xóa cả 2 chiều (nếu có)
        FriendshipEntity friendshipEntity = friendshipRepository.DeleteFriend(removerId, removedId);;
        friendshipRepository.deleteById(friendshipEntity.getFriendshipID());
    }
}