package com.javaweb.api.story;

import com.javaweb.model.dto.MyUserDetail;
import com.javaweb.service.FileStorageService;
import com.javaweb.service.IStoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/stories")
public class StoryAPI {
    @Autowired
    private IStoryService storyService;
    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<Object> getAllStories() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        return storyService.getAllStories(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createStory(
            @RequestParam(value = "media", required = false) MultipartFile mediaFile,
            @RequestParam("mediaType") String mediaType,
            @RequestParam(value = "textContent", required = false) String textContent,
            @RequestParam(value = "backgroundColor", required = false) String backgroundColor,
            @RequestParam(value = "fontStyle", required = false) String fontStyle) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

        return storyService.createStory(mediaFile, mediaType, textContent, backgroundColor, fontStyle, userId);
    }

    @PostMapping("/{storyId}/view")
    public ResponseEntity<?> viewStory(@PathVariable("storyId") Long storyId) {
        return null;
    }

    @GetMapping("/{storyId}/viewers")
    public ResponseEntity<?> getStoryViewers(@PathVariable("storyId") Long storyId) {
        return null;
    }

    @DeleteMapping("/{storyId}")
    public ResponseEntity<?> deleteStory(@PathVariable("storyId") Long storyId) {
        return null;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserStories(@PathVariable("userId") Long userId) {
        return null;
    }

    @PostMapping("/{storyId}/like")
    public ResponseEntity<?> likeStory(@PathVariable("storyId") Long storyId) {
        return null;
    }

    @PostMapping("/{storyId}/reply")
    public ResponseEntity<?> replyToStory(
            @PathVariable("storyId") Long storyId,
            @RequestBody Map<String,Object> replyRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        return storyService.replyStory(storyId, userId, replyRequest);
    }

    // Inner class for reply request
    public static class ReplyRequest {
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}