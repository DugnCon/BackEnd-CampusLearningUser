package com.javaweb.service.impl.EventService;

import com.javaweb.entity.Event.EventEntity;
import com.javaweb.repository.IEventRepository;
import com.javaweb.service.IEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public class EventServiceImpl implements IEventService {
    @Autowired
    private IEventRepository eventRepository;
    @Override
    public ResponseEntity<Object> getAllEvent() {
        try {
            List<EventEntity> eventEntities = eventRepository.findAll();
            if(eventEntities.isEmpty()) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Không có sự kiện nào diễn ra", "event", eventEntities));
            }
            return ResponseEntity.ok(eventEntities);
        } catch (Exception e) {
            throw new RuntimeException(e + " error by getting event list");
        }
    }
    public ResponseEntity<Object> getEventPreview(Long eventId) {
        try {
            EventEntity eventEntity = eventRepository.getEventPreview(eventId);
            return ResponseEntity.ok(eventEntity);
        } catch (Exception e) {
            throw new RuntimeException(e +  " error by getting event preview");
        }
    }
}
