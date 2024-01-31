package com.example.reddiserver.controller;

import com.example.reddiserver.auth.service.OAuthService;
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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

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
	private final OAuthService oAuthService;

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

	@Operation(summary = "마케팅(포스트) TOP N 조회")
	@Parameter(name = "n", description = "상위 n개")
	@GetMapping("/top")
	public ApiResponse<List<PostResponseDto>> getTopNPosts(@RequestParam(defaultValue = "10") int n) {
		List<PostResponseDto> topNPosts = postService.getTopNPosts(n);
		return ApiResponse.successResponse(topNPosts);
	}

	@SecurityRequirement(name = "Authorization") // 인증 필요한 엔드포인트에 설정
	@Operation(summary = "마케팅(포스트) 북마크 토글 (북마크 추가/삭제)")
	@PutMapping("/bookmark/toggle")
	public ApiResponse<HashMap<String, Boolean>> toggleBookmarkPost(@RequestParam Long postId) {

		Long memberId = oAuthService.getUserId();

		boolean isBookmarked = postService.toggleBookmarkPost(memberId, postId);

		HashMap<String, Boolean> response = new HashMap<>();
		response.put("is_bookmarked", isBookmarked);

		return ApiResponse.successResponse(response);
	}

	@SecurityRequirement(name = "Authorization") // 인증 필요한 엔드포인트에 설정
	@Operation(summary = "유저의 마케팅(포스트) 북마크 조회")
	@GetMapping("/bookmark")
	public ApiResponse<List<PostResponseDto>> getBookmarkBrands() {

		Long memberId = oAuthService.getUserId();

		List<PostResponseDto> bookmarkPosts = postService.getBookmarkPostList(memberId);

		return ApiResponse.successResponse(bookmarkPosts);
	}
}
