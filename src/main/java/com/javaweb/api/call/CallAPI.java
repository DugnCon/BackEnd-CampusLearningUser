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

import javax.sound.midi.SysexMessage;
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
     * Khởi tạo cuộc gọi mới
     */
    @PostMapping("/initiate")
    public ResponseEntity<?> initiateCall(@RequestBody InitiateCallRequest request) {
        System.out.println("✅ "  + request);
        try {
            Long userId = getCurrentUserId();
            log.info("📞 INITIATE CALL - userId: {}, conversationId: {}, type: {}",
                    userId, request.getConversationID(), request.getType());

            CallDTO call = callService.initiateCall(request, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cuộc gọi đã được khởi tạo");
            response.put("data", call);

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
     * Trả lời cuộc gọi
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
            response.put("data", call);

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
     * Kết thúc cuộc gọi
     */
    @PostMapping("/end")
    public ResponseEntity<?> endCall(@RequestBody Map<String, Object> request) {
        try {
            Long callId = convertToLong(request.get("callId"));
            String reason = request.get("reason") != null ? (String) request.get("reason") : "normal";
            Long userId = getCurrentUserId();

            log.info("📞 END CALL - userId: {}, callId: {}, reason: {}", userId, callId, reason);

            CallDTO call = callService.endCall(callId, reason, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã kết thúc cuộc gọi");
            response.put("data", call);

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
     * Từ chối cuộc gọi
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
            response.put("data", call);

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
            response.put("data", call);

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
     * Lấy danh sách cuộc gọi đang active
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveCalls() {
        try {
            Long userId = getCurrentUserId();
            log.info("📞 GET ACTIVE CALLS - userId: {}", userId);

            List<CallDTO> calls = callService.getActiveCalls();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", calls);

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