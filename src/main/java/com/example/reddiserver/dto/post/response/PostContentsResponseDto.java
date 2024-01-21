package com.example.reddiserver.dto.post.response;

import com.example.reddiserver.entity.Post;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostContentsResponseDto {
	private PostResponseDto post;

	@JsonRawValue
	private String content;

	public static PostContentsResponseDto from(Post post) {
		PostContentsResponseDto postContentsResponseDto = new PostContentsResponseDto();
		postContentsResponseDto.setPost(PostResponseDto.from(post));
		postContentsResponseDto.setContent(post.getContent());
		return postContentsResponseDto;
	}
}
