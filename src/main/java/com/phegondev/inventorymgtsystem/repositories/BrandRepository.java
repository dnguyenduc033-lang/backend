package com.phegondev.inventorymgtsystem.repositories;

import com.phegondev.inventorymgtsystem.models.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}