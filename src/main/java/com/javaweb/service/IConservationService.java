package com.javaweb.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
@Service
public interface IConservationService {
    ResponseEntity<Object> getAllConversation(Long userId);
    ResponseEntity<Object> searchUser(Long userId, String query);
    //ResponseEntity<Object> createPrivateConversation(Long userId, Map<String,Object> conversationData);
    ResponseEntity<Object> createConversation(Long userId, Map<String,Object> conversationData);

    ResponseEntity<Object> sendMessageToConversation(Long userId, Long conversationId, Map<String,Object> conversationData);
    ResponseEntity<Object> getMessages(Long userId, Long conversationId);
    ResponseEntity<Object> deleteMessage(Long messageId, Long userId, boolean deleteForEveryone);
    ResponseEntity<Object> updateMessage(Long messageId, Long userId, String content);

    ResponseEntity<Object> sendMessageToConversationByFile(Long userId, Long conversationId, MultipartFile file);
}
