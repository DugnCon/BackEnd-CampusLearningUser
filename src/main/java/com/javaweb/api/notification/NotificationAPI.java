package com.javaweb.api.notification;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/api")
public class NotificationAPI {
    
	/**
	 * ĐANG TRONG QUÁ TRÌNH KIỂM THỬ
	 * **/
    @GetMapping("/notifications")
    public ResponseEntity<Object> getNotifications() {
        List<Map<String, Object>> notifications = List.of(
            Map.of("id", 1, "title", "Thông báo hệ thống", "message", "Hệ thống sẽ bảo trì lúc 22h tối nay."),
            Map.of("id", 2, "title", "Cập nhật khóa học", "message", "Một khóa học mới đã được thêm."),
            Map.of("id", 3, "title", "Lịch thi", "message", "Lịch thi cuối kỳ đã được công bố.")
        );

        return ResponseEntity.ok(Map.of("notifications", notifications));
    }
}
