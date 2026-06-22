package com.phegondev.inventorymgtsystem.services;

import com.phegondev.inventorymgtsystem.dtos.Response;
import com.phegondev.inventorymgtsystem.dtos.TransactionRequest;
import com.phegondev.inventorymgtsystem.enums.TransactionStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TransactionService {

    Response sell(TransactionRequest transactionRequest);

    Response returnToSupplier(TransactionRequest transactionRequest);

    Response returnFromCustomer(TransactionRequest transactionRequest);

    Response getAllTransactions(int page, int size, String filter);

    Response getAllTransactionById(Long id);

    Response getAllTransactionByMonthAndYear(int month, int year);

    Response updateTransactionStatus(Long transactionId, TransactionStatus status);

    Response checkWarrantyBySerial(String serialNumber);

    Response updateStatus(Long id, TransactionStatus status);

    List<String> extractSerialsFromExcel(MultipartFile file);

    byte[] exportTransactionToPdf(Long transactionId);

    byte[] exportWarrantyPdfBySerial(String serialNumber);
}
