package com.example.reddiserver.repository;

import com.example.reddiserver.entity.Brand;
import com.example.reddiserver.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

	@Query("SELECT p FROM Post p")
	Page<Post> findAllPosts(Pageable pageable);

	@Query("SELECT p FROM Post p WHERE p.notion_page_id = :notion_page_id")
	Post findPostByNotion_page_id(@Param("notion_page_id") String notion_page_id);
}
