package com.phegondev.inventorymgtsystem.services;

import com.phegondev.inventorymgtsystem.dtos.ProductDTO;
import com.phegondev.inventorymgtsystem.dtos.Response;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    Response saveProduct(ProductDTO productDTO, MultipartFile imageFile);

    Response updateProduct(ProductDTO productDTO, MultipartFile imageFile);

    Response getAllProducts();

    Response getProductById(Long id);

    Response deleteProduct(Long id);

    Response searchProduct(String input);

    Response getProductsByDate(java.time.LocalDate date);

    // --- BỔ SUNG ĐỂ HẾT LỖI ĐỎ Ở CONTROLLER ---
    Response addSpecification(Long productId, String key, String value);
    Response getProductItemBySerial(String serialNumber);
    Response getLowStockProducts();
}