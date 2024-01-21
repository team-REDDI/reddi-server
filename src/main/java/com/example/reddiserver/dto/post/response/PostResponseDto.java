package com.example.reddiserver.dto.post.response;

import com.example.reddiserver.dto.brand.response.BrandResponseDto;
import com.example.reddiserver.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostResponseDto {
	private Long id;
	private Long brand_id;
	private String title;
	private String subtitle;
	private String description;
	private List<PostTagDto> postTags;
	private String cover_url;
	private String notion_page_url;
	private String notion_page_created_time;
	private String notion_page_last_edited_time;

	// 정적 메서드
	public static PostResponseDto from(Post post) {
		PostResponseDto postResponseDto = new PostResponseDto();
		postResponseDto.setId(post.getId());
		postResponseDto.setTitle(post.getTitle());
		postResponseDto.setSubtitle(post.getSubtitle());
		postResponseDto.setDescription(post.getDescription());
		postResponseDto.setPostTags(PostTagDto.convertToDtoList(post.getPostTags()));
		postResponseDto.setCover_url(post.getCover_url());
		postResponseDto.setNotion_page_url(post.getNotion_page_url());
		postResponseDto.setNotion_page_created_time(post.getNotion_page_created_time());
		postResponseDto.setNotion_page_last_edited_time(post.getNotion_page_last_edited_time());

		// Brand 정보 추가
		if (post.getBrand() != null){
			postResponseDto.setBrand_id(post.getBrand().getId());
		}

		return postResponseDto;
	}
}
