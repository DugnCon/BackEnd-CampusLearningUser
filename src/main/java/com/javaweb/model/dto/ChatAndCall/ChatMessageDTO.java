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

	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
    
}