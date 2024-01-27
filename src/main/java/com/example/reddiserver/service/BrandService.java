package com.example.reddiserver.service;

import com.example.reddiserver.dto.brand.response.BrandContentsResponseDto;
import com.example.reddiserver.dto.brand.response.BrandResponseDto;
import com.example.reddiserver.entity.Brand;
import com.example.reddiserver.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {
	private final BrandRepository brandRepository;

	public Page<BrandResponseDto> getBrandList(Pageable pageable) {
		Page<Brand> brandPage = brandRepository.findAllBrands(pageable);
		return brandPage.map(BrandResponseDto::from);
	}

	public BrandContentsResponseDto getBrandById(Long id) {
		Brand brand = brandRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 없습니다. id=" + id));
		return BrandContentsResponseDto.from(brand);
	}


}
