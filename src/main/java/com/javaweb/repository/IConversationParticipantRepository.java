package com.javaweb.repository;

import com.javaweb.entity.ChatAndCall.ConversationParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IConversationParticipantRepository extends JpaRepository<ConversationParticipantEntity, Long> {

    // Lấy tất cả user IDs trong conversation - SỬA user.UserID thành user.userID
    @Query("SELECT p.user.UserID FROM ConversationParticipantEntity p WHERE p.conversation.conversationID = :conversationId")
    List<Long> findUserIdsByConversationId(@Param("conversationId") Long conversationId);

    @Query("SELECT p.user.UserID FROM ConversationParticipantEntity p WHERE p.conversation.conversationID = :conversationId and p.user.UserID != :userId")
    List<Long> findAllUserIdsByConversationId(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    // Kiểm tra user có trong conversation không - SỬA user.UserID thành user.userID
    @Query("SELECT COUNT(p) > 0 FROM ConversationParticipantEntity p " +
            "WHERE p.conversation.conversationID = :conversationId AND p.user.UserID = :userId")
    boolean existsByConversationIdAndUserId(@Param("conversationId") Long conversationId,
                                            @Param("userId") Long userId);

    // Đếm unread messages cho user trong conversation - SỬA user.UserID thành user.userID
    @Query("SELECT COUNT(m) FROM MessageEntity m " +
            "WHERE m.conversation.conversationID = :conversationId " +
            "AND m.createdAt > COALESCE((SELECT p.joinedAt FROM ConversationParticipantEntity p " +
            "WHERE p.conversation.conversationID = :conversationId AND p.user.UserID = :userId), m.createdAt) " +
            "AND m.isDeleted = false")
    Integer countUnreadMessages(@Param("conversationId") Long conversationId,
                                @Param("userId") Long userId);

    // Lấy participant by conversation và user - SỬA user.UserID thành user.userID
    @Query("SELECT p FROM ConversationParticipantEntity p " +
            "WHERE p.conversation.conversationID = :conversationId AND p.user.UserID = :userId")
    Optional<ConversationParticipantEntity> findByConversationIdAndUserId(@Param("conversationId") Long conversationId,
                                                                          @Param("userId") Long userId);

    // Lấy tất cả participants của conversation
    @Query("SELECT p FROM ConversationParticipantEntity p WHERE p.conversation.conversationID = :conversationId")
    List<ConversationParticipantEntity> findByConversationId(@Param("conversationId") Long conversationId);

    // Cập nhật last read message - SỬA user.UserID thành user.userID
    @Modifying
    @Query("UPDATE ConversationParticipantEntity p SET p.lastReadMessageID = :lastReadMessageID " +
            "WHERE p.conversation.conversationID = :conversationId AND p.user.UserID = :userId")
    void updateLastReadMessage(@Param("conversationId") Long conversationId,
                               @Param("userId") Long userId,
                               @Param("lastReadMessageID") Long lastReadMessageID);

    // THÊM: Query methods đơn giản không cần @Query
    //List<ConversationParticipantEntity> findByConversation_ConversationID(Long conversationId);
    //Optional<ConversationParticipantEntity> findByConversation_ConversationIDAndUser_UserID(Long conversationId, Long userId);
    //boolean existsByConversation_ConversationIDAndUser_UserID(Long conversationId, Long userId);
}