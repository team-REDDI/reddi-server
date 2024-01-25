package com.example.reddiserver.service;

import com.example.reddiserver.dto.post.response.PostContentsResponseDto;
import com.example.reddiserver.dto.post.response.PostResponseDto;
import com.example.reddiserver.entity.Brand;
import com.example.reddiserver.entity.Post;
import com.example.reddiserver.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

	private final PostRepository postRepository;

	public Page<PostResponseDto> getPostList(Pageable pageable) {
		Page<Post> postPage = postRepository.findAllPosts(pageable);
		return postPage.map(PostResponseDto::from);
	}

	public PostContentsResponseDto getPostById(Long id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 포스트가 없습니다. id=" + id));
		return PostContentsResponseDto.from(post);
	}

	@Transactional
	public Long increaseViewCount(Long id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 없습니다. id=" + id));
		post.increaseViewCount();

		return post.getView_count();
	}
}
