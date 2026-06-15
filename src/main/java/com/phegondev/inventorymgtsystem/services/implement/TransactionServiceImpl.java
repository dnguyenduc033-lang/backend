package com.phegondev.inventorymgtsystem.services.implement;

import com.phegondev.inventorymgtsystem.dtos.ProductItemDTO;
import com.phegondev.inventorymgtsystem.dtos.Response;
import com.phegondev.inventorymgtsystem.dtos.TransactionDTO;
import com.phegondev.inventorymgtsystem.dtos.TransactionRequest;
import com.phegondev.inventorymgtsystem.enums.TransactionStatus;
import com.phegondev.inventorymgtsystem.enums.TransactionType;
import com.phegondev.inventorymgtsystem.exceptions.NameValueRequiredException;
import com.phegondev.inventorymgtsystem.exceptions.NotFoundException;
import com.phegondev.inventorymgtsystem.models.Product;
import com.phegondev.inventorymgtsystem.models.ProductItem;
import com.phegondev.inventorymgtsystem.models.Supplier;
import com.phegondev.inventorymgtsystem.models.Transaction;
import com.phegondev.inventorymgtsystem.models.User;
import com.phegondev.inventorymgtsystem.repositories.ProductItemRepository;
import com.phegondev.inventorymgtsystem.repositories.ProductRepository;
import com.phegondev.inventorymgtsystem.repositories.SupplierRepository;
import com.phegondev.inventorymgtsystem.repositories.TransactionRepository;
import com.phegondev.inventorymgtsystem.services.TransactionService;
import com.phegondev.inventorymgtsystem.services.UserService;
import com.phegondev.inventorymgtsystem.specification.TransactionFilter;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.BaseFont;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.math.RoundingMode;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final ProductItemRepository productItemRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;


    @Override
    public Response returnFromCustomer(TransactionRequest transactionRequest) {
        Long productId = transactionRequest.getProductId();
        Integer quantity = transactionRequest.getQuantity();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));


        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);

        User currentUser = userService.getCurrentLoggedInUser();

        // Lấy giao dịch SALE gần nhất để biết giá bán và profit đã ghi nhận
        Transaction lastSale = transactionRepository
                .findTopByProductIdAndTransactionTypeOrderByIdDesc(productId, TransactionType.SALE)
                .orElse(null);

        // Tính giá bán thực tế (dùng giá bán lúc bán, fallback về giá hiện tại)
        BigDecimal sellPrice = (lastSale != null && lastSale.getTotalPrice() != null)
                ? lastSale.getTotalPrice().divide(BigDecimal.valueOf(lastSale.getTotalProducts()), 2, RoundingMode.HALF_UP)
                : product.getPrice();
        BigDecimal totalReturnPrice = sellPrice.multiply(BigDecimal.valueOf(quantity));

        // Tính profit cần trừ (âm vì là hoàn trả)
        BigDecimal profitToDeduct = (lastSale != null && lastSale.getProfit() != null && lastSale.getTotalProducts() > 0)
                ? lastSale.getProfit()
                .divide(BigDecimal.valueOf(lastSale.getTotalProducts()), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(quantity))
                .negate()
                : BigDecimal.ZERO;

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.CUSTOMER_RETURN)
                .status(TransactionStatus.COMPLETED)
                .product(product)
                .totalProducts(quantity)
                .totalPrice(totalReturnPrice.negate())
                .purchasePrice(sellPrice)

                .profit(profitToDeduct)
                .description(transactionRequest.getDescription() != null ? transactionRequest.getDescription() : "Nhận trả hàng từ khách hàng")
                .note(transactionRequest.getNote())
                .user(currentUser)
                .build();

        transactionRepository.save(transaction);

        List<String> serialNumbers = transactionRequest.getSerialNumbers();
        if (serialNumbers != null && !serialNumbers.isEmpty()) {
            for (String serial : serialNumbers) {
                ProductItem item = productItemRepository.findBySerialNumber(serial)
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy mã Sê-ri " + serial));

                if (!"SOLD".equals(item.getStatus())) {
                    throw new NameValueRequiredException("Mã Sê-ri " + serial + " chưa được bán ra.");
                }
                item.setStatus("AVAILABLE");

                // BỔ SUNG QUAN TRỌNG: Gán giao dịch vừa tạo vào ProductItem
                item.setTransaction(transaction);

                productItemRepository.save(item); // Bây giờ lưu mới có đầy đủ transaction_id
            }
        }

        return Response.builder()
                .status(200)
                .message("Nhận trả hàng từ khách thành công")
                .build();
    }

    @Override
    public Response sell(TransactionRequest transactionRequest) {
        Long productId = transactionRequest.getProductId();
        Integer quantity = transactionRequest.getQuantity();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        if (product.getStockQuantity() < quantity) {
            throw new NameValueRequiredException("Not Enough Stock For This Product");
        }

        User currentUser = userService.getCurrentLoggedInUser();
        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));

        // Lấy giá vốn từ lần nhập kho gần nhất — bắt buộc phải có, không tự đoán
        BigDecimal actualPurchasePrice = transactionRepository
                .findTopByProductIdAndTransactionTypeOrderByIdDesc(productId, TransactionType.PURCHASE)
                .map(Transaction::getPurchasePrice)
                .orElseThrow(() -> new NameValueRequiredException(
                        "Sản phẩm chưa có lịch sử nhập kho. Vui lòng nhập kho trước khi bán."));

        // Tính lợi nhuận = (Giá bán - Giá vốn nhập) × Số lượng
        BigDecimal profitPerUnit = product.getPrice().subtract(actualPurchasePrice);
        BigDecimal totalProfit = profitPerUnit.multiply(BigDecimal.valueOf(quantity));

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.SALE)
                .status(TransactionStatus.COMPLETED)
                .product(product)
                .totalProducts(quantity)
                .totalPrice(totalPrice)
                .purchasePrice(actualPurchasePrice)
                .profit(totalProfit)
                .description(transactionRequest.getDescription())
                .note(transactionRequest.getNote())
                .user(currentUser)
                .build();

        transactionRepository.save(transaction);

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);

        List<String> serialNumbers = transactionRequest.getSerialNumbers();
        if (serialNumbers != null && !serialNumbers.isEmpty()) {
            for (String serial : serialNumbers) {
                ProductItem item = productItemRepository.findBySerialNumber(serial)
                        .orElseThrow(() -> new NotFoundException("Serial Number " + serial + " Not Found"));
                item.setStatus("SOLD");

                item.setTransaction(transaction);
                productItemRepository.save(item);
            }
        }

        return Response.builder()
                .status(200)
                .message("Sale Successful")
                .build();
    }
    @Override
    public Response returnToSupplier(TransactionRequest transactionRequest) {
        Long productId = transactionRequest.getProductId();
        Long supplierId = transactionRequest.getSupplierId();
        // ✅ CHUẨN BIẾN GỐC: Sử dụng đúng getQuantity() từ TransactionRequest của bạn
        Integer quantity = transactionRequest.getQuantity();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new NotFoundException("Supplier Not Found"));

        if (product.getStockQuantity() < quantity) {
            throw new NameValueRequiredException("Not Enough Stock In Inventory To Return");
        }

        User currentUser = userService.getCurrentLoggedInUser();
        BigDecimal purchasePrice = transactionRepository
                .findTopByProductIdAndTransactionTypeOrderByIdDesc(productId, TransactionType.PURCHASE)
                .map(Transaction::getPurchasePrice)
                .orElseThrow(() -> new NameValueRequiredException(
                        "Sản phẩm chưa có lịch sử nhập kho. Không thể xác định giá vốn để trả hàng."));
        BigDecimal totalPrice = purchasePrice.multiply(BigDecimal.valueOf(quantity));

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.RETURN_TO_SUPPLIER)
                .status(TransactionStatus.COMPLETED)
                .product(product)
                .supplier(supplier)
                .totalProducts(quantity)
                .totalPrice(totalPrice)
                .purchasePrice(purchasePrice)
                .profit(BigDecimal.ZERO)
                .description(transactionRequest.getDescription())
                .note(transactionRequest.getNote())
                .user(currentUser)
                .build();

        transactionRepository.save(transaction);

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);

        List<String> serialNumbers = transactionRequest.getSerialNumbers();
        if (serialNumbers != null && !serialNumbers.isEmpty()) {
            for (String serial : serialNumbers) {
                ProductItem item = productItemRepository.findBySerialNumber(serial)
                        .orElseThrow(() -> new NotFoundException("Serial Number " + serial + " Not Found"));
                item.setStatus("RETURNED_TO_SUPPLIER");
                item.setTransaction(transaction);
                productItemRepository.save(item);
            }
        }

        return Response.builder()
                .status(200)
                .message("Return To Supplier Processed Successfully")
                .build();
    }

    @Override
    public Response getAllTransactions(int page, int size, String filter) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Specification<Transaction> spec = TransactionFilter.byFilter(filter);
        Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);

        List<TransactionDTO> transactionDTOS = modelMapper.map(transactionPage.getContent(), new TypeToken<List<TransactionDTO>>() {
        }.getType());

        transactionDTOS.forEach(transactionDTO -> {
            transactionDTO.setUser(null);
            transactionDTO.setProduct(null);
            transactionDTO.setSupplier(null);
        });

        // 🌟 TÍNH NĂNG MỚI: Bổ sung totalElements và totalPages để kích hoạt thanh phân trang ở Frontend
        return Response.builder()
                .status(200)
                .message("success")
                .transactions(transactionDTOS)
                .totalElements(transactionPage.getTotalElements())
                .totalPages(transactionPage.getTotalPages())
                .build();
    }

    @Override
    public Response getAllTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction Not Found"));

        // 1. Ánh xạ sang DTO bình thường (Lúc này ModelMapper đang tự động chèn rác)
        TransactionDTO dto = modelMapper.map(transaction, TransactionDTO.class);

        // 2. 🌟 BẺ GÃY VÒNG LẶP VÀ XÓA RÁC TỪ MODELMAPPER
        if (dto.getProduct() != null) {
            dto.getProduct().setCategory(null); // Bẻ liên kết Category sâu
            dto.getProduct().setProductItems(null); // DỌN SẠCH mớ sê-ri tổng của kho
        }
        if (dto.getUser() != null) {
            // Giữ lại các trường cơ bản của user
        }
        if (dto.getSupplier() != null) {
            // Giữ lại supplier để hiển thị
        }

        // 3. Lấy danh sách serial thực tế liên kết với riêng giao dịch này
        List<ProductItem> items = productItemRepository.findByTransactionId(transaction.getId());
        if (!items.isEmpty()) {
            List<ProductItemDTO> itemDTOs = modelMapper.map(items,
                    new TypeToken<List<ProductItemDTO>>(){}.getType());
            dto.setProductItems(itemDTOs); // Ghi đè sê-ri chuẩn của đơn
        } else {
            dto.setProductItems(new ArrayList<>()); // ÉP RỖNG nếu không có để xóa sạch rác của ModelMapper
        }

        // 4. Trả về Response sạch bóng
        return Response.builder()
                .status(200)
                .message("success")
                .transaction(dto)
                .build();
    }

    @Override
    public Response getAllTransactionByMonthAndYear(int month, int year) {
        Specification<Transaction> spec = TransactionFilter.byMonthAndYear(month, year);
        List<Transaction> transactions = transactionRepository.findAll(spec);

        List<TransactionDTO> transactionDTOS = modelMapper.map(transactions, new TypeToken<List<TransactionDTO>>() {
        }.getType());

        // 🌟 TÍNH NĂNG MỚI: Giữ lại Object Product để Frontend lấy dữ liệu tính doanh thu/lợi nhuận,
        // bẻ gãy liên kết sâu Category để tránh sập lỗi lặp cấu trúc tuần hoàn JSON.
        transactionDTOS.forEach(dto -> {
            if (dto.getProduct() != null) {
                dto.getProduct().setCategory(null);
            }
        });

        return Response.builder()
                .status(200)
                .message("success")
                .transactions(transactionDTOS)
                .build();
    }

    @Override
    public Response updateTransactionStatus(Long id, TransactionStatus status) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction Not Found"));
        transaction.setStatus(status);
        transactionRepository.save(transaction);
        return Response.builder().status(200).message("Status Updated").build();
    }

    @Override
    public Response checkWarrantyBySerial(String serialNumber) {
        // 1. Tìm sản phẩm trong kho
        ProductItem productItem = productItemRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new NotFoundException("Mã số Serial/IMEI này không tồn tại."));

        // 2. Map sang DTO chuẩn
        ProductItemDTO productItemDTO = modelMapper.map(productItem, ProductItemDTO.class);

        // 3. Xử lý thông tin ngày tháng bảo hành (Tìm giao dịch SALE gần nhất nếu đã bán)
        String warrantyInfo = "Chưa xuất kho|0"; // Format: NgayBan|SoThangBH
        // Mặc định ban đầu nếu chưa bán
        productItemDTO.setSoldDate(null);
        productItemDTO.setWarrantyMonths(productItem.getProduct().getWarrantyMonths() != null ? productItem.getProduct().getWarrantyMonths() : 0);

        // Nếu sản phẩm đã bán, tìm giao dịch SALE gần nhất để lấy ngày giao dịch thực tế
        if ("SOLD".equals(productItem.getStatus())) {
            transactionRepository.findTopByProductIdAndTransactionTypeOrderByIdDesc(
                    productItem.getProduct().getId(),
                    TransactionType.SALE
            ).ifPresent(lastSale -> {
                // Lấy ngày tạo (createdAt) của giao dịch bán này gán làm ngày xuất kho bảo hành
                productItemDTO.setSoldDate(lastSale.getCreatedAt());
            });
        }

        return Response.builder()
                .status(200)
                .message("success")
                .productItem(productItemDTO) // Dùng đúng đối tượng DTO đã khai báo trong Response.java
                .build();
    }

    @Override
    public Response updateStatus(Long id, TransactionStatus status) {
        return null;
    }

    @Override
    public List<String> extractSerialsFromExcel(MultipartFile file) {
        List<String> extractedSerials = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream();
             org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(inputStream)) {

            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);

            for (org.apache.poi.ss.usermodel.Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                org.apache.poi.ss.usermodel.Cell cell = row.getCell(0);
                if (cell != null) {
                    String serial = "";
                    if (cell.getCellType() == org.apache.poi.ss.usermodel.CellType.STRING) {
                        serial = cell.getStringCellValue().trim();
                    } else if (cell.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
                        serial = String.valueOf((long) cell.getNumericCellValue()).trim();
                    }

                    if (!serial.isEmpty()) {
                        extractedSerials.add(serial);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Lỗi khi đọc file Excel: ", e);
            throw new NameValueRequiredException("Không thể đọc file Excel. Vui lòng đảm bảo file đúng định dạng .xlsx");
        }
        return extractedSerials;
    }

    @Override
    public byte[] exportTransactionToPdf(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction Not Found"));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            // 1. TẢI FONT HỖ TRỢ TIẾNG VIỆT TỪ RESOURCES
            InputStream fontStream = getClass().getResourceAsStream("/fonts/times.ttf");
            if (fontStream == null) {
                throw new RuntimeException("Không tìm thấy file times.ttf. Vui lòng kiểm tra lại thư mục src/main/resources/fonts/");
            }
            byte[] fontBytes = fontStream.readAllBytes();
            BaseFont bf = BaseFont.createFont("times.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, fontBytes, null);

            Font titleFont = new Font(bf, 18, Font.BOLD);
            Font boldFont = new Font(bf, 12, Font.BOLD);
            Font normalFont = new Font(bf, 12, Font.NORMAL);

            // 2. DỊCH TỪ KHÓA & FORMAT DỮ LIỆU
            String loaiGiaoDich = switch (transaction.getTransactionType().name()) {
                case "PURCHASE" -> "Nhập kho";
                case "SALE", "SELL" -> "Xuất kho";
                case "RETURN_TO_SUPPLIER" -> "Trả hàng cho nhà cung cấp";
                case "CUSTOMER_RETURN" -> "Khách trả hàng";
                default -> transaction.getTransactionType().name();
            };

            String trangThai = switch (transaction.getStatus().name()) {
                case "COMPLETED" -> "Hoàn thành";
                case "PENDING" -> "Chờ xử lý";
                case "PROCESSING" -> "Đang xử lý";
                case "CANCELLED" -> "Đã hủy";
                default -> transaction.getStatus().name();
            };

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String ngayTao = transaction.getCreatedAt() != null ? transaction.getCreatedAt().format(dateFormatter) : "N/A";

            NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
            String tongTien = transaction.getTotalPrice() != null ? currencyFormat.format(transaction.getTotalPrice()) : "0";

            // --- 3. BẮT ĐẦU VẼ NỘI DUNG PDF ---
            Paragraph title = new Paragraph("HỒ SƠ CHỨNG TỪ - GIAO DỊCH #" + transaction.getId(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ", normalFont));

            document.add(new Paragraph("Loại Giao Dịch: " + loaiGiaoDich, boldFont));
            document.add(new Paragraph("Trạng Thái: " + trangThai, boldFont));
            document.add(new Paragraph("Ngày Tạo: " + ngayTao, normalFont));
            document.add(new Paragraph("Tổng Giá Trị: " + tongTien + " VNĐ", boldFont));

            document.add(new Paragraph(" ", normalFont));
            document.add(new Paragraph("--- THÔNG TIN THIẾT BỊ ---", boldFont));
            if (transaction.getProduct() != null) {
                document.add(new Paragraph("Tên Thiết Bị: " + transaction.getProduct().getName(), normalFont));
                document.add(new Paragraph("Mã SKU: " + transaction.getProduct().getSku(), normalFont));
                document.add(new Paragraph("Số Lượng: " + transaction.getTotalProducts() + " chiếc", normalFont));
            }

            document.add(new Paragraph(" ", normalFont));
            document.add(new Paragraph("--- DANH SÁCH SERIAL / IMEI ---", boldFont));
            List<ProductItem> items = productItemRepository.findByTransactionId(transaction.getId());
            if(items != null && !items.isEmpty()){
                for(ProductItem item : items){
                    document.add(new Paragraph("- " + item.getSerialNumber(), normalFont));
                }
            } else {
                document.add(new Paragraph("Không có mã Serial đính kèm", normalFont));
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Lỗi khi tạo file PDF", e);
            throw new RuntimeException("Hệ thống không thể tạo file PDF. Chi tiết: " + e.getMessage());
        }
    }
}