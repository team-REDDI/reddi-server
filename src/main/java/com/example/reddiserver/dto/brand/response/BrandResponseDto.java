package com.example.reddiserver.dto.brand.response;

import com.example.reddiserver.dto.post.response.PostResponseDto;
import com.example.reddiserver.entity.Brand;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BrandResponseDto {
	private Long id;
	private String name;
	private List<BrandTagDto> brandTags;
	private String cover_url;
	private String notion_page_url;
	private String notion_page_created_time;
	private String notion_page_last_edited_time;

	// 정적 메서드
	public static BrandResponseDto from(Brand brand) {
		BrandResponseDto brandResponseDto = new BrandResponseDto();
		brandResponseDto.setId(brand.getId());
		brandResponseDto.setName(brand.getName());
		brandResponseDto.setBrandTags(BrandTagDto.convertToDtoList(brand.getBrandTags()));
		brandResponseDto.setCover_url(brand.getCover_url());
		brandResponseDto.setNotion_page_url(brand.getNotion_page_url());
		brandResponseDto.setNotion_page_created_time(brand.getNotion_page_created_time());
		brandResponseDto.setNotion_page_last_edited_time(brand.getNotion_page_last_edited_time());
		return brandResponseDto;
	}
}
