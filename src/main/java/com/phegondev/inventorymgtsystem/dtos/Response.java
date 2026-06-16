package com.phegondev.inventorymgtsystem.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.phegondev.inventorymgtsystem.enums.UserRole;
import lombok.Builder;
import lombok.Data;
import com.phegondev.inventorymgtsystem.models.News;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    //Generic
    private int status;
    private String message;
    //for login
    private String token;
    private UserRole role;
    private String expirationTime;

    //for pagination
    private Integer totalPages;
    private Long totalElements;

    //data output optionals
    private UserDTO user;
    private List<UserDTO> users;
    private List<UserTreeNodeDTO> userTree;

    private SupplierDTO supplier;
    private List<SupplierDTO> suppliers;

    private CategoryDTO category;
    private List<CategoryDTO> categories;

    private BrandDTO brand;
    private List<BrandDTO> brands;

    private ProductDTO product;
    private List<ProductDTO> products;

    // === BỔ SUNG 2 DÒNG NÀY ĐỂ HẾT LỖI BIÊN DỊCH ===
    private ProductItemDTO productItem;
    private List<ProductItemDTO> productItems;

    private TransactionDTO transaction;
    private List<TransactionDTO> transactions;

    private PurchaseRequestDTO purchaseRequest;
    private List<PurchaseRequestDTO> purchaseRequests;
    private NotificationDTO notification;
    private List<NotificationDTO> notifications;

    private final LocalDateTime timestamp = LocalDateTime.now();

    private List<News> newsList; // <--- THÊM DÒNG NÀY VÀO
}
