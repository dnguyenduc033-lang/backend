package com.phegondev.inventorymgtsystem.repositories;

import com.phegondev.inventorymgtsystem.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
