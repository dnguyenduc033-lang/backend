package com.phegondev.inventorymgtsystem.services.implement;

import com.phegondev.inventorymgtsystem.dtos.ProductDTO;
import com.phegondev.inventorymgtsystem.dtos.ProductSpecDTO;
import com.phegondev.inventorymgtsystem.dtos.Response;
import com.phegondev.inventorymgtsystem.exceptions.NotFoundException;
import com.phegondev.inventorymgtsystem.models.Category;
import com.phegondev.inventorymgtsystem.models.Product;
import com.phegondev.inventorymgtsystem.models.ProductItem;
import com.phegondev.inventorymgtsystem.models.ProductSpecification;
import com.phegondev.inventorymgtsystem.repositories.CategoryRepository;
import com.phegondev.inventorymgtsystem.repositories.ProductItemRepository;
import com.phegondev.inventorymgtsystem.repositories.ProductRepository;
import com.phegondev.inventorymgtsystem.repositories.ProductSpecificationRepository;
import com.phegondev.inventorymgtsystem.services.ProductService;
import com.cloudinary.Cloudinary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    // --- BỔ SUNG REPOSITORY ---
    private final ProductItemRepository productItemRepository; //
    private final ProductSpecificationRepository specRepository; //
    // THÊM DÒNG NÀY VÀO ĐÂY:
    private final Cloudinary cloudinary;

    // KHỐI CODE BẠN CHÈN VÀO TẠI ĐÂY:
    @jakarta.annotation.PostConstruct
    public void initModelMapper() {
        if (modelMapper.getTypeMap(Product.class, ProductDTO.class) == null) {
            modelMapper.typeMap(Product.class, ProductDTO.class).addMappings(mapper -> {
                // Tự động bóc tách ID danh mục gán vào trường phẳng categoryId của ProductDTO
                mapper.map(src -> src.getCategory().getId(), ProductDTO::setCategoryId);
            });
        }
    }

    @Override
    @Transactional
    public Response saveProduct(ProductDTO productDTO, MultipartFile imageFile) {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category Not Found"));

        Product productToSave = Product.builder()
                .name(productDTO.getName())
                .sku(productDTO.getSku())
                .price(productDTO.getPrice())
                .stockQuantity(0)
                .description(productDTO.getDescription())
                .warrantyMonths(productDTO.getWarrantyMonths())
                .minStockLevel(productDTO.getMinStockLevel())
                .category(category)
                .build();

        // Xử lý lưu Thông số kỹ thuật (Specs)
        if (productDTO.getSpecs() != null) {
            productToSave.setSpecs(productDTO.getSpecs().stream().map(specDTO -> {
                ProductSpecification spec = modelMapper.map(specDTO, ProductSpecification.class);
                spec.setProduct(productToSave);
                return spec;
            }).collect(Collectors.toList()));
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            productToSave.setImageUrl(saveImage(imageFile));
        }

        productRepository.save(productToSave);
        return Response.builder().status(200).message("Sản phẩm và chi tiết kho đã được lưu thành công").build();
    }

    @Override
    @Transactional
    public Response updateProduct(ProductDTO productDTO, MultipartFile imageFile) {
        Product existingProduct = productRepository.findById(productDTO.getProductId())
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        if (imageFile != null && !imageFile.isEmpty()) {
            existingProduct.setImageUrl(saveImage(imageFile));
        }

        if (productDTO.getCategoryId() != null && productDTO.getCategoryId() > 0) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category Not Found"));
            existingProduct.setCategory(category);
        }

        if (productDTO.getName() != null) existingProduct.setName(productDTO.getName());
        if (productDTO.getSku() != null) existingProduct.setSku(productDTO.getSku());
        if (productDTO.getPrice() != null) existingProduct.setPrice(productDTO.getPrice());
        if (productDTO.getStockQuantity() != null) existingProduct.setStockQuantity(productDTO.getStockQuantity());
        if (productDTO.getDescription() != null) existingProduct.setDescription(productDTO.getDescription());

        // <--- CHỈNH SỬA TẠI ĐÂY: Bổ sung cập nhật các trường mới trong hàm update --->
        if (productDTO.getWarrantyMonths() != null) existingProduct.setWarrantyMonths(productDTO.getWarrantyMonths());
        if (productDTO.getMinStockLevel() != null) existingProduct.setMinStockLevel(productDTO.getMinStockLevel());
        if (productDTO.getLocation() != null) existingProduct.setLocation(productDTO.getLocation().trim());

        productRepository.save(existingProduct);
        return Response.builder().status(200).message("Cập nhật sản phẩm thành công").build();
    }

    // === THỰC THI 3 PHƯƠNG THỨC MỚI MÀ CONTROLLER ĐANG GỌI ===

    @Override
    @Transactional
    public Response addSpecification(Long productId, String key, String value) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        ProductSpecification spec = new ProductSpecification();
        spec.setSpecKey(key);
        spec.setSpecValue(value);
        spec.setProduct(product);
        specRepository.save(spec);

        return Response.builder().status(200).message("Đã thêm thông số kỹ thuật thành công").build();
    }

    @Override
    public Response getProductItemBySerial(String serialNumber) {
        ProductItem item = productItemRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy số Serial/IMEI này"));

        // Map sang DTO nếu Response của bạn yêu cầu DTO, hoặc trả về trực tiếp tùy cấu hình Response
        return Response.builder()
                .status(200)
                .message("success")
                .productItem(modelMapper.map(item, com.phegondev.inventorymgtsystem.dtos.ProductItemDTO.class))
                .build();
    }

    @Override
    public Response getLowStockProducts() {
        List<Product> lowStockList = productRepository.findLowStockProducts();
        List<ProductDTO> productDTOList = modelMapper.map(lowStockList, new TypeToken<List<ProductDTO>>() {}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .products(productDTOList)
                .build();
    }
    // ---------

    @Override
    public Response getAllProducts() {
        List<Product> productList = productRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        if (modelMapper.getTypeMap(ProductSpecification.class, ProductSpecDTO.class) == null) {
            modelMapper.typeMap(ProductSpecification.class, ProductSpecDTO.class).addMappings(mapper -> {
                mapper.map(src -> src.getSpecKey(), ProductSpecDTO::setSpecKey);
                mapper.map(src -> src.getSpecValue(), ProductSpecDTO::setSpecValue);
                mapper.map(src -> src.getGroupName(), ProductSpecDTO::setGroupName);
            });
        }
        List<ProductDTO> productDTOList = modelMapper.map(productList, new TypeToken<List<ProductDTO>>() {}.getType());
        return Response.builder().status(200).message("success").products(productDTOList).build();
    }

    @Override
    public Response getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        // 1. Map các thông tin cơ bản
        ProductDTO dto = modelMapper.map(product, ProductDTO.class);

        // 2. Ép bóc tách thủ công danh sách Thông số kỹ thuật
        if (product.getSpecs() != null) {
            dto.setSpecs(product.getSpecs().stream()
                    .map(spec -> modelMapper.map(spec, ProductSpecDTO.class))
                    .collect(Collectors.toList()));
        }

        // 3. Ép bóc tách thủ công danh sách Mã Sê-ri
        if (product.getProductItems() != null) {
            dto.setProductItems(product.getProductItems().stream()
                    .map(item -> modelMapper.map(item, com.phegondev.inventorymgtsystem.dtos.ProductItemDTO.class))
                    .collect(Collectors.toList()));
        }

        return Response.builder()
                .status(200)
                .message("success")
                .product(dto)
                .build();
    }

    @Override
    public Response deleteProduct(Long productId) {
        productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product Not Found"));
        productRepository.deleteById(productId);
        return Response.builder().status(200).message("Xóa sản phẩm thành công").build();
    }

    @Override
    public Response searchProduct(String searchValue) {
        List<Product> productList = productRepository.searchProductSmart(searchValue);

        if (modelMapper.getTypeMap(ProductSpecification.class, ProductSpecDTO.class) == null) {
            modelMapper.typeMap(ProductSpecification.class, ProductSpecDTO.class).addMappings(mapper -> {
                mapper.map(src -> src.getSpecKey(), ProductSpecDTO::setSpecKey);
                mapper.map(src -> src.getSpecValue(), ProductSpecDTO::setSpecValue);
                mapper.map(src -> src.getGroupName(), ProductSpecDTO::setGroupName);
            });
        }
        List<ProductDTO> productDTOList = modelMapper.map(productList, new TypeToken<List<ProductDTO>>() {}.getType());
        return Response.builder().status(200).message("success").products(productDTOList).build();
    }

    private String saveImage(MultipartFile imageFile) {
        if (!imageFile.getContentType().startsWith("image/") || imageFile.getSize() > 1024 * 1024 * 1024) {
            throw new IllegalArgumentException("Only image files under 1GIG is allowed");
        }

        try {
            // Đẩy file thẳng lên Cloudinary và tự động cho vào folder riêng trên mạng
            Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(),
                    com.cloudinary.utils.ObjectUtils.asMap(
                            "folder", "inventory_management/products"
                    ));

            // Trả về đường link URL dạng https://res.cloudinary.com/... để lưu vào Database
            return uploadResult.get("secure_url").toString();

        } catch (Exception e) {
            throw new IllegalArgumentException("Error uploading Image to Cloud: " + e.getMessage());
        }
    }

    @Override
    public Response getProductsByDate(LocalDate date) {
        // Lấy từ 00:00:00 đến 23:59:59 của ngày được chọn
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.atTime(LocalTime.MAX);

        List<Product> productList = productRepository.findProductsByCreatedAtDate(startDate, endDate);

        List<ProductDTO> productDTOList = modelMapper.map(productList, new TypeToken<List<ProductDTO>>() {}.getType());
        return Response.builder().status(200).message("success").products(productDTOList).build();
    }
}