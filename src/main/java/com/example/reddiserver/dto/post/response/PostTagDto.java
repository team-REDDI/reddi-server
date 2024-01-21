package com.example.reddiserver.dto.post.response;

import com.example.reddiserver.entity.PostTag;
import com.example.reddiserver.entity.enums.PostTagType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostTagDto {
	private PostTagType postTagType;
	private String tag;

	public PostTagDto(PostTag postTag) {
		this.postTagType = postTag.getPostTagType();
		this.tag = postTag.getTag();
	}

	public static List<PostTagDto> convertToDtoList(List<PostTag> postTags) {
		return postTags.stream()
				.map(PostTagDto::new)
				.toList();
	}
}
