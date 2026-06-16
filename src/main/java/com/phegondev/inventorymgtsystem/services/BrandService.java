package com.phegondev.inventorymgtsystem.services;

import com.phegondev.inventorymgtsystem.dtos.BrandDTO;
import com.phegondev.inventorymgtsystem.dtos.Response;

public interface BrandService {
    Response createBrand(BrandDTO brandDTO);
    Response getAllBrands();
    Response updateBrand(Long id, BrandDTO brandDTO);
    Response deleteBrand(Long id);
}