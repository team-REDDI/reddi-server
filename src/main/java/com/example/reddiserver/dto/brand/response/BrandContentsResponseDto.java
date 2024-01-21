package com.example.reddiserver.dto.brand.response;

import com.example.reddiserver.entity.Brand;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonRawValue;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BrandContentsResponseDto {
	private Long id;

	@JsonRawValue
	private String content;

	public static BrandContentsResponseDto from(Brand brand) {
		BrandContentsResponseDto brandContentsResponseDto = new BrandContentsResponseDto();
		brandContentsResponseDto.setId(brand.getId());
		brandContentsResponseDto.setContent(brand.getContent());
		return brandContentsResponseDto;
	}
}
