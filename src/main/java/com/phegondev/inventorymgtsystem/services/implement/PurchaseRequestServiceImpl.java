package com.phegondev.inventorymgtsystem.services.implement;

import com.phegondev.inventorymgtsystem.dtos.PurchaseRequestDTO;
import com.phegondev.inventorymgtsystem.dtos.Response;
import com.phegondev.inventorymgtsystem.enums.TransactionStatus;
import com.phegondev.inventorymgtsystem.enums.TransactionType;
import com.phegondev.inventorymgtsystem.exceptions.NotFoundException;
import com.phegondev.inventorymgtsystem.models.*;
import com.phegondev.inventorymgtsystem.repositories.*;
import com.phegondev.inventorymgtsystem.services.EmailService;
import com.phegondev.inventorymgtsystem.services.NotificationService;
import com.phegondev.inventorymgtsystem.services.PurchaseRequestService;
import com.phegondev.inventorymgtsystem.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseRequestServiceImpl implements PurchaseRequestService {

    private final PurchaseRequestRepository purchaseRequestRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final ProductItemRepository productItemRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Override
    public Response createRequest(PurchaseRequestDTO dto) {
        User currentUser = userService.getCurrentLoggedInUser();

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm"));
        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà cung cấp"));

        PurchaseRequest request = PurchaseRequest.builder()
                .product(product)
                .supplier(supplier)
                .createdBy(currentUser)
                .quantity(dto.getQuantity())
                .purchasePrice(dto.getPurchasePrice())
                .note(dto.getNote())
                .status(TransactionStatus.AWAITING_APPROVAL)
                .build();

        purchaseRequestRepository.save(request);

        return Response.builder()
                .status(200)
                .message("Yêu cầu nhập hàng đã được gửi, đang chờ Admin phê duyệt.")
                .build();
    }

    @Override
    public Response getAllRequests() {
        List<PurchaseRequest> requests = purchaseRequestRepository.findAllByOrderByCreatedAtDesc();
        return Response.builder()
                .status(200)
                .message("success")
                .purchaseRequests(requests.stream().map(this::toDTO).collect(Collectors.toList()))
                .build();
    }

    @Override
    public Response getMyRequests() {
        User currentUser = userService.getCurrentLoggedInUser();
        List<PurchaseRequest> requests = purchaseRequestRepository.findByCreatedByIdOrderByCreatedAtDesc(currentUser.getId());
        return Response.builder()
                .status(200)
                .message("success")
                .purchaseRequests(requests.stream().map(this::toDTO).collect(Collectors.toList()))
                .build();
    }

    @Override
    public Response approveRequest(Long id) {
        User admin = userService.getCurrentLoggedInUser();
        PurchaseRequest request = purchaseRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy yêu cầu"));

        // Tạo token bảo mật dùng 1 lần để nhúng vào link email NCC
        String token = UUID.randomUUID().toString();

        request.setStatus(TransactionStatus.APPROVED);
        request.setReviewedBy(admin);
        request.setReviewedAt(LocalDateTime.now());
        request.setConfirmToken(token);
        purchaseRequestRepository.save(request);

        // Gửi email HTML có 2 nút Chấp nhận / Từ chối cho NCC
        emailService.sendPurchaseRequestEmail(request);

        // Gửi thông báo cho MANAGER
        notificationService.createNotification(
                request.getCreatedBy(),
                "✅ Yêu cầu nhập hàng được duyệt",
                "Yêu cầu nhập " + request.getQuantity() + " " + request.getProduct().getName()
                        + " đã được Admin phê duyệt. Email đã gửi cho nhà cung cấp, vui lòng chờ xác nhận.",
                "APPROVED",
                "/purchase-request"
        );

        return Response.builder()
                .status(200)
                .message("Đã phê duyệt yêu cầu và gửi email cho nhà cung cấp.")
                .build();
    }

    @Override
    public Response rejectRequest(Long id, String reason) {
        User admin = userService.getCurrentLoggedInUser();
        PurchaseRequest request = purchaseRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy yêu cầu"));

        request.setStatus(TransactionStatus.REJECTED);
        request.setReviewedBy(admin);
        request.setReviewedAt(LocalDateTime.now());
        request.setRejectReason(reason);
        purchaseRequestRepository.save(request);

        // Gửi thông báo cho MANAGER
        notificationService.createNotification(
                request.getCreatedBy(),
                "❌ Yêu cầu nhập hàng bị từ chối",
                "Yêu cầu nhập " + request.getQuantity() + " " + request.getProduct().getName()
                        + " đã bị từ chối. Lý do: " + reason,
                "REJECTED",
                "/purchase-request"
        );

        return Response.builder()
                .status(200)
                .message("Đã từ chối yêu cầu nhập hàng.")
                .build();
    }

    /**
     * NCC bấm link trong email → hệ thống xử lý tự động.
     * Endpoint này PUBLIC (không cần JWT).
     *
     * - action=accept → tạo Transaction WAITING_DELIVERY, xóa token
     * - action=reject → đánh dấu SUPPLIER_REJECTED, xóa token
     */
    @Override
    public Response handleSupplierConfirm(String token, String action) {
        PurchaseRequest request = purchaseRequestRepository.findByConfirmToken(token)
                .orElseThrow(() -> new NotFoundException("Link xác nhận không hợp lệ hoặc đã được sử dụng."));

        // Chỉ cho phép xác nhận khi đơn đang ở trạng thái APPROVED
        if (request.getStatus() != TransactionStatus.APPROVED) {
            return Response.builder()
                    .status(400)
                    .message("Đơn hàng này đã được xử lý trước đó.")
                    .build();
        }

        if ("accept".equalsIgnoreCase(action)) {
            // Tạo Transaction với status WAITING_DELIVERY → hiển thị trong TransactionPage
            BigDecimal totalPrice = request.getPurchasePrice()
                    .multiply(BigDecimal.valueOf(request.getQuantity()));

            Transaction transaction = Transaction.builder()
                    .transactionType(TransactionType.PURCHASE)
                    .status(TransactionStatus.WAITING_DELIVERY)
                    .product(request.getProduct())
                    .supplier(request.getSupplier())
                    .user(request.getCreatedBy())
                    .totalProducts(request.getQuantity())
                    .purchasePrice(request.getPurchasePrice())
                    .totalPrice(totalPrice)
                    .note(request.getNote())
                    .build();
            transactionRepository.save(transaction);

            // Cập nhật PurchaseRequest: lưu transactionId để Manager biết cần complete cái nào
            request.setStatus(TransactionStatus.WAITING_DELIVERY);
            request.setConfirmToken(null); // Vô hiệu hóa token sau khi dùng
            purchaseRequestRepository.save(request);

            notificationService.createNotification(
                    request.getCreatedBy(),
                    "🚚 Nhà cung cấp đã xác nhận đơn hàng",
                    "Nhà cung cấp " + request.getSupplier().getName()
                            + " đã chấp nhận giao " + request.getQuantity() + " " + request.getProduct().getName()
                            + ". Vui lòng chuẩn bị nhận hàng và nhập serial.",
                    "SUPPLIER_ACCEPTED",
                    "/purchase-request"
            );

            log.info("NCC xác nhận đơn hàng #{}, đã tạo Transaction #{}", request.getId(), transaction.getId());

            return Response.builder()
                    .status(200)
                    .message("Cảm ơn bạn đã xác nhận! Đơn hàng đã được ghi nhận, chúng tôi sẽ chờ hàng từ bạn.")
                    .build();

        } else if ("reject".equalsIgnoreCase(action)) {
            request.setStatus(TransactionStatus.SUPPLIER_REJECTED);
            request.setConfirmToken(null); // Vô hiệu hóa token
            purchaseRequestRepository.save(request);

            notificationService.createNotification(
                    request.getCreatedBy(),
                    "⚠️ Nhà cung cấp từ chối đơn hàng",
                    "Nhà cung cấp " + request.getSupplier().getName()
                            + " đã từ chối giao " + request.getQuantity() + " " + request.getProduct().getName()
                            + ". Vui lòng tạo yêu cầu mới hoặc chọn nhà cung cấp khác.",
                    "SUPPLIER_REJECTED",
                    "/purchase-request"
            );

            log.info("NCC từ chối đơn hàng #{}", request.getId());

            return Response.builder()
                    .status(200)
                    .message("Bạn đã từ chối đơn hàng. Cảm ơn đã phản hồi.")
                    .build();

        } else {
            return Response.builder()
                    .status(400)
                    .message("Hành động không hợp lệ.")
                    .build();
        }
    }

    /**
     * Manager nhập serial + xác nhận → Transaction chuyển COMPLETED, kho được cập nhật.
     * Chỉ cho phép khi PurchaseRequest đang ở WAITING_DELIVERY.
     */
    @Override
    public Response completeRequest(Long id, List<String> serialNumbers) {
        PurchaseRequest request = purchaseRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy yêu cầu"));

        if (request.getStatus() != TransactionStatus.WAITING_DELIVERY) {
            return Response.builder()
                    .status(400)
                    .message("Chỉ có thể hoàn tất nhập kho khi nhà cung cấp đã xác nhận giao hàng.")
                    .build();
        }

        if (serialNumbers == null || serialNumbers.size() != request.getQuantity()) {
            return Response.builder()
                    .status(400)
                    .message("Số lượng Serial không khớp. Cần: " + request.getQuantity())
                    .build();
        }

        // Tìm Transaction WAITING_DELIVERY tương ứng và cập nhật thành COMPLETED
        Transaction transaction = transactionRepository
                .findTopByProductIdAndTransactionTypeOrderByIdDesc(
                        request.getProduct().getId(), TransactionType.PURCHASE)
                .filter(t -> t.getStatus() == TransactionStatus.WAITING_DELIVERY)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy giao dịch chờ giao hàng tương ứng."));

        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setUpdateAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        // Tạo từng ProductItem (serial)
        for (String serial : serialNumbers) {
            ProductItem item = new ProductItem();
            item.setSerialNumber(serial);
            item.setProduct(request.getProduct());
            item.setStatus("IN_STOCK");
            productItemRepository.save(item);
        }

        // Cập nhật tổng số lượng sản phẩm
        Product product = request.getProduct();
        product.setStockQuantity(
                (product.getStockQuantity() == null ? 0 : product.getStockQuantity()) + request.getQuantity()
        );
        productRepository.save(product);

        // Đánh dấu PurchaseRequest hoàn thành
        request.setStatus(TransactionStatus.COMPLETED);
        purchaseRequestRepository.save(request);

        return Response.builder()
                .status(200)
                .message("Nhập kho thành công! Đã thêm " + request.getQuantity() + " sản phẩm vào kho.")
                .build();
    }

    private PurchaseRequestDTO toDTO(PurchaseRequest r) {
        PurchaseRequestDTO dto = new PurchaseRequestDTO();
        dto.setId(r.getId());
        dto.setProductId(r.getProduct().getId());
        dto.setProductName(r.getProduct().getName());
        dto.setSupplierId(r.getSupplier().getId());
        dto.setSupplierName(r.getSupplier().getName());
        dto.setSupplierEmail(r.getSupplier().getEmail());
        dto.setCreatedById(r.getCreatedBy().getId());
        dto.setCreatedByName(r.getCreatedBy().getName());
        if (r.getReviewedBy() != null) {
            dto.setReviewedById(r.getReviewedBy().getId());
            dto.setReviewedByName(r.getReviewedBy().getName());
        }
        dto.setQuantity(r.getQuantity());
        dto.setPurchasePrice(r.getPurchasePrice());
        dto.setNote(r.getNote());
        dto.setRejectReason(r.getRejectReason());
        dto.setStatus(r.getStatus());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setReviewedAt(r.getReviewedAt());
        return dto;
    }
}