package com.javaweb.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface IFriendshipService {
    ResponseEntity<Object> addFriend(Long userId, Map<String,Object> data);
    ResponseEntity<Object> acceptFriend(Long friendId,Long userId);
    ResponseEntity<Object> rejectFriend(Long friendId,Long userId);
    ResponseEntity<Object> deleteFrienḍ(Long friendId,Long userId);
    ResponseEntity<Object> getFriendshipStatus(Long userId, Long friendId);
    ResponseEntity<Object> getProfileFriend(Long userId);
    ResponseEntity<Object> getAllFriendships(Long userId);
    ResponseEntity<Object> searchUser(Long userId, String query);
}
