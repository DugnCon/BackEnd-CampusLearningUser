package com.javaweb.api.events;

import com.javaweb.model.dto.MyUserDetail;
import com.javaweb.service.IEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

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
    @PostMapping("/events/{eventId}/register")
    public ResponseEntity<Object> eventRegister(@PathVariable Long eventId, @RequestBody Map<String, Object> userData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

        return eventService.eventRegister(eventId, userId, userData);
    }

    @DeleteMapping("/events/{eventId}/register")
    public ResponseEntity<Object> eventCancelRegistration(@PathVariable Long eventId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

        return eventService.eventCancelRegistration(eventId, userId);
    }

    @GetMapping("/events/{eventId}/registration-status")
    public ResponseEntity<Object> eventRegistrationStatus(@PathVariable Long eventId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();;

        return eventService.eventRegistrationStatus(eventId, userId);
    }
}
