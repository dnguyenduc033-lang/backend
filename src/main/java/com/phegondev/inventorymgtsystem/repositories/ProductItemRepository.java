package com.phegondev.inventorymgtsystem.repositories;

import com.phegondev.inventorymgtsystem.models.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {
    // Tìm chính xác một chiếc máy theo Serial/IMEI để kiểm tra bảo hành
    Optional<ProductItem> findBySerialNumber(String serialNumber);

    // Tìm tất cả các máy đang sẵn sàng trong kho của một dòng sản phẩm
    List<ProductItem> findByProductIdAndStatus(Long productId, String status);
}