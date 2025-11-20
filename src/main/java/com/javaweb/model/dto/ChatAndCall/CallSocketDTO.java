package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

public class CallSocketDTO {

    @Data
    public static class InitiateCallRequest {
        private Long receiverID;
        private Long conversationID;
        private String type; // "audio" or "video"
        private Long callID;
    }

    @Data
    public static class CallResponse {
        private Long callID;
        private String callType;
        private Long initiatorID;
        private String initiatorName;
        private Long receiverID;
        private String receiverName;
        private Long conversationID;
        private String status;
    }

    @Data
    public static class CallSignal {
        private Long toUserID;
        private Long fromUserID;
        private Long callID;
        private Signal signal;
    }

    @Data
    public static class Signal {
        private String type; // "offer", "answer", "candidate"
        private String sdp;
        private Object candidate;
    }

    @Data
    public static class AnswerCallRequest {
        private Long callID;
        private Long initiatorID;
        private Boolean accepted;
    }

    @Data
    public static class RejectCallRequest {
        private Long callID;
    }

    @Data
    public static class EndCallRequest {
        private Long callID;
    }

    @Data
    public static class CallError {
        private String message;
        private Long callID;
    }
}
