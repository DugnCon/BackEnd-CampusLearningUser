package com.javaweb.utils;

import com.javaweb.model.dto.ChatAndCall.CallSocketDTO;
import com.javaweb.service.ICallSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CallWebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final ICallSocketService callSocketService;

    @MessageMapping("/call.initiate")
    public void handleCallInitiate(@Payload CallSocketDTO.InitiateCallRequest request) {
        Long userId = request.getFromUserID();
        log.info("User {} initiating call to {}", userId, request.getReceiverID());

        try {
            callSocketService.storeCallInfo(request.getCallID(), userId, request.getReceiverID(), request.getType());

            CallSocketDTO.CallResponse response = new CallSocketDTO.CallResponse();
            response.setCallID(request.getCallID());
            response.setCallType(request.getType());
            response.setInitiatorID(userId);
            response.setReceiverID(request.getReceiverID());
            response.setConversationID(request.getConversationID());
            response.setStatus("ringing");

            messagingTemplate.convertAndSend("/user/" + request.getReceiverID() + "/queue/call.incoming", response);
            log.info("Call invitation sent to user {}", request.getReceiverID());
        } catch (Exception e) {
            log.error("Error initiating call: {}", e.getMessage());
            sendCallError(userId, request.getCallID(), "Failed to initiate call: " + e.getMessage());
        }
    }

    @MessageMapping("/call.answer")
    public void handleCallAnswer(@Payload CallSocketDTO.AnswerCallRequest request) {
        Long userId = request.getFromUserID();
        log.info("User {} answering call {}", userId, request.getCallID());

        try {
            ICallSocketService.CallInfo callInfo = callSocketService.getCallInfo(request.getCallID());
            if (callInfo != null) callInfo.status = "ongoing";

            Map<String, Object> response = Map.of(
                    "callID", request.getCallID(),
                    "accepted", request.getAccepted(),
                    "answererID", userId
            );
            messagingTemplate.convertAndSend("/user/" + request.getInitiatorID() + "/queue/call.answered", response);
            log.info("Call {} answered by user {}", request.getCallID(), userId);
        } catch (Exception e) {
            log.error("Error answering call: {}", e.getMessage());
            sendCallError(userId, request.getCallID(), "Failed to answer call");
        }
    }

    @MessageMapping("/call.reject")
    public void handleCallReject(@Payload Map<String, Object> payload) {
        Long callID = ((Number) payload.get("callID")).longValue();
        Long userId = ((Number) payload.get("fromUserID")).longValue();

        log.info("User {} rejecting call {}", userId, callID);

        try {
            callSocketService.removeCallInfo(callID);

            Map<String, Object> response = Map.of("callID", callID, "rejectedBy", userId);
            Long initiator = callSocketService.getCallInitiator(callID);
            if (initiator != null) {
                messagingTemplate.convertAndSend("/user/" + initiator + "/queue/call.rejected", response);
            }
            log.info("Call {} rejected by user {}", callID, userId);
        } catch (Exception e) {
            log.error("Error rejecting call: {}", e.getMessage());
            sendCallError(userId, callID, "Failed to reject call");
        }
    }

    @MessageMapping("/call.end")
    public void handleCallEnd(@Payload Map<String, Object> payload) {
        Long callID = ((Number) payload.get("callID")).longValue();
        Long userId = ((Number) payload.get("fromUserID")).longValue();

        log.info("User {} ending call {}", userId, callID);

        try {
            callSocketService.removeCallInfo(callID);

            Map<String, Object> response = Map.of("callID", callID, "endedBy", userId);
            Long otherParticipant = callSocketService.getOtherParticipant(callID, userId);
            if (otherParticipant != null) {
                messagingTemplate.convertAndSend("/user/" + otherParticipant + "/queue/call.ended", response);
            }
            log.info("Call {} ended by user {}", callID, userId);
        } catch (Exception e) {
            log.error("Error ending call: {}", e.getMessage());
            sendCallError(userId, callID, "Failed to end call");
        }
    }

    @MessageMapping("/call.signal")
    public void handleCallSignal(@Payload CallSocketDTO.CallSignal signal) {
        Long fromUserID = Long.valueOf(signal.getFromUserID().toString());

        log.debug("Forwarding WebRTC signal {} from {} to {} (call {})",
                signal.getSignal().getType(), fromUserID, signal.getToUserID(), signal.getCallID());

        try {
            signal.setFromUserID(fromUserID);
            messagingTemplate.convertAndSend("/user/" + signal.getToUserID() + "/queue/call.signal", signal);
        } catch (Exception e) {
            log.error("Error forwarding signal: {}", e.getMessage());
            sendCallError(fromUserID, signal.getCallID(), "Failed to send signal: " + e.getMessage());
        }
    }

    private void sendCallError(Long userId, Long callID, String message) {
        if (userId == null) return;
        CallSocketDTO.CallError error = new CallSocketDTO.CallError();
        error.setCallID(callID);
        error.setMessage(message);
        messagingTemplate.convertAndSend("/user/" + userId + "/queue/call.error", error);
    }
}