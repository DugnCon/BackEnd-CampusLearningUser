package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class CallEndMessage {
    private Long callID;        // ID cuộc gọi
    private Long targetUserID;  // ID người còn lại trong cuộc gọi
    private String reason;      // Lý do kết thúc
    private Integer duration;   // Thời lượng cuộc gọi (giây)
    private Long fromUserID;    // ID người kết thúc cuộc gọi (optional)
}