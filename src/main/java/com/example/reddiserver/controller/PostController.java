package com.example.reddiserver.controller;

import com.example.reddiserver.common.ApiResponse;
import com.example.reddiserver.dto.brand.response.BrandResponseDto;
import com.example.reddiserver.dto.post.response.HomeCuratingPostDto;
import com.example.reddiserver.dto.post.response.PostContentsResponseDto;
import com.example.reddiserver.dto.post.response.PostResponseDto;
import com.example.reddiserver.entity.Post;
import com.example.reddiserver.service.NotionService;
import com.example.reddiserver.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
@Slf4j
public class PostController {
	private final PostService postService;
	private final NotionService notionService;

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

	@Operation(summary = "홈 화면 큐레이팅 포스트(마케팅) 조회")
	@GetMapping("/home")
	public ApiResponse<List<HomeCuratingPostDto>> getHomePostList() {
		List<String> homeCuratingPageIds = notionService.getHomeCuratingPageIds();
		List<Map<String, Object>> homeCuratingPageContents = notionService.getHomeCuratingPageContents(homeCuratingPageIds);

		List<HomeCuratingPostDto> homeCuratingPostList = new ArrayList<>();
		for (Map<String, Object> content : homeCuratingPageContents) {
			String title = (String) content.get("title");

			List<Post> relatedPosts = (List<Post>) content.get("relatedPosts");
			List<PostResponseDto> postResponseDtoList = new ArrayList<>();


			for (Post relatedPost : relatedPosts) {
				try {
					PostResponseDto postResponseDto = PostResponseDto.from(relatedPost);
					postResponseDtoList.add(postResponseDto);
				}
				catch (Exception e) {
					log.error("relatedPost dto 변환 중 에러 : {}", relatedPost, e);
					throw e;
				}

			}

			HomeCuratingPostDto homeCuratingPostDto = HomeCuratingPostDto.from(title, postResponseDtoList);
			homeCuratingPostList.add(homeCuratingPostDto);
		}


		return ApiResponse.successResponse(homeCuratingPostList);
	}

	@Operation(summary = "마케팅(포스트) 페이지 조회 수 증가")
	@Parameter(name = "post_id", description = "마케팅(포스트) id", required = true)
	@GetMapping("/viewCount/{post_id}")
	public ApiResponse<HashMap<String, Long>> increaseViewCount(Long post_id) {
		Long afterViewCount = postService.increaseViewCount(post_id);

		HashMap<String, Long> response = new HashMap<>();
		response.put("after_view_count", afterViewCount);

		return ApiResponse.successResponse(response);
	}
}
