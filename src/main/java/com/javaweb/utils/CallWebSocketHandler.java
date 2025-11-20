package com.javaweb.utils;

import com.javaweb.model.dto.ChatAndCall.CallSocketDTO;
import com.javaweb.service.ICallSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CallWebSocketHandler {
    private final SimpMessagingTemplate messagingTemplate;
    private final ICallSocketService callSocketService;

    @MessageMapping("/call.initiate")
    public void handleCallInitiate(@Payload CallSocketDTO.InitiateCallRequest request, @AuthenticationPrincipal Long userId) {
        log.info("User {} initiating call to {}", userId, request.getReceiverID());
        try {
            callSocketService.storeCallInfo(request.getCallID(), userId, request.getReceiverID(), request.getType());
            CallSocketDTO.CallResponse callResponse = new CallSocketDTO.CallResponse();
            callResponse.setCallID(request.getCallID());
            callResponse.setCallType(request.getType());
            callResponse.setInitiatorID(userId);
            callResponse.setReceiverID(request.getReceiverID());
            callResponse.setConversationID(request.getConversationID());
            callResponse.setStatus("ringing");
            messagingTemplate.convertAndSendToUser(request.getReceiverID().toString(), "/queue/call.incoming", callResponse);
            log.info("Call invitation sent to user {}", request.getReceiverID());
        } catch (Exception e) {
            log.error("Error initiating call: {}", e.getMessage());
            CallSocketDTO.CallError error = new CallSocketDTO.CallError();
            error.setCallID(request.getCallID());
            error.setMessage("Failed to initiate call: " + e.getMessage());
            messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/call.error", error);
        }
    }

    @MessageMapping("/call.answer")
    public void handleCallAnswer(@Payload CallSocketDTO.AnswerCallRequest request, @AuthenticationPrincipal Long userId) {
        log.info("User {} answering call {}", userId, request.getCallID());
        try {
            ICallSocketService.CallInfo callInfo = callSocketService.getCallInfo(request.getCallID());
            if (callInfo != null) callInfo.status = "ongoing";
            Map<String, Object> response = Map.of(
                    "callID", request.getCallID(),
                    "accepted", request.getAccepted(),
                    "answererID", userId
            );
            messagingTemplate.convertAndSendToUser(request.getInitiatorID().toString(), "/queue/call.answered", response);
            log.info("Call {} answered by user {}", request.getCallID(), userId);
        } catch (Exception e) {
            log.error("Error answering call: {}", e.getMessage());
            sendCallError(userId, request.getCallID(), "Failed to answer call");
        }
    }

    @MessageMapping("/call.reject")
    public void handleCallReject(@Payload Long callID, @AuthenticationPrincipal Long userId) {
        log.info("User {} rejecting call {}", userId, callID);
        try {
            callSocketService.removeCallInfo(callID);
            Map<String, Object> response = Map.of("callID", callID, "rejectedBy", userId);
            Long initiator = callSocketService.getCallInitiator(callID);
            if (initiator != null) {
                messagingTemplate.convertAndSendToUser(initiator.toString(), "/queue/call.rejected", response);
            }
            log.info("Call {} rejected by user {}", callID, userId);
        } catch (Exception e) {
            log.error("Error rejecting call: {}", e.getMessage());
            sendCallError(userId, callID, "Failed to reject call");
        }
    }

    @MessageMapping("/call.end")
    public void handleCallEnd(@Payload Long callID, @AuthenticationPrincipal Long userId) {
        log.info("User {} ending call {}", userId, callID);
        try {
            callSocketService.removeCallInfo(callID);
            Map<String, Object> response = Map.of("callID", callID, "endedBy", userId);
            Long otherParticipant = callSocketService.getOtherParticipant(callID, userId);
            if (otherParticipant != null) {
                messagingTemplate.convertAndSendToUser(otherParticipant.toString(), "/queue/call.ended", response);
            }
            log.info("Call {} ended by user {}", callID, userId);
        } catch (Exception e) {
            log.error("Error ending call: {}", e.getMessage());
            sendCallError(userId, callID, "Failed to end call");
        }
    }

    @MessageMapping("/call.signal")
    public void handleCallSignal(@Payload CallSocketDTO.CallSignal signal, @AuthenticationPrincipal Long userId) {
        log.debug("Handling WebRTC signal: {} for call {}", signal.getSignal().getType(), signal.getCallID());
        try {
            signal.setFromUserID(userId);
            messagingTemplate.convertAndSendToUser(signal.getToUserID().toString(), "/queue/call.signal", signal);
            log.debug("Signal forwarded from {} to {}", userId, signal.getToUserID());
        } catch (Exception e) {
            log.error("Error forwarding signal: {}", e.getMessage());
            CallSocketDTO.CallError error = new CallSocketDTO.CallError();
            error.setCallID(signal.getCallID());
            error.setMessage("Failed to send signal: " + e.getMessage());
            messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/call.error", error);
        }
    }

    private void sendCallError(Long userId, Long callID, String message) {
        CallSocketDTO.CallError error = new CallSocketDTO.CallError();
        error.setCallID(callID);
        error.setMessage(message);
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/call.error", error);
    }
}