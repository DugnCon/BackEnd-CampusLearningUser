package com.javaweb.repository;

import com.javaweb.entity.ChatAndCall.CallEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICallRepository extends JpaRepository<CallEntity, Long> {

    // Tìm call bằng callId (UUID)
    Optional<CallEntity> findByCallID(Long callId);

    // Lấy danh sách call đang active
    @Query("SELECT c FROM CallEntity c WHERE c.status IN ('initiated', 'ringing', 'active')")
    List<CallEntity> findActiveCalls();

    // Lấy call theo conversation
    @Query("SELECT c FROM CallEntity c WHERE c.conversation.conversationID = :conversationId AND c.status IN ('initiated', 'ringing', 'active')")
    List<CallEntity> findActiveCallsByConversation(@Param("conversationId") String conversationId);

    // Lấy call history của user
    @Query("SELECT c FROM CallEntity c WHERE c.initiator.UserID = :userId OR EXISTS (SELECT cp FROM c.callParticipant cp WHERE cp.user.UserID = :userId)")
    List<CallEntity> findCallHistoryByUser(@Param("userId") String userId);
}