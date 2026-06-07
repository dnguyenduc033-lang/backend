package com.phegondev.inventorymgtsystem.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductSpecDTO {

    private Long id;

    private String specKey;   // Tên thông số (ví dụ: "RAM", "CPU")

    private String specValue; // Giá trị (ví dụ: "16GB", "Apple M3")

    private String groupName; // Gom nhóm (ví dụ: "Cấu hình", "Màn hình")

    private Long productId;   // ID của sản phẩm sở hữu thông số này

}