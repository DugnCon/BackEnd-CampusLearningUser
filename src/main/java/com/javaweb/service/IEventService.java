package com.javaweb.service;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Service
public interface IEventService {
    ResponseEntity<Object> getAllEvent();
    ResponseEntity<Object> getEventPreview(Long eventId);
    ResponseEntity<Object> eventRegister(Long eventId, Long userId, Map<String,Object> userData);
    ResponseEntity<Object> eventRegistrationStatus(Long eventId, Long userId);
    ResponseEntity<Object> eventCancelRegistration(Long eventId, Long userId);
}
