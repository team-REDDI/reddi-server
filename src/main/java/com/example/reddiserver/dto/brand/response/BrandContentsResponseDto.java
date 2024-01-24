package com.example.reddiserver.dto.brand.response;

import com.example.reddiserver.entity.Brand;
import com.example.reddiserver.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonRawValue;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class BrandContentsResponseDto {
	private BrandResponseDto brand;

	private List<Long> postIds;

	@JsonRawValue
	private String content;

	public static BrandContentsResponseDto from(Brand brand) {
		BrandContentsResponseDto brandContentsResponseDto = new BrandContentsResponseDto();
		brandContentsResponseDto.setBrand(BrandResponseDto.from(brand));
		brandContentsResponseDto.setContent(brand.getContent());

		// Post의 id 목록 추가
		brandContentsResponseDto.setPostIds(brand.getPosts().stream()
				.map(Post::getId)
				.collect(Collectors.toList()));
		return brandContentsResponseDto;
	}
}
