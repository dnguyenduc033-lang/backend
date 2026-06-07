package com.phegondev.inventorymgtsystem.controllers;

import com.phegondev.inventorymgtsystem.dtos.PurchaseRequestDTO;
import com.phegondev.inventorymgtsystem.dtos.Response;
import com.phegondev.inventorymgtsystem.services.PurchaseRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase-requests")
@RequiredArgsConstructor
public class PurchaseRequestController {

    private final PurchaseRequestService purchaseRequestService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Response> createRequest(@RequestBody PurchaseRequestDTO dto) {
        return ResponseEntity.ok(purchaseRequestService.createRequest(dto));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> getAllRequests() {
        return ResponseEntity.ok(purchaseRequestService.getAllRequests());
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Response> getMyRequests() {
        return ResponseEntity.ok(purchaseRequestService.getMyRequests());
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> approveRequest(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseRequestService.approveRequest(id));
    }

    @PutMapping("/bulk-approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> bulkApproveRequests(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = body.getOrDefault("ids", List.of());
        return ResponseEntity.ok(purchaseRequestService.bulkApproveRequests(ids));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> rejectRequest(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String reason = body.getOrDefault("reason", "Không có lý do cụ thể.");
        return ResponseEntity.ok(purchaseRequestService.rejectRequest(id, reason));
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Response> completeRequest(
            @PathVariable Long id,
            @RequestBody List<String> serialNumbers) {
        return ResponseEntity.ok(purchaseRequestService.completeRequest(id, serialNumbers));
    }

    /**
     * Endpoint PUBLIC — không cần đăng nhập.
     * NCC bấm link trong email → trình duyệt gọi GET endpoint này.
     * Trả về trang HTML thông báo kết quả để NCC thấy ngay trên trình duyệt.
     *
     * URL mẫu: GET /api/purchase-requests/confirm?token=xxx&action=accept
     *           GET /api/purchase-requests/confirm?token=xxx&action=reject
     */
    @GetMapping(value = "/confirm", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> supplierConfirm(
            @RequestParam String token,
            @RequestParam String action) {

        Response result = purchaseRequestService.handleSupplierConfirm(token, action);
        boolean isSuccess = result.getStatus() == 200;
        boolean isAccept  = "accept".equalsIgnoreCase(action);

        String html = buildResultPage(isSuccess, isAccept, result.getMessage());
        return ResponseEntity.ok(html);
    }

    private String buildResultPage(boolean isSuccess, boolean isAccept, String message) {
        String icon    = isSuccess ? (isAccept ? "✅" : "❌") : "⚠️";
        String title   = isSuccess ? (isAccept ? "Đã xác nhận đơn hàng" : "Đã từ chối đơn hàng") : "Không thể xử lý";
        String color   = isSuccess ? (isAccept ? "#00a884" : "#e11d48") : "#f59e0b";
        String bgColor = isSuccess ? (isAccept ? "#f0fdf4" : "#fff1f2") : "#fffbeb";

        return "<!DOCTYPE html><html lang='vi'><head><meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width,initial-scale=1.0'>" +
                "<title>" + title + "</title></head>" +
                "<body style='margin:0;padding:0;background:#f4f7f9;font-family:Arial,sans-serif;" +
                "display:flex;align-items:center;justify-content:center;min-height:100vh;'>" +
                "<div style='background:#fff;border-radius:20px;padding:48px 40px;text-align:center;" +
                "max-width:480px;width:90%;box-shadow:0 8px 32px rgba(0,0,0,0.08);border-top:5px solid " + color + ";'>" +
                "<div style='font-size:56px;margin-bottom:16px;'>" + icon + "</div>" +
                "<h1 style='margin:0 0 12px;font-size:22px;color:#0f172a;'>" + title + "</h1>" +
                "<div style='background:" + bgColor + ";border-radius:10px;padding:16px 20px;margin:20px 0;'>" +
                "<p style='margin:0;font-size:15px;color:#334155;line-height:1.6;'>" + message + "</p>" +
                "</div>" +
                "<p style='margin:0;font-size:13px;color:#94a3b8;'>Bạn có thể đóng trang này.</p>" +
                "</div></body></html>";
    }
}