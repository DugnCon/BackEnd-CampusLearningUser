package com.javaweb.repository;

import com.javaweb.entity.ChatAndCall.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface IMessageRepository extends JpaRepository<MessageEntity, Long> {

    @Query("SELECT m FROM MessageEntity m JOIN FETCH m.conversation c " +
            "WHERE c.conversationID = :conversationId AND m.isDeleted = false ORDER BY m.createdAt DESC")
    List<MessageEntity> findByConversationIdOrderByCreatedAtDesc(@Param("conversationId") Long conversationId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM MessageEntity m WHERE m.conversation.conversationID = :conversationId AND m.isDeleted = false")
    Long countByConversationId(@Param("conversationId") Long conversationId);

    @Query("SELECT m FROM MessageEntity m " +
            "WHERE m.conversation.conversationID = :conversationId " +
            "AND m.messageID > COALESCE((SELECT p.lastReadMessageID FROM ConversationParticipantEntity p " +
            "WHERE p.conversation.conversationID = :conversationId AND p.user.UserID = :userId), 0) " +
            "AND m.isDeleted = false ORDER BY m.createdAt ASC")
    List<MessageEntity> findUnreadMessages(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    @Query("SELECT m FROM MessageEntity m WHERE m.sender.UserID = :senderId AND m.isDeleted = false")
    List<MessageEntity> findBySenderId(@Param("senderId") Long senderId);

    @Modifying
    @Query("UPDATE ConversationEntity c SET c.lastMessageAt = :lastMessageAt WHERE c.conversationID = :conversationId")
    void updateConversationLastMessageTime(@Param("conversationId") Long conversationId,
                                           @Param("lastMessageAt") LocalDateTime lastMessageAt);

    @Query(value = "SELECT * FROM messages WHERE ConversationID = :conversationId AND IsDeleted = false ORDER BY CreatedAt DESC LIMIT 1", nativeQuery = true)
    MessageEntity findLastMessageByConversationId(@Param("conversationId") Long conversationId);

    // thêm method mới để lấy tin nhắn với pagination
    @Query("SELECT m FROM MessageEntity m WHERE m.conversation.conversationID = :conversationId AND m.isDeleted = false ORDER BY m.createdAt DESC")
    List<MessageEntity> findMessagesByConversationId(@Param("conversationId") Long conversationId, Pageable pageable);

    // lấy tin nhắn sau 1 thời điểm cụ thể
    @Query("SELECT m FROM MessageEntity m WHERE m.conversation.conversationID = :conversationId AND m.createdAt > :afterTime AND m.isDeleted = false ORDER BY m.createdAt ASC")
    List<MessageEntity> findMessagesAfterTime(@Param("conversationId") Long conversationId,
                                              @Param("afterTime") LocalDateTime afterTime);

    // đánh dấu tin nhắn là đã xóa
    @Modifying
    @Query("UPDATE MessageEntity m SET m.isDeleted = true, m.deletedAt = :deletedAt WHERE m.messageID = :messageId")
    void softDeleteMessage(@Param("messageId") Long messageId, @Param("deletedAt") LocalDateTime deletedAt);

    // tìm tin nhắn chưa đọc cho nhiều conversation
    @Query("SELECT m.conversation.conversationID, COUNT(m) FROM MessageEntity m " +
            "WHERE m.conversation.conversationID IN :conversationIds " +
            "AND m.messageID > COALESCE((SELECT p.lastReadMessageID FROM ConversationParticipantEntity p " +
            "WHERE p.conversation.conversationID = m.conversation.conversationID AND p.user.UserID = :userId), 0) " +
            "AND m.isDeleted = false " +
            "GROUP BY m.conversation.conversationID")
    List<Object[]> countUnreadMessagesByConversationIds(@Param("conversationIds") List<Long> conversationIds,
                                                        @Param("userId") Long userId);
}