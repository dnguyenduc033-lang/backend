package com.phegondev.inventorymgtsystem.controllers;

import com.phegondev.inventorymgtsystem.dtos.Response;
import com.phegondev.inventorymgtsystem.dtos.TransactionRequest;
import com.phegondev.inventorymgtsystem.enums.TransactionStatus;
import com.phegondev.inventorymgtsystem.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {


    private final TransactionService transactionService;

    @PostMapping("/sell")
    public ResponseEntity<Response> makeSale(@RequestBody @Valid TransactionRequest transactionRequest) {
        return ResponseEntity.ok(transactionService.sell(transactionRequest));
    }

    @PostMapping("/return")
    public ResponseEntity<Response> returnToSupplier(@RequestBody @Valid TransactionRequest transactionRequest) {
        return ResponseEntity.ok(transactionService.returnToSupplier(transactionRequest));
    }

    @PostMapping("/return-from-customer")
    // Tạm thời tôi chưa nhét @PreAuthorize vào đây để khớp với file SecurityConfig gốc của bạn
    public ResponseEntity<Response> returnFromCustomer(@RequestBody @Valid TransactionRequest transactionRequest) {
        return ResponseEntity.ok(transactionService.returnFromCustomer(transactionRequest));
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size,
            @RequestParam(required = false) String filter) {

        System.out.println("SEARCH VALUE IS: " +filter);

        return ResponseEntity.ok(transactionService.getAllTransactions(page, size, filter));
    }


    @GetMapping("/{id}")
    public ResponseEntity<Response> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getAllTransactionById(id));
    }

    @GetMapping("/by-month-year")
    public ResponseEntity<Response> getTransactionByMonthAndYear(
            @RequestParam int month,
            @RequestParam int year) {

        return ResponseEntity.ok(transactionService.getAllTransactionByMonthAndYear(month, year));
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<Response> updateTransactionStatus(
            @PathVariable Long transactionId,
            @RequestBody TransactionStatus status) {

        return ResponseEntity.ok(transactionService.updateTransactionStatus(transactionId, status));
    }

    // TRA CỨU BẢO HÀNH ---
    @GetMapping("/warranty-check/{serialNumber}")
    public ResponseEntity<Response> checkWarranty(@PathVariable String serialNumber) {
        return ResponseEntity.ok(transactionService.checkWarrantyBySerial(serialNumber));
    }

    // CẬP NHẬT TRẠNG THÁI (Hủy đơn/Trả hàng) ---
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    public ResponseEntity<Response> updateStatusOnly(@PathVariable Long id,
                                                            @RequestParam TransactionStatus status) {
        return ResponseEntity.ok(transactionService.updateStatus(id, status));
    }

    // API NHẬN FILE EXCEL VÀ TRẢ VỀ DANH SÁCH SERI ---
    @PostMapping("/extract-serials")
    public ResponseEntity<List<String>> extractSerialsFromExcel(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(transactionService.extractSerialsFromExcel(file));
    }

    @GetMapping("/{id}/export-pdf")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id) {
        byte[] pdfBytes = transactionService.exportTransactionToPdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "GiaoDich_" + id + ".pdf");
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
