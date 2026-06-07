package com.phegondev.inventorymgtsystem.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime; // 🌟 Thêm import này để dùng kiểu dữ liệu thời gian

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductItemDTO {

    private Long id;

    private String serialNumber; // Số Serial hoặc IMEI của thiết bị

    private String status; // Trạng thái cụ thể: IN_STOCK, SOLD, DEFECTIVE

    private Long productId; // ID để mapping ngược lại dòng sản phẩm gốc

    // 🌟 THÊM 2 THUỘC TÍNH MỚI DƯỚI ĐÂY:
    private LocalDateTime soldDate; // Ngày xuất kho giao dịch thực tế của đơn hàng

    private Integer warrantyMonths; // Số tháng được bảo hành lấy từ dòng sản phẩm gốc
}