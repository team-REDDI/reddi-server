package com.example.reddiserver.service;

import com.example.reddiserver.dto.brand.response.BrandContentsResponseDto;
import com.example.reddiserver.dto.brand.response.BrandResponseDto;
import com.example.reddiserver.entity.BookmarkBrand;
import com.example.reddiserver.entity.Brand;
import com.example.reddiserver.entity.Member;
import com.example.reddiserver.repository.BookmarkBrandRepository;
import com.example.reddiserver.repository.BrandRepository;
import com.example.reddiserver.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {
	private final BrandRepository brandRepository;
	private final BookmarkBrandRepository bookmarkBrandRepository;
	private final MemberRepository memberRepository;

	public Page<BrandResponseDto> getBrandList(Pageable pageable) {
		Page<Brand> brandPage = brandRepository.findAllBrands(pageable);
		return brandPage.map(brand -> BrandResponseDto.from(brand));
	}

	public BrandContentsResponseDto getBrandById(Long id) {
		Brand brand = brandRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 없습니다. id=" + id));
		return BrandContentsResponseDto.from(brand);
	}

	@Transactional
	public Long increaseViewCount(Long id) {
		Brand brand = brandRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 없습니다. id=" + id));
		brand.increaseViewCount();

		return brand.getView_count();
	}

	public List<BrandResponseDto> getTopNBrands(int topN) {
		List<Brand> topNBrands = brandRepository.findTopNByOrderByViewCountDescAndNameAsc(PageRequest.of(0, topN));

		List<BrandResponseDto> topNBrandList = new ArrayList<>();
		for (Brand brand : topNBrands) {
			topNBrandList.add(BrandResponseDto.from(brand));
		}
		return topNBrandList;
	}

	public List<BrandResponseDto> getBookmarkBrandList(Long memberId) {
		List<BookmarkBrand> bookmarkBrands = bookmarkBrandRepository.findByMemberId(memberId);

		List<BrandResponseDto> brandResponseDtoList = bookmarkBrands.stream()
				.map(bookmarkBrand -> BrandResponseDto.from(bookmarkBrand.getBrand()))
				.collect(Collectors.toList());

		return brandResponseDtoList;

	}

	@Transactional
	public boolean toggleBookmarkBrand(Long memberId, Long brandId) {
		BookmarkBrand bookmarkBrand = bookmarkBrandRepository.findByMemberIdAndBrandId(memberId, brandId);
		if (bookmarkBrand == null) {

			// memberId 로 member 조회
			Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다. id=" + memberId));


			// brandId 로 brand 조회
			Brand brand = brandRepository.findById(brandId).orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 없습니다. id=" + brandId));

			// bookmarkBrand 생성
			BookmarkBrand newBookmarkBrand = BookmarkBrand.builder()
					.member(member)
					.brand(brand)
					.build();

			// bookmarkBrand 저장
			bookmarkBrandRepository.save(newBookmarkBrand);

			// bookmarkBrand 저장 성공 시 true 리턴
			return true;

		} else {
			bookmarkBrandRepository.delete(bookmarkBrand);

			// bookmarkBrand 삭제 성공 시 false 리턴
			return false;
		}
	}

}
