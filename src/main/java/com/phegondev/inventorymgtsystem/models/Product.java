package com.phegondev.inventorymgtsystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
@Data
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Column(unique = true)
    @NotBlank(message = "SKU is required")
    private String sku;

    @Column(name = "cost_price", precision = 15, scale = 2)
    private BigDecimal costPrice;

    @Positive(message = "product price must be a positive value")
    private BigDecimal price;

    @Min(value = 0, message = "stock quantity cannot be negative")
    private Integer stockQuantity;

    @Column(name = "warranty_months")
    private Integer warrantyMonths;

    @Column(name = "min_stock_level")
    private Integer minStockLevel = 5;

    private String description;

    // --- BỔ SUNG TRƯỜNG VỊ TRÍ KHO HÀNG GỐC CỦA THIẾT BỊ ---
    @Column(name = "location")
    private String location;

    private LocalDateTime expiryDate;
    private String imageUrl;

    private final LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSpecification> specs;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductItem> productItems;

    public boolean isLowStock() {
        return this.stockQuantity <= this.minStockLevel;
    }
}