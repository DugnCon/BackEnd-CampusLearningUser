package com.javaweb.model.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRequestDTO {

    // Frontend gửi toàn bộ lịch sử (bao gồm tin nhắn mới nhất của User)
    private List<ChatMessageDTO> messages;
}