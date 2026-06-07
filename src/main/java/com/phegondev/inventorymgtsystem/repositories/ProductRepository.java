package com.phegondev.inventorymgtsystem.repositories;

import com.phegondev.inventorymgtsystem.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingOrDescriptionContaining(String name, String description);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.minStockLevel")
    List<Product> findLowStockProducts();

    // 🌟 THÊM HÀM NÀY: Truy vấn thông minh liên kết (JOIN) sang bảng Specs để quét được cả trường 'Hãng'
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.specs s " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :input, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :input, '%')) " +
            "OR (LOWER(s.specKey) = 'hãng' AND LOWER(s.specValue) LIKE LOWER(CONCAT('%', :input, '%')))")
    List<Product> searchProductSmart(@Param("input") String input);
}
