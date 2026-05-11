package com.phegondev.inventorymgtsystem.repositories;

import com.phegondev.inventorymgtsystem.models.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
