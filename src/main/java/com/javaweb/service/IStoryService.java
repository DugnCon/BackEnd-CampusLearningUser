package com.javaweb.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public interface IStoryService {
    ResponseEntity<Object> createStory(MultipartFile mediaFile,String mediaType,String textContent,String backgroundColor,String fontStyle,Long userId) throws IOException;
    ResponseEntity<Object> getAllStories(Long userId);
    ResponseEntity<Object> replyStory(Long storyId, Long userId,Map<String,Object> replyRequest);
}
