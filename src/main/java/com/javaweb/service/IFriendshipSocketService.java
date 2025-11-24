// File: src/main/java/com/javaweb/service/IFriendshipSocketService.java
package com.javaweb.service;

public interface IFriendshipSocketService {
    void sendFriendRequest(Long requesterId, Long addresseeId);
    void acceptFriendRequest(Long requesterId, Long accepterId);
    void rejectFriendRequest(Long requesterId, Long rejecterId);
    void cancelFriendRequest(Long cancellerId, Long addresseeId);
    void removeFriend(Long removerId, Long removedId);
}