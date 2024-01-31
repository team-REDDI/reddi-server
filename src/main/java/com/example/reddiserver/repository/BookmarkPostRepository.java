package com.example.reddiserver.repository;

import com.example.reddiserver.entity.BookmarkBrand;
import com.example.reddiserver.entity.BookmarkPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkPostRepository extends JpaRepository<BookmarkPost, Long> {

	List<BookmarkPost> findByMemberId(Long memberId);

	BookmarkPost findByMemberIdAndPostId(Long memberId, Long postId);
}
