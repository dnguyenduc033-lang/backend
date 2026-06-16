package com.phegondev.inventorymgtsystem.services.implement;

import com.phegondev.inventorymgtsystem.dtos.BrandDTO;
import com.phegondev.inventorymgtsystem.dtos.Response;
import com.phegondev.inventorymgtsystem.exceptions.NotFoundException;
import com.phegondev.inventorymgtsystem.models.Brand;
import com.phegondev.inventorymgtsystem.repositories.BrandRepository;
import com.phegondev.inventorymgtsystem.services.BrandService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;

    @Override
    public Response createBrand(BrandDTO brandDTO) {
        Brand brand = Brand.builder()
                .name(brandDTO.getName())
                .description(brandDTO.getDescription())
                .build();
        brandRepository.save(brand);
        return Response.builder().status(200).message("Thêm hãng sản xuất thành công").build();
    }

    @Override
    public Response getAllBrands() {
        List<Brand> brands = brandRepository.findAll();
        List<BrandDTO> brandDTOS = modelMapper.map(brands, new TypeToken<List<BrandDTO>>() {}.getType());
        return Response.builder().status(200).message("success").brands(brandDTOS).build();
    }

    @Override
    public Response updateBrand(Long id, BrandDTO brandDTO) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy hãng"));
        if (brandDTO.getName() != null) brand.setName(brandDTO.getName());
        if (brandDTO.getDescription() != null) brand.setDescription(brandDTO.getDescription());
        brandRepository.save(brand);
        return Response.builder().status(200).message("Cập nhật hãng thành công").build();
    }

    @Override
    public Response deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy hãng"));
        brandRepository.delete(brand);
        return Response.builder().status(200).message("Xóa hãng sản xuất thành công").build();
    }
}