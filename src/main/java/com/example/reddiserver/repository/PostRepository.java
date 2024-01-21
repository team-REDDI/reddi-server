package com.example.reddiserver.repository;

import com.example.reddiserver.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
