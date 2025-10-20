package com.javaweb.api.events;

import com.javaweb.service.IEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EventAPI {
    @Autowired
    private IEventService eventService;
    @GetMapping("/events")
    public ResponseEntity<Object> getAllEvent() {
        return eventService.getAllEvent();
    }
    @GetMapping("/events/{eventId}")
    public ResponseEntity<Object> getEventPreview(@PathVariable Long eventId) {
        return eventService.getEventPreview(eventId);
    }
}
