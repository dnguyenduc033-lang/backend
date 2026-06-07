package com.phegondev.inventorymgtsystem.models;

import com.phegondev.inventorymgtsystem.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "purchase_requests")
@Data
@Builder
public class PurchaseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    private Integer quantity;

    @Column(precision = 15, scale = 2)
    private BigDecimal purchasePrice;

    private String note;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String rejectReason;

    /**
     * Token bảo mật dùng 1 lần, nhúng vào link trong email gửi NCC.
     * NCC bấm link Chấp nhận / Từ chối thì hệ thống xác minh token này.
     * Sau khi NCC phản hồi, token được xóa (set null) để không dùng lại được.
     */
    @Column(name = "confirm_token", unique = true)
    private String confirmToken;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = TransactionStatus.AWAITING_APPROVAL;
    }
}