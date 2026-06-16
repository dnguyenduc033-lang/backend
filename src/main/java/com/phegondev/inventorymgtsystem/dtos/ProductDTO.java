package com.phegondev.inventorymgtsystem.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDTO {

    private Long id;

    private Long categoryId;
    private Long brandId;
    private String brandName;
    private Long productId;
    private Long supplierId;

    private String name;

    private String sku;

    private BigDecimal price;

    private Integer stockQuantity;

    private String description;

    private String location;

    private LocalDateTime expiryDate;
    private String imageUrl;

    private LocalDateTime createdAt;

    private Integer warrantyMonths;
    private Integer minStockLevel;

    // 🌟 CHẶN ĐỨNG VÒNG LẶP TUẦN HOÀN TẠI ĐÂY

    private List<ProductSpecDTO> specs;


    private List<ProductItemDTO> productItems;

    private CategoryDTO category;
}