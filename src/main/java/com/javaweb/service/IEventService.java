package com.javaweb.service;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface IEventService {
    ResponseEntity<Object> getAllEvent();
    ResponseEntity<Object> getEventPreview(Long eventId);
}
