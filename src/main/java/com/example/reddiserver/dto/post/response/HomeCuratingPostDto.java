package com.example.reddiserver.dto.post.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class HomeCuratingPostDto {

	private String curation_title;
	private List<PostResponseDto> posts;

	// 정적 메서드
	public static HomeCuratingPostDto from(String title, List<PostResponseDto> posts) {
		HomeCuratingPostDto homeCuratingPostDto = new HomeCuratingPostDto();
		homeCuratingPostDto.setCuration_title(title);
		homeCuratingPostDto.setPosts(posts);
		return homeCuratingPostDto;
	}
}
