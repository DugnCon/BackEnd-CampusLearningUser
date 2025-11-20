package com.javaweb.service;

import java.util.concurrent.ConcurrentHashMap;

public interface ICallSocketService {

    class CallInfo {
        public Long initiatorID;
        public Long receiverID;
        public String callType;
        public String status;

        public CallInfo(Long initiatorID, Long receiverID, String callType) {
            this.initiatorID = initiatorID;
            this.receiverID = receiverID;
            this.callType = callType;
            this.status = "ringing";
        }
    }

    void storeCallInfo(Long callID, Long initiatorID, Long receiverID, String callType);
    CallInfo getCallInfo(Long callID);
    void removeCallInfo(Long callID);
    Long getCallInitiator(Long callID);
    Long getOtherParticipant(Long callID, Long currentUserID);
}