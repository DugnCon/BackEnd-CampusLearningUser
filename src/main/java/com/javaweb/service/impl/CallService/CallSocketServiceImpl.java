package com.javaweb.service.impl.CallService;

import com.javaweb.service.ICallSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class CallSocketServiceImpl implements ICallSocketService {

    private final ConcurrentHashMap<Long, CallInfo> activeCalls = new ConcurrentHashMap<>();

    @Override
    public void storeCallInfo(Long callID, Long initiatorID, Long receiverID, String callType) {
        activeCalls.put(callID, new CallInfo(initiatorID, receiverID, callType));
        log.info("💾 Stored call info: {}", callID);
    }

    @Override
    public CallInfo getCallInfo(Long callID) {
        return activeCalls.get(callID);
    }

    @Override
    public void removeCallInfo(Long callID) {
        activeCalls.remove(callID);
        log.info("🗑️ Removed call info: {}", callID);
    }

    @Override
    public Long getCallInitiator(Long callID) {
        CallInfo callInfo = activeCalls.get(callID);
        return callInfo != null ? callInfo.initiatorID : null;
    }

    @Override
    public Long getOtherParticipant(Long callID, Long currentUserID) {
        CallInfo callInfo = activeCalls.get(callID);
        if (callInfo == null) return null;

        if (callInfo.initiatorID.equals(currentUserID)) {
            return callInfo.receiverID;
        } else if (callInfo.receiverID.equals(currentUserID)) {
            return callInfo.initiatorID;
        }

        return null;
    }
}