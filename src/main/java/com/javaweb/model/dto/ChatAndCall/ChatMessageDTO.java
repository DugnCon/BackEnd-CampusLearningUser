package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private String role;    // "user" hoặc "assistant"
    private String content; // Nội dung tin nhắn
    private String image;   // Base64 string của ảnh (chỉ có trong tin nhắn user)
}