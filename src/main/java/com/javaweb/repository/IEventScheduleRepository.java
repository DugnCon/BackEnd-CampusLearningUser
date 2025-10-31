package com.javaweb.repository;

import com.javaweb.entity.Event.EventScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface IEventScheduleRepository extends JpaRepository<EventScheduleEntity, Long> {
    //Set<EventScheduleEntity> findByEventSchedule_EventID(Long eventId);
}
