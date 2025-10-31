package com.javaweb.repository;

import com.javaweb.entity.ChatAndCall.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IConversationRepository extends JpaRepository<ConversationEntity, Long> {
    // Lấy tất cả conversations mà user tham gia
    @Query("SELECT DISTINCT c FROM ConversationEntity c " +
            "JOIN c.conversationParticipant p " +
            "WHERE p.user.UserID = :userId AND c.isActive = true " +
            "ORDER BY c.lastMessageAt DESC NULLS LAST, c.createdAt DESC")
    List<ConversationEntity> findByParticipantUserId(@Param("userId") Long userId);

    // Tìm private conversation giữa 2 users
    @Query("SELECT c FROM ConversationEntity c " +
            "WHERE c.type = 'private' AND c.isActive = true " +
            "AND EXISTS (SELECT 1 FROM ConversationParticipantEntity p1 WHERE p1.conversation = c AND p1.user.UserID = :user1Id) " +
            "AND EXISTS (SELECT 1 FROM ConversationParticipantEntity p2 WHERE p2.conversation = c AND p2.user.UserID = :user2Id)")
    ConversationEntity findPrivateConversationBetweenUsers(@Param("user1Id") Long user1Id,
                                                           @Param("user2Id") Long user2Id);

    // Đếm số participants trong conversation
    @Query("SELECT COUNT(p) FROM ConversationParticipantEntity p WHERE p.conversation.conversationID = :conversationId")
    Long countParticipantsByConversationId(@Param("conversationId") Long conversationId);

    // Lấy conversations được tạo bởi user
    @Query("SELECT c FROM ConversationEntity c WHERE c.user.UserID = :userId AND c.isActive = true")
    List<ConversationEntity> findByCreatedBy(@Param("userId") Long userId);
}
