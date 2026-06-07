package com.phegondev.inventorymgtsystem.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.phegondev.inventorymgtsystem.enums.TransactionStatus;
import com.phegondev.inventorymgtsystem.enums.TransactionType;
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
public class TransactionDTO {

    private Long id;

    private Long userId;
    private Long productId;
    private Long supplierId;

    private Integer totalProducts;

    private BigDecimal totalPrice;

    private BigDecimal purchasePrice;
    private String purchaseType;

    private BigDecimal profit; // Trả về lợi nhuận

    private TransactionType transactionType; // pruchase, sale, return

    private TransactionStatus status; //pending, completed, processing

    private String description;
    private String note;

    // --- MỚI: Bổ sung theo dõi bảo hành đồ công nghệ ---
    private LocalDateTime warrantyExpiryDate;
    // -----------------------------------------------

    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    private ProductDTO product;

    private UserDTO user;

    private SupplierDTO supplier;


}