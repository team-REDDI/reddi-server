package com.example.reddiserver.repository;

import com.example.reddiserver.entity.BookmarkBrand;
import com.example.reddiserver.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkBrandRepository extends JpaRepository<BookmarkBrand, Long> {

	List<BookmarkBrand> findByMemberId(Long memberId);

	BookmarkBrand findByMemberIdAndBrandId(Long memberId, Long brandId);
}
