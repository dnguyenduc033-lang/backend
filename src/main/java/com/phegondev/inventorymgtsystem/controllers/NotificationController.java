package com.phegondev.inventorymgtsystem.controllers;

import com.phegondev.inventorymgtsystem.dtos.Response;
import com.phegondev.inventorymgtsystem.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // Lấy tất cả thông báo của user hiện tại
    @GetMapping("/my")
    public ResponseEntity<Response> getMyNotifications() {
        return ResponseEntity.ok(notificationService.getMyNotifications());
    }

    // Lấy số thông báo chưa đọc
    @GetMapping("/unread-count")
    public ResponseEntity<Response> getUnreadCount() {
        return ResponseEntity.ok(notificationService.getUnreadCount());
    }

    // Đánh dấu 1 thông báo đã đọc
    @PutMapping("/{id}/read")
    public ResponseEntity<Response> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    // Đánh dấu tất cả đã đọc
    @PutMapping("/read-all")
    public ResponseEntity<Response> markAllAsRead() {
        return ResponseEntity.ok(notificationService.markAllAsRead());
    }
}