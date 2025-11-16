package com.javaweb.utils;

import com.javaweb.model.dto.ChatAndCall.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class CallSocketHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Xử lý khi có người khởi tạo cuộc gọi
     */
    @MessageMapping("/call.initiate")
    public void handleInitiateCall(CallInitiateMessage message) {
        try {
            log.info("SOCKET - CALL INITIATE: conversationId={}, callId={}, type={}",
                    message.getConversationID(), message.getCallID(), message.getType());

            Map<String, Object> response = new HashMap<>();
            response.put("type", "CALL_INITIATED");
            response.put("data", message);
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend(
                    "/topic/conversation." + message.getConversationID() + ".call",
                    response
            );

            log.info("Đã gửi CALL_INITIATED đến conversation: {}", message.getConversationID());

        } catch (Exception e) {
            log.error("Lỗi xử lý CALL_INITIATE: {}", e.getMessage(), e);
        }
    }

    /**
     * Xử lý khi có người trả lời cuộc gọi
     */
    @MessageMapping("/call.answer")
    public void handleAnswerCall(CallAnswerMessage message) {
        try {
            log.info("SOCKET - CALL ANSWER: callId={}, respondent={}",
                    message.getCallID(), message.getRespondentName());

            Map<String, Object> response = new HashMap<>();
            response.put("type", "CALL_ANSWERED");
            response.put("data", message);
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend(
                    "/topic/call." + message.getCallID(),
                    response
            );

            log.info("Đã gửi CALL_ANSWERED đến call: {}", message.getCallID());

        } catch (Exception e) {
            log.error("Lỗi xử lý CALL_ANSWER: {}", e.getMessage(), e);
        }
    }

    /**
     * Xử lý khi kết thúc cuộc gọi
     */
    @MessageMapping("/call.end")
    public void handleEndCall(CallEndMessage message) {
        try {
            log.info("SOCKET - CALL END: callId={}, endedBy={}, reason={}",
                    message.getCallID(), message.getEndedByName(), message.getReason());

            Map<String, Object> response = new HashMap<>();
            response.put("type", "CALL_ENDED");
            response.put("data", message);
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend(
                    "/topic/call." + message.getCallID(),
                    response
            );

            log.info("Đã gửi CALL_ENDED đến call: {}", message.getCallID());

        } catch (Exception e) {
            log.error("Lỗi xử lý CALL_END: {}", e.getMessage(), e);
        }
    }

    /**
     * Xử lý khi từ chối cuộc gọi
     */
    @MessageMapping("/call.reject")
    public void handleRejectCall(CallEndMessage message) {
        try {
            log.info("SOCKET - CALL REJECT: callId={}, rejectedBy={}",
                    message.getCallID(), message.getEndedByName());

            message.setReason("rejected");

            Map<String, Object> response = new HashMap<>();
            response.put("type", "CALL_REJECTED");
            response.put("data", message);
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend(
                    "/topic/call." + message.getCallID(),
                    response
            );

            log.info("Đã gửi CALL_REJECTED đến call: {}", message.getCallID());

        } catch (Exception e) {
            log.error("Lỗi xử lý CALL_REJECT: {}", e.getMessage(), e);
        }
    }

    // === THÊM CÁC WEBRTC SIGNALING EVENTS === //

    /**
     * WebRTC Offer - Gửi offer từ caller
     */
    @MessageMapping("/webrtc.offer")
    public void handleWebRTCOffer(WebRTCOfferMessage message) {
        try {
            log.info("SOCKET - WEBRTC OFFER: from={}, to={}, callId={}",
                    message.getFromUserId(), message.getToUserId(), message.getCallId());

            Map<String, Object> response = new HashMap<>();
            response.put("type", "webrtc-offer");
            response.put("data", message);
            response.put("timestamp", System.currentTimeMillis());

            // Gửi trực tiếp đến user cụ thể
            messagingTemplate.convertAndSendToUser(
                    message.getToUserId(),
                    "/queue/webrtc",
                    response
            );

            log.info("Đã gửi WEBRTC OFFER đến user: {}", message.getToUserId());

        } catch (Exception e) {
            log.error("Lỗi xử lý WEBRTC OFFER: {}", e.getMessage(), e);
        }
    }

    /**
     * WebRTC Answer - Gửi answer từ callee
     */
    @MessageMapping("/webrtc.answer")
    public void handleWebRTCAnswer(WebRTCAnswerMessage message) {
        try {
            log.info("SOCKET - WEBRTC ANSWER: from={}, to={}, callId={}",
                    message.getFromUserId(), message.getToUserId(), message.getCallId());

            Map<String, Object> response = new HashMap<>();
            response.put("type", "webrtc-answer");
            response.put("data", message);
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSendToUser(
                    message.getToUserId(),
                    "/queue/webrtc",
                    response
            );

            log.info("Đã gửi WEBRTC ANSWER đến user: {}", message.getToUserId());

        } catch (Exception e) {
            log.error("Lỗi xử lý WEBRTC ANSWER: {}", e.getMessage(), e);
        }
    }

    /**
     * WebRTC ICE Candidate - Trao đổi ICE candidates
     */
    @MessageMapping("/webrtc.ice-candidate")
    public void handleWebRTCIceCandidate(WebRTCIceCandidateMessage message) {
        try {
            log.info("SOCKET - WEBRTC ICE CANDIDATE: from={}, to={}, callId={}",
                    message.getFromUserId(), message.getToUserId(), message.getCallId());

            Map<String, Object> response = new HashMap<>();
            response.put("type", "webrtc-ice-candidate");
            response.put("data", message);
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSendToUser(
                    message.getToUserId(),
                    "/queue/webrtc",
                    response
            );

            log.info("Đã gửi WEBRTC ICE CANDIDATE đến user: {}", message.getToUserId());

        } catch (Exception e) {
            log.error("Lỗi xử lý WEBRTC ICE CANDIDATE: {}", e.getMessage(), e);
        }
    }

    /**
     * Toggle Audio - Bật/tắt âm thanh
     */
    @MessageMapping("/call.audio.toggle")
    public void handleToggleAudio(ToggleMediaMessage message) {
        try {
            log.info("SOCKET - TOGGLE AUDIO: userId={}, callId={}, muted={}",
                    message.getUserId(), message.getCallId(), message.isMuted());

            Map<String, Object> response = new HashMap<>();
            response.put("type", "audio-toggled");
            response.put("data", message);
            response.put("timestamp", System.currentTimeMillis());

            // Gửi đến tất cả participants trong call (trừ chính user đó)
            messagingTemplate.convertAndSend(
                    "/topic/call." + message.getCallId() + ".media",
                    response
            );

        } catch (Exception e) {
            log.error("Lỗi xử lý TOGGLE AUDIO: {}", e.getMessage(), e);
        }
    }

    /**
     * Toggle Video - Bật/tắt video
     */
    @MessageMapping("/call.video.toggle")
    public void handleToggleVideo(ToggleMediaMessage message) {
        try {
            log.info("SOCKET - TOGGLE VIDEO: userId={}, callId={}, enabled={}",
                    message.getUserId(), message.getCallId(), message.isEnabled());

            Map<String, Object> response = new HashMap<>();
            response.put("type", "video-toggled");
            response.put("data", message);
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend(
                    "/topic/call." + message.getCallId() + ".media",
                    response
            );

        } catch (Exception e) {
            log.error("Lỗi xử lý TOGGLE VIDEO: {}", e.getMessage(), e);
        }
    }

    /**
     * Screen Share Start - Bắt đầu chia sẻ màn hình
     */
    @MessageMapping("/call.screenshare.start")
    public void handleScreenShareStart(ScreenShareMessage message) {
        try {
            log.info("SOCKET - SCREENSHARE START: userId={}, callId={}",
                    message.getUserId(), message.getCallId());

            Map<String, Object> response = new HashMap<>();
            response.put("type", "screenshare-started");
            response.put("data", message);
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend(
                    "/topic/call." + message.getCallId() + ".media",
                    response
            );

        } catch (Exception e) {
            log.error("Lỗi xử lý SCREENSHARE START: {}", e.getMessage(), e);
        }
    }

    /**
     * Screen Share Stop - Dừng chia sẻ màn hình
     */
    @MessageMapping("/call.screenshare.stop")
    public void handleScreenShareStop(ScreenShareMessage message) {
        try {
            log.info("SOCKET - SCREENSHARE STOP: userId={}, callId={}",
                    message.getUserId(), message.getCallId());

            Map<String, Object> response = new HashMap<>();
            response.put("type", "screenshare-stopped");
            response.put("data", message);
            response.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend(
                    "/topic/call." + message.getCallId() + ".media",
                    response
            );

        } catch (Exception e) {
            log.error("Lỗi xử lý SCREENSHARE STOP: {}", e.getMessage(), e);
        }
    }

    /**
     * Keep Alive - Giữ kết nối
     */
    @MessageMapping("/call.keepalive")
    public void handleKeepAlive(KeepAliveMessage message) {
        try {
            // Chỉ log để debug, không cần xử lý gì
            log.debug("SOCKET - KEEP ALIVE: userId={}, callId={}",
                    message.getUserId(), message.getCallId());

        } catch (Exception e) {
            log.error("Lỗi xử lý KEEP ALIVE: {}", e.getMessage(), e);
        }
    }
}