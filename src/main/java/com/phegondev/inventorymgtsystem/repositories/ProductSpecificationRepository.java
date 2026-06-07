package com.phegondev.inventorymgtsystem.repositories;

import com.phegondev.inventorymgtsystem.models.ProductSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductSpecificationRepository extends JpaRepository<ProductSpecification, Long> {
    // Lấy toàn bộ cấu hình (RAM, CPU...) của một sản phẩm
    List<ProductSpecification> findByProductId(Long productId);
}
