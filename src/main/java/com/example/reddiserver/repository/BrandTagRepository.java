package com.example.reddiserver.repository;

import com.example.reddiserver.entity.Brand;
import com.example.reddiserver.entity.BrandTag;
import com.example.reddiserver.entity.enums.BrandTagType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandTagRepository extends JpaRepository<BrandTag, Long> {

	BrandTag findBrandTagByBrandAndBrandTagTypeAndTag(Brand brand, BrandTagType brandTagType, String tag);

	List<BrandTag> findAllByBrand(Brand brand);
}
