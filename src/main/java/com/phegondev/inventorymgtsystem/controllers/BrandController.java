package com.phegondev.inventorymgtsystem.controllers;

import com.phegondev.inventorymgtsystem.dtos.BrandDTO;
import com.phegondev.inventorymgtsystem.dtos.Response;
import com.phegondev.inventorymgtsystem.services.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @PostMapping("/add")
    public ResponseEntity<Response> createBrand(@RequestBody BrandDTO brandDTO) {
        return ResponseEntity.ok(brandService.createBrand(brandDTO));
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllBrands() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateBrand(@PathVariable Long id, @RequestBody BrandDTO brandDTO) {
        return ResponseEntity.ok(brandService.updateBrand(id, brandDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteBrand(@PathVariable Long id) {
        return ResponseEntity.ok(brandService.deleteBrand(id));
    }
}