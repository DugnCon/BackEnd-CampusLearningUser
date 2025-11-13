package com.javaweb.repository;

import com.javaweb.entity.ChatAndCall.CallParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICallParticipantRepository extends JpaRepository<CallParticipantEntity, Long> {

    @Query("SELECT cp FROM CallParticipantEntity cp WHERE cp.call.callID = :callId and cp.user.UserID = :userId")
    Optional<CallParticipantEntity> findByCallAndUser(@Param("callId") Long callId,@Param("userId") Long userId);

    // Lấy tất cả participants của một call
    @Query("SELECT cp FROM CallParticipantEntity cp WHERE cp.call.callID = :callId")
    List<CallParticipantEntity> findByCallId(@Param("callId") Long callId);

    // Lấy participant đang active trong call
    @Query("SELECT cp FROM CallParticipantEntity cp WHERE cp.call.callID = :callId AND cp.status IN ('joined', 'calling')")
    List<CallParticipantEntity> findActiveParticipantsByCallId(@Param("callId") Long callId);

    // Cập nhật status của participant
    @Query("UPDATE CallParticipantEntity cp SET cp.status = :status WHERE cp.callParticipantID = :participantId")
    void updateParticipantStatus(@Param("participantId") Long participantId, @Param("status") String status);
}