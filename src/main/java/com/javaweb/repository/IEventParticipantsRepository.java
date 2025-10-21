package com.javaweb.repository;

import com.javaweb.entity.Event.EventEntity;
import com.javaweb.entity.Event.EventParticipantsEntity;
import com.javaweb.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IEventParticipantsRepository extends JpaRepository<EventParticipantsEntity, Long> {
    @Query("select ep from EventParticipantsEntity ep join fetch ep.event e join fetch ep.user u where e.eventID = :eventId and u.UserID = :userId")
    EventParticipantsEntity getSingleEventParticipant(@Param("eventId") Long eventId, @Param("userId") Long userId);
    boolean existsByEventAndUser(EventEntity eventEntity, UserEntity userEntity);
}
