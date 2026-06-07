package com.phegondev.inventorymgtsystem.repositories;

import com.phegondev.inventorymgtsystem.enums.TransactionStatus;
import com.phegondev.inventorymgtsystem.models.PurchaseRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {
    List<PurchaseRequest> findAllByOrderByCreatedAtDesc();
    List<PurchaseRequest> findByStatusOrderByCreatedAtDesc(TransactionStatus status);
    List<PurchaseRequest> findByCreatedByIdOrderByCreatedAtDesc(Long userId);

    // Dùng để tra cứu khi NCC bấm link xác nhận trong email
    Optional<PurchaseRequest> findByConfirmToken(String confirmToken);
}