package com.example.reddiserver.service;

import com.example.reddiserver.dto.brand.response.BrandResponseDto;
import com.example.reddiserver.dto.post.response.PostContentsResponseDto;
import com.example.reddiserver.dto.post.response.PostResponseDto;
import com.example.reddiserver.entity.*;
import com.example.reddiserver.repository.BookmarkPostRepository;
import com.example.reddiserver.repository.MemberRepository;
import com.example.reddiserver.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

	private final PostRepository postRepository;
	private final BookmarkPostRepository bookmarkPostRepository;
	private final MemberRepository memberRepository;

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

	public List<PostResponseDto> getTopNPosts(int topN) {
		List<Post> topNPosts = postRepository.findTopNByOrderByViewCountDescAndNameAsc(PageRequest.of(0, topN));

		List<PostResponseDto> topNPostList = new ArrayList<>();
		for (Post post : topNPosts) {
			topNPostList.add(PostResponseDto.from(post));
		}
		return topNPostList;
	}

	public List<PostResponseDto> getBookmarkPostList(Long memberId) {
		List<BookmarkPost> bookmarkBrands = bookmarkPostRepository.findByMemberId(memberId);

		List<PostResponseDto> postResponseDtoList = bookmarkBrands.stream()
				.map(bookmarkPost -> PostResponseDto.from(bookmarkPost.getPost()))
				.collect(Collectors.toList());

		return postResponseDtoList;

	}

	@Transactional
	public boolean toggleBookmarkPost(Long memberId, Long postId) {
		BookmarkPost bookmarkPost = bookmarkPostRepository.findByMemberIdAndPostId(memberId, postId);
		if (bookmarkPost == null) {

			// memberId 로 member 조회
			Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다. id=" + memberId));


			// postId 로 brand 조회
			Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 없습니다. id=" + postId));

			// bookmarkPost 생성
			BookmarkPost newBookmarkPost = BookmarkPost.builder()
					.member(member)
					.post(post)
					.build();

			// bookmarkPost 저장
			bookmarkPostRepository.save(newBookmarkPost);

			// bookmarkPost 저장 성공 시 true 리턴
			return true;

		} else {
			bookmarkPostRepository.delete(bookmarkPost);

			// bookmarkPost 삭제 성공 시 false 리턴
			return false;
		}
	}
}
