package com.example.reddiserver.dto.brand.response;

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


	public BrandResponseDto(Brand brand) {
		this.id = brand.getId();
		this.brandTags = BrandTagDto.convertToDtoList(brand.getBrandTags());
		this.name = brand.getName();
		this.cover_url = brand.getCover_url();
		this.notion_page_url = brand.getNotion_page_url();
		this.notion_page_created_time = brand.getNotion_page_created_time();
		this.notion_page_last_edited_time = brand.getNotion_page_last_edited_time();

	}
}
