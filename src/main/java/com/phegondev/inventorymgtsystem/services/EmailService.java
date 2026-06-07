package com.phegondev.inventorymgtsystem.services;

import com.phegondev.inventorymgtsystem.models.PurchaseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * URL gốc của backend, cấu hình trong application.properties:
     *   app.base-url=http://localhost:5050
     * NCC sẽ bấm vào link: {baseUrl}/api/purchase-requests/confirm?token=xxx&action=accept
     */
    @Value("${app.base-url:http://localhost:5050}")
    private String baseUrl;

    public void sendPurchaseRequestEmail(PurchaseRequest request) {
        try {
            String supplierEmail = request.getSupplier().getEmail();
            if (supplierEmail == null || supplierEmail.isBlank()) {
                log.warn("Nhà cung cấp {} không có email, bỏ qua gửi mail.", request.getSupplier().getName());
                return;
            }

            String acceptUrl  = baseUrl + "/api/purchase-requests/confirm?token=" + request.getConfirmToken() + "&action=accept";
            String rejectUrl  = baseUrl + "/api/purchase-requests/confirm?token=" + request.getConfirmToken() + "&action=reject";
            String managerEmail = request.getCreatedBy().getEmail();
            String managerName  = request.getCreatedBy().getName();

            NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            String formattedPrice = formatter.format(request.getPurchasePrice()) + " VNĐ";
            String formattedTotal = formatter.format(
                    request.getPurchasePrice().multiply(java.math.BigDecimal.valueOf(request.getQuantity()))
            ) + " VNĐ";

            String htmlContent = buildEmailHtml(
                    request.getSupplier().getName(),
                    request.getProduct().getName(),
                    request.getQuantity(),
                    formattedPrice,
                    formattedTotal,
                    request.getNote(),
                    managerName,
                    managerEmail,
                    acceptUrl,
                    rejectUrl
            );

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(supplierEmail);
            helper.setSubject("[YÊU CẦU ĐẶT HÀNG] " + request.getProduct().getName() + " — #" + request.getId());
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(message);
            log.info("Đã gửi email đặt hàng HTML tới: {}", supplierEmail);

        } catch (MessagingException e) {
            log.error("Lỗi khi gửi email tới nhà cung cấp: {}", e.getMessage());
        }
    }

    private String buildEmailHtml(
            String supplierName,
            String productName,
            int quantity,
            String unitPrice,
            String totalPrice,
            String note,
            String managerName,
            String managerEmail,
            String acceptUrl,
            String rejectUrl
    ) {
        String noteRow = (note != null && !note.isBlank())
                ? "<tr><td style='padding:10px 0;color:#64748b;font-size:14px;border-bottom:1px solid #f1f5f9;'>Ghi chú</td>"
                + "<td style='padding:10px 0;font-weight:600;font-size:14px;border-bottom:1px solid #f1f5f9;'>" + note + "</td></tr>"
                : "";

        return "<!DOCTYPE html>" +
                "<html lang='vi'><head><meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width,initial-scale=1.0'></head>" +
                "<body style='margin:0;padding:0;background:#f4f7f9;font-family:Arial,sans-serif;'>" +
                "<div style='max-width:600px;margin:40px auto;background:#ffffff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);'>" +

                // Header
                "<div style='background:linear-gradient(135deg,#00a884,#00876a);padding:36px 40px;text-align:center;'>" +
                "<h1 style='margin:0;color:#ffffff;font-size:24px;font-weight:800;letter-spacing:-0.5px;'>📦 Yêu Cầu Đặt Hàng</h1>" +
                "<p style='margin:8px 0 0;color:rgba(255,255,255,0.85);font-size:14px;'>Hệ thống Quản lý Kho</p>" +
                "</div>" +

                // Body
                "<div style='padding:36px 40px;'>" +
                "<p style='margin:0 0 24px;font-size:15px;color:#334155;'>Kính gửi <strong>" + supplierName + "</strong>,</p>" +
                "<p style='margin:0 0 24px;font-size:14px;color:#64748b;line-height:1.6;'>" +
                "Chúng tôi xin gửi yêu cầu đặt hàng với thông tin chi tiết như bên dưới. " +
                "Vui lòng xem xét và phản hồi bằng cách bấm một trong hai nút ở cuối email.</p>" +

                // Bảng thông tin đơn hàng
                "<div style='background:#f8fafc;border-radius:12px;padding:24px;margin-bottom:28px;border:1px solid #e2e8f0;'>" +
                "<h3 style='margin:0 0 16px;font-size:14px;font-weight:700;color:#00a884;text-transform:uppercase;letter-spacing:0.5px;'>Thông tin đơn hàng</h3>" +
                "<table style='width:100%;border-collapse:collapse;'>" +
                "<tr><td style='padding:10px 0;color:#64748b;font-size:14px;border-bottom:1px solid #f1f5f9;'>Sản phẩm</td>" +
                "<td style='padding:10px 0;font-weight:700;font-size:14px;border-bottom:1px solid #f1f5f9;color:#0f172a;'>" + productName + "</td></tr>" +
                "<tr><td style='padding:10px 0;color:#64748b;font-size:14px;border-bottom:1px solid #f1f5f9;'>Số lượng</td>" +
                "<td style='padding:10px 0;font-weight:600;font-size:14px;border-bottom:1px solid #f1f5f9;'>" + quantity + " chiếc</td></tr>" +
                "<tr><td style='padding:10px 0;color:#64748b;font-size:14px;border-bottom:1px solid #f1f5f9;'>Đơn giá</td>" +
                "<td style='padding:10px 0;font-weight:600;font-size:14px;border-bottom:1px solid #f1f5f9;'>" + unitPrice + "</td></tr>" +
                "<tr><td style='padding:10px 0;color:#64748b;font-size:14px;'>Tổng giá trị</td>" +
                "<td style='padding:10px 0;font-weight:800;font-size:16px;color:#00a884;'>" + totalPrice + "</td></tr>" +
                noteRow +
                "</table></div>" +

                // Hướng dẫn gửi Serial
                "<div style='background:#eff6ff;border-radius:12px;padding:20px 24px;margin-bottom:28px;border:1px solid #bfdbfe;'>" +
                "<h3 style='margin:0 0 10px;font-size:14px;font-weight:700;color:#1d4ed8;'>📋 Hướng dẫn sau khi chấp nhận</h3>" +
                "<p style='margin:0;font-size:13px;color:#334155;line-height:1.7;'>" +
                "Nếu bạn <strong>chấp nhận</strong> đơn hàng, sau khi chuẩn bị hàng xong, vui lòng gửi " +
                "<strong>danh sách mã Serial / IMEI</strong> của từng sản phẩm về địa chỉ email bên dưới " +
                "để chúng tôi tiến hành nhập kho:</p>" +
                "<p style='margin:12px 0 0;font-size:14px;'>" +
                "👤 <strong>" + managerName + "</strong><br>" +
                "📧 <a href='mailto:" + managerEmail + "' style='color:#00a884;font-weight:600;text-decoration:none;'>" + managerEmail + "</a>" +
                "</p></div>" +

                // Hai nút hành động
                "<div style='text-align:center;margin-bottom:8px;'>" +
                "<p style='margin:0 0 20px;font-size:14px;color:#64748b;'>Vui lòng xác nhận phản hồi của bạn:</p>" +
                "<a href='" + acceptUrl + "' style='display:inline-block;background:#00a884;color:#ffffff;text-decoration:none;" +
                "font-weight:700;font-size:15px;padding:14px 40px;border-radius:10px;margin:0 8px 12px;'>✅ Chấp nhận đơn hàng</a>" +
                "<a href='" + rejectUrl + "' style='display:inline-block;background:#ffffff;color:#e11d48;text-decoration:none;" +
                "font-weight:700;font-size:15px;padding:14px 40px;border-radius:10px;margin:0 8px 12px;" +
                "border:2px solid #fda4af;'>❌ Không chấp nhận</a>" +
                "</div>" +
                "<p style='text-align:center;font-size:12px;color:#94a3b8;margin-top:16px;'>Link xác nhận chỉ có hiệu lực một lần duy nhất.</p>" +

                "</div>" + // end body padding

                // Footer
                "<div style='background:#f8fafc;padding:20px 40px;text-align:center;border-top:1px solid #e2e8f0;'>" +
                "<p style='margin:0;font-size:12px;color:#94a3b8;'>Email này được gửi tự động từ Hệ thống Quản lý Kho. Vui lòng không trả lời email này.</p>" +
                "</div>" +

                "</div></body></html>";
    }
}