package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

public class CallSocketDTO {
    @Data
    public static class InitiateCallRequest {
        private Object receiverID;
        private Object conversationID;
        private String type;
        private Object callID;
        private Long fromUserID;

        public Long getReceiverID() {
            return convertToLong(receiverID);
        }
        public Long getConversationID() {
            return convertToLong(conversationID);
        }
        public Long getCallID() {
            return convertToLong(callID);
        }
        private Long convertToLong(Object obj) {
            if (obj == null) return null;
            if (obj instanceof Long) return (Long) obj;
            if (obj instanceof Integer) return ((Integer) obj).longValue();
            if (obj instanceof String) {
                try {
                    return Long.parseLong((String) obj);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        }
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
        private Object toUserID;
        private Object fromUserID;
        private Object callID;
        private Signal signal;

        public Long getToUserID() {
            return convertToLong(toUserID);
        }
        public Long getCallID() {
            return convertToLong(callID);
        }
        private Long convertToLong(Object obj) {
            if (obj == null) return null;
            if (obj instanceof Long) return (Long) obj;
            if (obj instanceof Integer) return ((Integer) obj).longValue();
            if (obj instanceof String) {
                try {
                    return Long.parseLong((String) obj);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        }
    }

    @Data
    public static class Signal {
        private String type;
        private String sdp;
        private Object candidate;
    }

    @Data
    public static class AnswerCallRequest {
        private Object callID;
        private Object initiatorID;
        private Boolean accepted;
        private Long fromUserID;

        public Long getCallID() {
            return convertToLong(callID);
        }
        public Long getInitiatorID() {
            return convertToLong(initiatorID);
        }
        private Long convertToLong(Object obj) {
            if (obj == null) return null;
            if (obj instanceof Long) return (Long) obj;
            if (obj instanceof Integer) return ((Integer) obj).longValue();
            if (obj instanceof String) {
                try {
                    return Long.parseLong((String) obj);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        }
    }

    @Data
    public static class RejectCallRequest {
        private Object callID;

        public Long getCallID() {
            return convertToLong(callID);
        }
        private Long convertToLong(Object obj) {
            if (obj == null) return null;
            if (obj instanceof Long) return (Long) obj;
            if (obj instanceof Integer) return ((Integer) obj).longValue();
            if (obj instanceof String) {
                try {
                    return Long.parseLong((String) obj);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        }
    }

    @Data
    public static class EndCallRequest {
        private Object callID;

        public Long getCallID() {
            return convertToLong(callID);
        }
        private Long convertToLong(Object obj) {
            if (obj == null) return null;
            if (obj instanceof Long) return (Long) obj;
            if (obj instanceof Integer) return ((Integer) obj).longValue();
            if (obj instanceof String) {
                try {
                    return Long.parseLong((String) obj);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        }
    }

    @Data
    public static class CallError {
        private String message;
        private Long callID;
    }
}