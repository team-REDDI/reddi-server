package com.example.reddiserver.service;

import com.example.reddiserver.dto.brand.response.BrandContentsResponseDto;
import com.example.reddiserver.dto.brand.response.BrandResponseDto;
import com.example.reddiserver.entity.Brand;
import com.example.reddiserver.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {
	private final BrandRepository brandRepository;

	public List<BrandResponseDto> getBrandList() {
		List<Brand> brands = brandRepository.findAll();
		List<BrandResponseDto> brandResponseDtos = new ArrayList<>();

		System.out.println("111");

		for (Brand brand : brands) {
			brandResponseDtos.add(new BrandResponseDto(brand));
		}

		return brandResponseDtos;
	}

	public BrandContentsResponseDto getBrandById(Long id) {
		Brand brand = brandRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 없습니다. id=" + id));
		return new BrandContentsResponseDto(brand);
	}


}
