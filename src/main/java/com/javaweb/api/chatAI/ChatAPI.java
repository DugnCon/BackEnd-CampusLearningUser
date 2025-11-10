package com.javaweb.api.chatAI;
import com.javaweb.model.dto.ChatMessageDTO;
import com.javaweb.model.dto.ChatRequestDTO;
import com.javaweb.service.GeminiService;
import com.javaweb.api.chatAI.ErrorResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ai/chat")
@CrossOrigin(origins = "http://localhost:5004")
public class ChatAPI {

    private final GeminiService geminiService;

    public ChatAPI(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody ChatRequestDTO chatRequestDTO) {

        List<ChatMessageDTO> messages = chatRequestDTO.getMessages();
        if (messages == null || messages.isEmpty()) {
            return ResponseEntity.badRequest().body(new ChatMessageDTO("error", "Danh sách tin nhắn không hợp lệ.", null));
        }

        ChatMessageDTO latestUserMessage = messages.get(messages.size() - 1);
        List<ChatMessageDTO> history = messages.subList(0, messages.size() - 1);

        String aiResponseContent;
        try {
            aiResponseContent = geminiService.getAIResponse(history, latestUserMessage);

            ChatMessageDTO aiMessage = new ChatMessageDTO("assistant", aiResponseContent, null);
            return ResponseEntity.ok(aiMessage);

        } catch (Exception e) {
            System.err.println("Error processing chat request: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ErrorResponse(
                            "Lỗi AI Server",
                            "Xin lỗi, đã xảy ra lỗi trong quá trình xử lý yêu cầu AI: " + e.getMessage()
                    )
            );
        }
    }
}
class ErrorResponse {
    private String error;
    private String message;

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }

    public String getError() { return error; }
    public String getMessage() { return message; }

    public void setError(String error) { this.error = error; }
    public void setMessage(String message) { this.message = message; }

}


// 💡 Cần tạo DTO ErrorResponse (Hoặc dùng Map<String, String>)
