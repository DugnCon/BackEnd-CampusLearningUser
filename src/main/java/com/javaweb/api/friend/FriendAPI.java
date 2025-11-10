package com.javaweb.api.friend;

import com.javaweb.model.dto.MyUserDetail;
import com.javaweb.service.IFriendshipService;
import com.javaweb.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user/api")
public class FriendAPI {
    @Autowired
    private IUserService userService;
    @Autowired
    private IFriendshipService friendshipService;

    // === API HIỆN CÓ ===

    @GetMapping("/friendships")
    public ResponseEntity<Object> getCurrentUserFriendships() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) authentication.getPrincipal();

        Long userId = myUserDetail.getId();

        return friendshipService.getAllFriendships(userId);
    }

    //Lấy danh sách bạn bè của user đó (không phải user đang login)
    @GetMapping("/friendships/user/{userId}")
    public ResponseEntity<Object> getFriendsOfUserProfile(@PathVariable Long userId) {
        //return friendshipService.getProfileFriend(userId);
        return null;
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Object> getFriendsOfUser(@PathVariable Long userId) {
        return friendshipService.getProfileFriend(userId);
    }

    @GetMapping("/friendships/status/{friendId}")
    public ResponseEntity<Object> getFriendshipStatus(@PathVariable Long friendId) {
        Authentication auth  = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

        return friendshipService.getFriendshipStatus(userId, friendId);
    }

    @PostMapping("/friendships")
    public ResponseEntity<Object> sendFriendRequest(
            @RequestBody Map<String,Object> data) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

        return friendshipService.addFriend(userId, data);
    }

    @PutMapping("/friendships/{friendshipId}/accept")
    public ResponseEntity<Object> acceptFriendRequest(@PathVariable Long friendshipId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        return friendshipService.acceptFriend(friendshipId, userId);
    }

    @PutMapping("/friendships/{friendshipId}/reject")
    public ResponseEntity<Object> rejectFriendRequest(@PathVariable Long friendshipId, Authentication authentication) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        return friendshipService.rejectFriend(friendshipId, userId);
    }

    @DeleteMapping("/friendships/{friendshipId}")
    public ResponseEntity<Object> cancelOrRemoveFriendship(@PathVariable Long friendshipId, Authentication authentication) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        return friendshipService.deleteFrienḍ(friendshipId, userId);
    }

    @GetMapping("/friendships/suggestions/random")
    public ResponseEntity<Object> getRandomFriendSuggestions(Authentication authentication) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

        return userService.findAllUser(userId);
    }

    // === API CÒN THIẾU - THÊM VÀO ===

    // 1. API tìm kiếm user (cho chức năng search)
    @GetMapping("/users/search")
    public ResponseEntity<Object> searchUsers(@RequestParam String query) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        return friendshipService.searchUser(userId,query );
    }

    // 2. API lấy danh sách lời mời đã gửi (riêng biệt)
    @GetMapping("/friendships/sent-requests")
    public ResponseEntity<Object> getSentFriendRequests() {
        return ResponseEntity.ok(null);
    }

    // 3. API lấy danh sách lời mời đang chờ (riêng biệt)
    @GetMapping("/friendships/pending-requests")
    public ResponseEntity<Object> getPendingFriendRequests() {
        return ResponseEntity.ok(null);
    }

    // 4. API lấy danh sách bạn bè (riêng biệt)
    @GetMapping("/friendships/friends")
    public ResponseEntity<Object> getFriends() {
        return ResponseEntity.ok(null);
    }

    // 5. API chấp nhận lời mời theo userId (thay vì friendshipId)
    @PutMapping("/friendships/{userId}/accept-by-user")
    public ResponseEntity<Object> acceptFriendRequestByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(null);
    }

    // 6. API từ chối lời mời theo userId (thay vì friendshipId)
    @PutMapping("/friendships/{userId}/reject-by-user")
    public ResponseEntity<Object> rejectFriendRequestByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(null);
    }

    // 7. API hủy kết bạn/lời mời theo userId (thay vì friendshipId)
    @DeleteMapping("/friendships/user/{userId}")
    public ResponseEntity<Object> cancelOrRemoveFriendshipByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(null);
    }

    // 8. API kiểm tra xem đã gửi lời mời kết bạn chưa
    @GetMapping("/friendships/check-request/{friendId}")
    public ResponseEntity<Object> checkFriendRequest(@PathVariable Long friendId) {
        return ResponseEntity.ok(null);
    }

    // 9. API lấy số lượng thông báo bạn bè
    @GetMapping("/friendships/notifications/count")
    public ResponseEntity<Object> getFriendNotificationsCount() {
        return ResponseEntity.ok(null);
    }
}