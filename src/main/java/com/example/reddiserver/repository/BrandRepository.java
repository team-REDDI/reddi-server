package com.example.reddiserver.repository;

import com.example.reddiserver.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long> {

	@Query("SELECT b FROM Brand b WHERE b.notion_page_id = :notion_page_id")
	Brand findBrandByNotion_page_id(@Param("notion_page_id") String notion_page_id);
}
