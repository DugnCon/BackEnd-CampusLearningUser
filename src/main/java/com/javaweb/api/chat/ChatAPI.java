package com.javaweb.api.chat;

import com.javaweb.model.dto.MyUserDetail;
import com.javaweb.service.IConservationService;
import com.javaweb.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatAPI {
    @Autowired
    private IConservationService conservationService;

    // 1. CONVERSATIONS
    @GetMapping("/conversations")
    public ResponseEntity<Object> getConversations() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        return conservationService.getAllConversation(userId);
    }

    @PostMapping("/conversations")
    public ResponseEntity<Object> createConversation(@RequestBody Map<String, Object> conversationData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

        return conservationService.createConversation(userId, conversationData);
    }

    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<Object> getConversationDetails(@PathVariable String conversationId) {
        return ResponseEntity.ok(null);
    }

    @PutMapping("/conversations/{conversationId}")
    public ResponseEntity<Object> updateConversation(@PathVariable String conversationId, @RequestBody Map<String, Object> updateData) {
        return ResponseEntity.ok(null);
    }

    @PostMapping("/conversations/{conversationId}/leave")
    public ResponseEntity<Object> leaveConversation(@PathVariable String conversationId) {
        return ResponseEntity.ok(null);
    }

    @PostMapping("/conversations/{conversationId}/mute")
    public ResponseEntity<Object> toggleMuteConversation(@PathVariable String conversationId, @RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(null);
    }

    @PostMapping("/conversations/{conversationId}/read")
    public ResponseEntity<Object> markMessagesAsRead(@PathVariable String conversationId, @RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(null);
    }

    // 2. MESSAGES
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<Object> getMessages(@PathVariable Long conversationId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        return conservationService.getMessages(userId, conversationId);
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<Object> sendMessageToConversation(@PathVariable Long conversationId, @RequestBody Map<String, Object> messageData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        return conservationService.sendMessageToConversation(userId, conversationId, messageData);
    }

    @PostMapping("/messages")
    public ResponseEntity<Object> sendMessage(@RequestBody Map<String, Object> messageData) {
        return ResponseEntity.ok(null);
    }

    @PutMapping("/messages/{messageId}")
    public ResponseEntity<Object> updateMessage(@PathVariable Long messageId, @RequestBody Map<String,Object> messageData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();
        String content = MapUtils.getObject(messageData, "content", String.class);
        Long userId = myUserDetail.getId();
        return conservationService.updateMessage(messageId, userId, content);
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Object> deleteMessage(@PathVariable Long messageId, @RequestParam boolean deleteForEveryone) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        try {
            return conservationService.deleteMessage(messageId, userId, deleteForEveryone);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // 3. PARTICIPANTS
    @PostMapping("/conversations/{conversationId}/participants")
    public ResponseEntity<Object> addParticipants(@PathVariable String conversationId, @RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/conversations/{conversationId}/participants/{userId}")
    public ResponseEntity<Object> removeParticipant(@PathVariable String conversationId, @PathVariable String userId) {
        return ResponseEntity.ok(null);
    }

    // 4. FILES AND MEDIA
    @PostMapping("/upload")
    public ResponseEntity<Object> uploadMedia(@RequestParam("file") MultipartFile file, @RequestParam("type") String type) {
        return ResponseEntity.ok(null);
    }

    @PostMapping("/upload-files")
    public ResponseEntity<Object> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        return ResponseEntity.ok(null);
    }

    @PostMapping("/conversations/{conversationId}/files")
    public ResponseEntity<Object> sendFileMessage(@PathVariable Long conversationId, @RequestParam("file") MultipartFile file, @RequestParam(value = "caption", required = false) String caption) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        return conservationService.sendMessageToConversationByFile(userId, conversationId, file);
    }

    //USERS
    @GetMapping("/users/search")
    public ResponseEntity<Object> searchUsers(@RequestParam String query) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();
        return conservationService.searchUser(userId, query);
    }
}