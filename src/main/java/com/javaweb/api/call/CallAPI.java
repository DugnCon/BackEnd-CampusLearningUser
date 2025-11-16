package com.javaweb.api.call;

import com.javaweb.model.dto.ChatAndCall.CallDTO;
import com.javaweb.model.dto.ChatAndCall.InitiateCallRequest;
import com.javaweb.model.dto.MyUserDetail;
import com.javaweb.service.ICallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/calls")
@Slf4j
public class CallAPI {

    @Autowired
    private ICallService callService;

    /**
     * Lấy thông tin user từ authentication
     */
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User chưa đăng nhập");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof MyUserDetail) {
            MyUserDetail myUserDetail = (MyUserDetail) principal;
            return myUserDetail.getId();
        } else {
            throw new RuntimeException("Không thể lấy thông tin user");
        }
    }

    /**
     * Khởi tạo cuộc gọi mới - VERSION MỚI cho FE
     */
    @PostMapping("/initiate")
    public ResponseEntity<?> initiateCall(@RequestBody Map<String, Object> request) {
        try {
            Long userId = getCurrentUserId();
            String receiverId = request.get("receiverId").toString();
            String type = request.get("type") != null ? request.get("type").toString() : "video";

            log.info("📞 INITIATE CALL - userId: {}, receiverId: {}, type: {}",
                    userId, receiverId, type);

            // Tạo request object
            InitiateCallRequest initiateRequest = new InitiateCallRequest();
            initiateRequest.setReceiverID(receiverId);
            initiateRequest.setType(type);
            // Tạo conversation ID tạm thời từ receiverId (có thể sửa sau)
            initiateRequest.setConversationID(Long.parseLong(receiverId));

            CallDTO call = callService.initiateCall(initiateRequest, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cuộc gọi đã được khởi tạo");
            response.put("call", call); // Đổi từ "data" -> "call" để match FE

            log.info("✅ INITIATE CALL SUCCESS - callId: {}", call.getCallID());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ INITIATE CALL ERROR: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Trả lời cuộc gọi - VERSION MỚI
     */
    @PostMapping("/answer")
    public ResponseEntity<?> answerCall(@RequestBody Map<String, Object> request) {
        try {
            Long callId = convertToLong(request.get("callId"));
            Long userId = getCurrentUserId();

            log.info("📞 ANSWER CALL - userId: {}, callId: {}", userId, callId);

            CallDTO call = callService.answerCall(callId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã trả lời cuộc gọi");
            response.put("call", call); // Đổi từ "data" -> "call"

            log.info("✅ ANSWER CALL SUCCESS - callId: {}", callId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ ANSWER CALL ERROR: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Kết thúc cuộc gọi - VERSION MỚI
     */
    @PostMapping("/end")
    public ResponseEntity<?> endCall(@RequestBody Map<String, Object> request) {
        try {
            Long callId = convertToLong(request.get("callId"));
            Long userId = getCurrentUserId();

            log.info("📞 END CALL - userId: {}, callId: {}", userId, callId);

            CallDTO call = callService.endCall(callId, "normal", userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã kết thúc cuộc gọi");
            response.put("call", call); // Đổi từ "data" -> "call"

            log.info("✅ END CALL SUCCESS - callId: {}", callId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ END CALL ERROR: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Từ chối cuộc gọi - VERSION MỚI
     */
    @PostMapping("/reject")
    public ResponseEntity<?> rejectCall(@RequestBody Map<String, Object> request) {
        try {
            Long callId = convertToLong(request.get("callId"));
            Long userId = getCurrentUserId();

            log.info("📞 REJECT CALL - userId: {}, callId: {}", userId, callId);

            CallDTO call = callService.rejectCall(callId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã từ chối cuộc gọi");
            response.put("call", call); // Đổi từ "data" -> "call"

            log.info("✅ REJECT CALL SUCCESS - callId: {}", callId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ REJECT CALL ERROR: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Lấy thông tin cuộc gọi theo ID
     */
    @GetMapping("/{callId}")
    public ResponseEntity<?> getCallDetails(@PathVariable Long callId) {
        try {
            Long userId = getCurrentUserId();
            log.info("📞 GET CALL DETAILS - userId: {}, callId: {}", userId, callId);

            CallDTO call = callService.getCallById(callId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("call", call); // Đổi từ "data" -> "call"

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ GET CALL DETAILS ERROR: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Lấy danh sách cuộc gọi đang active - VERSION MỚI cho FE
     */
    @GetMapping("/active/check")
    public ResponseEntity<?> checkActiveCall() {
        try {
            Long userId = getCurrentUserId();
            log.info("📞 CHECK ACTIVE CALL - userId: {}", userId);

            List<CallDTO> calls = callService.getActiveCalls();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("hasActiveCall", !calls.isEmpty());

            if (!calls.isEmpty()) {
                // Trả về call đầu tiên đang active
                response.put("call", calls.get(0));
                log.info("✅ ACTIVE CALL FOUND - callId: {}", calls.get(0).getCallID());
            } else {
                response.put("call", null);
                log.info("ℹ️ NO ACTIVE CALL FOUND");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ CHECK ACTIVE CALL ERROR: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("hasActiveCall", false);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Lấy danh sách cuộc gọi đang active (original)
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveCalls() {
        try {
            Long userId = getCurrentUserId();
            log.info("📞 GET ACTIVE CALLS - userId: {}", userId);

            List<CallDTO> calls = callService.getActiveCalls();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("calls", calls); // Đổi từ "data" -> "calls"

            log.info("✅ GET ACTIVE CALLS SUCCESS - count: {}", calls.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ GET ACTIVE CALLS ERROR: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Helper method để convert object sang Long
     */
    private Long convertToLong(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("ID không được null");
        }
        if (obj instanceof String) {
            try {
                return Long.parseLong((String) obj);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("ID phải là số hợp lệ: " + obj);
            }
        } else if (obj instanceof Number) {
            return ((Number) obj).longValue();
        } else {
            throw new IllegalArgumentException("ID phải là số: " + obj.getClass().getSimpleName());
        }
    }
}