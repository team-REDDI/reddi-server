package com.example.reddiserver.controller;

import com.example.reddiserver.common.ApiResponse;
import com.example.reddiserver.dto.brand.response.BrandResponseDto;
import com.example.reddiserver.dto.post.response.PostContentsResponseDto;
import com.example.reddiserver.dto.post.response.PostResponseDto;
import com.example.reddiserver.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {
	private final PostService postService;

	@Operation(summary = "포스트(마케팅) 리스트 조회")
	@GetMapping("/")
	public ApiResponse<Page<PostResponseDto>> getPostList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		Page<PostResponseDto> postList = postService.getPostList(PageRequest.of(page, size));
		return ApiResponse.successResponse(postList);
	}

	@Operation(summary = "포스트(마케팅) 단건(상세) 조회")
	@GetMapping("/{id}")
	public ApiResponse<PostContentsResponseDto> getPostById(Long id) {
		PostContentsResponseDto post = postService.getPostById(id);
		return ApiResponse.successResponse(post);
	}
}
