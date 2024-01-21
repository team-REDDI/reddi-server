package com.example.reddiserver.service;

import com.example.reddiserver.dto.post.response.PostContentsResponseDto;
import com.example.reddiserver.dto.post.response.PostResponseDto;
import com.example.reddiserver.entity.Post;
import com.example.reddiserver.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

	private final PostRepository postRepository;

	public List<PostResponseDto> getPostList() {
		List<Post> posts = postRepository.findAll();
		List<PostResponseDto> postResponseDtos = new ArrayList<>();

		for (Post post : posts) {
			postResponseDtos.add(PostResponseDto.from(post));
		}

		return postResponseDtos;
	}

	public PostContentsResponseDto getPostById(Long id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 포스트가 없습니다. id=" + id));
		return PostContentsResponseDto.from(post);
	}
}
