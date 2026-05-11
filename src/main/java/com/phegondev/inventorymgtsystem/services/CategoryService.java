package com.phegondev.inventorymgtsystem.services;

import com.phegondev.inventorymgtsystem.dtos.CategoryDTO;
import com.phegondev.inventorymgtsystem.dtos.Response;

public interface CategoryService {

    Response createCategory(CategoryDTO categoryDTO);

    Response getAllCategories();

    Response getCategoryById(Long id);

    Response updateCategory(Long id, CategoryDTO categoryDTO);

    Response deleteCategory(Long id);
}
