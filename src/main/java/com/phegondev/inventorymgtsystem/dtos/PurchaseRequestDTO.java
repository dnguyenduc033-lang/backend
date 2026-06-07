package com.phegondev.inventorymgtsystem.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.phegondev.inventorymgtsystem.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseRequestDTO {

    private Long id;
    private Long productId;
    private String productName;
    private Long supplierId;
    private String supplierName;
    private String supplierEmail;
    private Long createdById;
    private String createdByName;
    private Long reviewedById;
    private String reviewedByName;
    private Integer quantity;
    private BigDecimal purchasePrice;
    private String note;
    private String rejectReason;
    private TransactionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
}