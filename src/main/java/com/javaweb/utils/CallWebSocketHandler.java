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

    /**
     * Handle call initiation - User A calls User B
     */
    @MessageMapping("/call.initiate")
    public void handleCallInitiate(@Payload CallSocketDTO.InitiateCallRequest request,
                                   @AuthenticationPrincipal Long userId) {
        log.info("📞 User {} initiating call to {}", userId, request.getReceiverID());

        try {
            // Store call information
            callSocketService.storeCallInfo(
                    request.getCallID(),
                    userId,
                    request.getReceiverID(),
                    request.getType()
            );

            // Forward call invitation to receiver
            CallSocketDTO.CallResponse callResponse = new CallSocketDTO.CallResponse();
            callResponse.setCallID(request.getCallID());
            callResponse.setCallType(request.getType());
            callResponse.setInitiatorID(userId);
            callResponse.setReceiverID(request.getReceiverID());
            callResponse.setConversationID(request.getConversationID());
            callResponse.setStatus("ringing");

            messagingTemplate.convertAndSendToUser(
                    request.getReceiverID().toString(),
                    "/queue/call.incoming",
                    callResponse
            );

            log.info("✅ Call invitation sent to user {}", request.getReceiverID());

        } catch (Exception e) {
            log.error("❌ Error initiating call: {}", e.getMessage());

            // Send error back to caller
            CallSocketDTO.CallError error = new CallSocketDTO.CallError();
            error.setCallID(request.getCallID());
            error.setMessage("Failed to initiate call: " + e.getMessage());

            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/call.error",
                    error
            );
        }
    }

    /**
     * Handle call answer - User B answers User A's call
     */
    @MessageMapping("/call.answer")
    public void handleCallAnswer(@Payload CallSocketDTO.AnswerCallRequest request,
                                 @AuthenticationPrincipal Long userId) {
        log.info("✅ User {} answering call {}", userId, request.getCallID());

        try {
            // Update call status
            ICallSocketService.CallInfo callInfo = callSocketService.getCallInfo(request.getCallID());
            if (callInfo != null) {
                callInfo.status = "ongoing";
            }

            // Notify caller that call was answered
            Map<String, Object> response = Map.of(
                    "callID", request.getCallID(),
                    "accepted", request.getAccepted(),
                    "answererID", userId
            );

            messagingTemplate.convertAndSendToUser(
                    request.getInitiatorID().toString(),
                    "/queue/call.answered",
                    response
            );

            log.info("📞 Call {} answered by user {}", request.getCallID(), userId);

        } catch (Exception e) {
            log.error("❌ Error answering call: {}", e.getMessage());
            sendCallError(userId, request.getCallID(), "Failed to answer call");
        }
    }

    /**
     * Handle call rejection - User B rejects User A's call
     */
    @MessageMapping("/call.reject")
    public void handleCallReject(@Payload CallSocketDTO.RejectCallRequest request,
                                 @AuthenticationPrincipal Long userId) {
        log.info("❌ User {} rejecting call {}", userId, request.getCallID());

        try {
            // Remove call information
            callSocketService.removeCallInfo(request.getCallID());

            // Notify caller that call was rejected
            Map<String, Object> response = Map.of(
                    "callID", request.getCallID(),
                    "rejectedBy", userId
            );

            messagingTemplate.convertAndSendToUser(
                    callSocketService.getCallInitiator(request.getCallID()).toString(),
                    "/queue/call.rejected",
                    response
            );

            log.info("📞 Call {} rejected by user {}", request.getCallID(), userId);

        } catch (Exception e) {
            log.error("❌ Error rejecting call: {}", e.getMessage());
            sendCallError(userId, request.getCallID(), "Failed to reject call");
        }
    }

    /**
     * Handle call end - Either user ends the call
     */
    @MessageMapping("/call.end")
    public void handleCallEnd(@Payload CallSocketDTO.EndCallRequest request,
                              @AuthenticationPrincipal Long userId) {
        log.info("⏹️ User {} ending call {}", userId, request.getCallID());

        try {
            // Remove call information
            callSocketService.removeCallInfo(request.getCallID());

            // Notify both parties that call ended
            Map<String, Object> response = Map.of(
                    "callID", request.getCallID(),
                    "endedBy", userId
            );

            // Notify all participants
            Long otherParticipant = callSocketService.getOtherParticipant(request.getCallID(), userId);
            if (otherParticipant != null) {
                messagingTemplate.convertAndSendToUser(
                        otherParticipant.toString(),
                        "/queue/call.ended",
                        response
                );
            }

            log.info("📞 Call {} ended by user {}", request.getCallID(), userId);

        } catch (Exception e) {
            log.error("❌ Error ending call: {}", e.getMessage());
            sendCallError(userId, request.getCallID(), "Failed to end call");
        }
    }

    /**
     * Handle WebRTC signaling - Forward signals between users
     */
    @MessageMapping("/call.signal")
    public void handleCallSignal(@Payload CallSocketDTO.CallSignal signal,
                                 @AuthenticationPrincipal Long userId) {
        log.debug("📨 Handling WebRTC signal: {} for call {}",
                signal.getSignal().getType(), signal.getCallID());

        try {
            // Set the sender ID
            signal.setFromUserID(userId);

            // Forward signal to target user
            messagingTemplate.convertAndSendToUser(
                    signal.getToUserID().toString(),
                    "/queue/call.signal",
                    signal
            );

            log.debug("✅ Signal forwarded from {} to {} (type: {})",
                    userId, signal.getToUserID(), signal.getSignal().getType());

        } catch (Exception e) {
            log.error("❌ Error forwarding signal: {}", e.getMessage());

            CallSocketDTO.CallError error = new CallSocketDTO.CallError();
            error.setCallID(signal.getCallID());
            error.setMessage("Failed to send signal: " + e.getMessage());

            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/call.error",
                    error
            );
        }
    }

    private void sendCallError(Long userId, Long callID, String message) {
        CallSocketDTO.CallError error = new CallSocketDTO.CallError();
        error.setCallID(callID);
        error.setMessage(message);

        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/call.error",
                error
        );
    }
}
