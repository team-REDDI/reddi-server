package com.example.reddiserver.repository;

import com.example.reddiserver.entity.Brand;
import com.example.reddiserver.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

//	@Query("SELECT b FROM Brand b")
	@Query("SELECT b FROM Brand b LEFT JOIN FETCH b.brandTags")
	Page<Brand> findAllBrands(Pageable pageable);

	@EntityGraph(attributePaths = {"posts"})
	Optional<Brand> findById(Long id);

	@Query("SELECT b FROM Brand b WHERE b.notion_page_id = :notion_page_id")
	Brand findBrandByNotion_page_id(@Param("notion_page_id") String notion_page_id);

	@Query("SELECT b FROM Brand b ORDER BY b.view_count DESC, b.name ASC")
	List<Brand> findTopNByOrderByViewCountDescAndNameAsc(Pageable pageable);

	@Query("SELECT b FROM Brand b WHERE b.name like :keyword OR b.content like :keyword")
	Page<Brand> findByNameContainingOrContentContaining(String keyword, Pageable pageable);

}
