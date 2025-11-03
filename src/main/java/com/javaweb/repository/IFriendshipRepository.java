package com.javaweb.repository;

import com.javaweb.entity.Friend.FriendshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFriendshipRepository extends JpaRepository<FriendshipEntity, Long> {
    @Query("select fs from FriendshipEntity fs join fetch fs.user join fetch fs.friend where fs.user.UserID = :userId and fs.friend.UserID = :friendId")
    FriendshipEntity getFriendshipStatus(@Param("userId") Long userId, @Param("friendId") Long friendId);
    @Query("select fs from FriendshipEntity fs join fetch fs.user join fetch fs.friend where fs.user.UserID = :userId and fs.friend.UserID = :friendId")
    FriendshipEntity getSingleFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId);
    //Lấy danh sách bạn bè của chính user
    @Query("""
    SELECT fs FROM FriendshipEntity fs
    JOIN FETCH fs.user u
    JOIN FETCH fs.friend f
    WHERE (
        (u.UserID = :userId AND f.UserID != :userId) OR
        (f.UserID = :userId AND u.UserID != :userId)
    )
    AND fs.status = :status
    """)
    List<FriendshipEntity> getAllFriendshipAccepted(
            @Param("userId") Long userId,
            @Param("status") String status
    );
    //Lấy danh sách bạn bè mà user chưa xử lý
    @Query("select fs from FriendshipEntity fs join fetch fs.user join fetch fs.friend where fs.user.UserID = :userId and fs.status = :status")
    List<FriendshipEntity> getAllFriendshipPended(@Param("userId") Long userId, @Param("status") String status);
    //Lấy danh sách bạn bè chưa accept mình
    @Query("select fs from FriendshipEntity fs join fetch fs.user join fetch fs.friend where fs.friend.UserID = :userId and fs.status = :status")
    List<FriendshipEntity> getAllUserPending(@Param("userId") Long userId, @Param("status") String status);
    //Kiểm tra xem kết bạn chưa
    @Query("select fs from FriendshipEntity fs join fetch fs.user join fetch fs.friend where ( fs.friend.UserID = :friendId and fs.user.UserID = :userId) and ( fs.status = :status_1 or fs.status = :status_2 )")
    FriendshipEntity isFriend(@Param("friendId") Long friendId,@Param("userId") Long userId, @Param("status_1") String pending, @Param("status_2") String friend);
}
