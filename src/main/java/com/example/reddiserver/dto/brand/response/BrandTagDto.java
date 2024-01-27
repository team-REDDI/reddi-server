package com.example.reddiserver.dto.brand.response;

import com.example.reddiserver.entity.BrandTag;
import com.example.reddiserver.entity.enums.BrandTagType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class BrandTagDto {
	private BrandTagType brandTagType;
	private String tag;

	public BrandTagDto(BrandTag brandTag) {
		this.brandTagType = brandTag.getBrandTagType();
		this.tag = brandTag.getTag();
	}

	public static List<BrandTagDto> convertToDtoList(List<BrandTag> brandTags) {
		return brandTags.stream()
				.map(BrandTagDto::new)
				.collect(Collectors.toList());
	}
}
