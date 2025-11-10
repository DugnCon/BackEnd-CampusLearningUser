package com.javaweb.service;

import com.javaweb.model.dto.ChatMessageDTO;
import com.javaweb.model.dto.ChatMessageDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class GeminiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String MODEL_NAME = "gemini-2.5-flash";
    private static final String API_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String SYSTEM_PROMPT = "Bạn là trợ lý AI hữu ích. Hãy trả lời các câu hỏi một cách thân thiện, chính xác, ngắn gọn và chuyên nghiệp.";

    public GeminiService( WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(API_BASE_URL).build();
    }

    private List<Map<String, Object>> buildContentsPayload(List<ChatMessageDTO> fullContext) {
        List<Map<String, Object>> contents = new ArrayList<>();

        if (fullContext.size() <= 1 ||
                (fullContext.size() > 1 &&
                        !fullContext.get(0).getContent().equals(SYSTEM_PROMPT))) {

            Map<String, Object> systemPart = new HashMap<>();
            systemPart.put("text", SYSTEM_PROMPT);

            Map<String, Object> systemContent = new HashMap<>();
            // Đặt role là user để API chấp nhận làm tin nhắn đầu tiên
            systemContent.put("role", "user");
            systemContent.put("parts", List.of(systemPart));
            contents.add(systemContent);
        }


        for (ChatMessageDTO msg : fullContext) {

            if (msg.getContent().equals(SYSTEM_PROMPT)) continue;

            // Parts có thể chứa Text và Image
            List<Map<String, Object>> parts = new ArrayList<>();

            // Thêm phần Text
            if (msg.getContent() != null && !msg.getContent().isEmpty()) {
                parts.add(Map.of("text", msg.getContent()));
            }

            // Thêm phần Image
            if (msg.getImage() != null && !msg.getImage().isEmpty()) {
                parts.add(Map.of("inlineData", Map.of(
                        "mimeType", "image/jpeg",
                        "data", msg.getImage()
                )));
            }

            // Chỉ thêm vào contents nếu có Parts
            if (!parts.isEmpty()) {
                Map<String, Object> content = new HashMap<>();
                content.put("role", msg.getRole().equals("assistant") ? "model" : "user");
                content.put("parts", parts);
                contents.add(content);
            }
        }

        return contents;
    }


    /**
     * Phương thức chính để gọi Gemini API.
     */
    public String getAIResponse(List<ChatMessageDTO> history, ChatMessageDTO latestUserMessage) {

        List<ChatMessageDTO> fullContext = new ArrayList<>(history);
        fullContext.add(latestUserMessage);

        if (apiKey == null || apiKey.isEmpty()) {
            return "Lỗi cấu hình: Gemini API Key chưa được cung cấp.";
        }

        try {
            Map<String, Object> payload = new HashMap<>();

            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("maxOutputTokens", 2048);


            payload.put("generationConfig", generationConfig);
            payload.put("contents", buildContentsPayload(fullContext));

            String response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(MODEL_NAME + ":generateContent")
                            .queryParam("key", apiKey)
                            .build())
                    .body(BodyInserters.fromValue(payload))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse ->
                            clientResponse.bodyToMono(String.class).map(body -> new RuntimeException("API call failed with status: " + clientResponse.statusCode() + " and body: " + body))
                    )
                    .bodyToMono(String.class)
                    .block();

            return parseGeminiResponse(response);

        } catch (WebClientResponseException e) {
            System.err.println("API Error: Status " + e.getStatusCode() + ", Body: " + e.getResponseBodyAsString());
            return "Xin lỗi, có lỗi xảy ra khi gọi Gemini API: Status " + e.getStatusCode() + ". Vui lòng kiểm tra lại API Key và hạn mức.";
        } catch (Exception e) {
            System.err.println("Error calling Gemini API: " + e.getMessage());
            return "Xin lỗi, có lỗi xảy ra khi gọi Gemini API: " + e.getMessage();
        }
    }

    private String parseGeminiResponse(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.isEmpty()) {
            return "Không nhận được phản hồi từ AI.";
        }

        try {
            JsonNode root = objectMapper.readTree(jsonResponse);

            JsonNode textNode = root.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text");

            if (textNode.isTextual()) {
                return textNode.asText();
            } else {
                JsonNode promptFeedback = root.path("promptFeedback");
                if (!promptFeedback.isMissingNode() && promptFeedback.has("safetyRatings")) {
                    return "Phản hồi AI bị chặn do vi phạm chính sách an toàn: " + promptFeedback.toString();
                }
                return "Không thể trích xuất nội dung văn bản từ phản hồi AI.";
            }

        } catch (Exception e) {
            System.err.println("Lỗi phân tích cú pháp JSON phản hồi AI: " + e.getMessage());
            return "Lỗi phân tích cú pháp phản hồi AI: " + e.getMessage();
        }
    }
}