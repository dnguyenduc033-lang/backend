package com.phegondev.inventorymgtsystem.enums;

public enum TransactionStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    CANCELLED,
    RETURNED,
    AWAITING_APPROVAL,   // MANAGER vừa tạo, chờ ADMIN duyệt
    APPROVED,            // ADMIN đã duyệt, email đã gửi cho NCC
    REJECTED,            // ADMIN từ chối
    WAITING_DELIVERY,    // NCC đã xác nhận, chờ hàng về
    SUPPLIER_REJECTED    // NCC từ chối đơn hàng
}