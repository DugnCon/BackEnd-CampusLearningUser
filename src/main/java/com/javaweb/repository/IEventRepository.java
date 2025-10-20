package com.javaweb.repository;

import com.javaweb.entity.Event.EventEntity;
import com.javaweb.entity.Event.EventPrizesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IEventRepository extends JpaRepository<EventEntity, Long> {
    @Query("select e from EventEntity e where eventID = :eventId")
    EventEntity getEventPreview(@Param("eventId") Long eventId);
}
