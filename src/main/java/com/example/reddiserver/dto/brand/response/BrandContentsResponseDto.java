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

	public BrandContentsResponseDto(Brand brand) {
		this.id = brand.getId();
		this.content = brand.getContent();
	}
}
