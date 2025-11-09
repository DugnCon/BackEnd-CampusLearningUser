package com.javaweb.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface IProfileService {
    ResponseEntity<?> getMediaUser(Long userId, Long targetUserId, int limit);
    ResponseEntity<?> updateAvatar(MultipartFile file, Long userId) throws IOException;
}
