package com.example.reddiserver.repository;

import com.example.reddiserver.entity.Brand;
import com.example.reddiserver.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

	@Query("SELECT p FROM Post p")
	Page<Post> findAllPosts(Pageable pageable);

	@Query("SELECT p FROM Post p WHERE p.notion_page_id = :notion_page_id")
	Post findPostByNotion_page_id(@Param("notion_page_id") String notion_page_id);

	@Query("SELECT p FROM Post p ORDER BY p.view_count DESC, p.title ASC")
	List<Post> findTopNByOrderByViewCountDescAndNameAsc(Pageable pageable);
}
