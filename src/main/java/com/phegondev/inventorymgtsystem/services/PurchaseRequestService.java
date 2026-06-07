package com.phegondev.inventorymgtsystem.services;

import com.phegondev.inventorymgtsystem.dtos.PurchaseRequestDTO;
import com.phegondev.inventorymgtsystem.dtos.Response;

import java.util.List;

public interface PurchaseRequestService {
    Response createRequest(PurchaseRequestDTO dto);
    Response getAllRequests();
    Response getMyRequests();
    Response approveRequest(Long id);
    Response rejectRequest(Long id, String reason);
    Response completeRequest(Long id, List<String> serialNumbers);

    // NCC bấm link trong email để xác nhận / từ chối đơn hàng
    Response handleSupplierConfirm(String token, String action);
}