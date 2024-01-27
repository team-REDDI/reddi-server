package com.example.reddiserver.dto.search.response;

import com.example.reddiserver.dto.brand.response.BrandTagDto;
import com.example.reddiserver.entity.Brand;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static com.example.reddiserver.dto.brand.response.BrandTagDto.convertToDtoList;


@Getter
@Builder
public class SearchBrands {
    private String name;
    private List<BrandTagDto> brandTags;
    private String coverUrl;

    public static SearchBrands convertBrandToSearchBrands(Brand brand) {
        return SearchBrands.builder()
                .name(brand.getName())
                .brandTags(convertToDtoList(brand.getBrandTags()))
                .coverUrl(brand.getCover_url())
                .build();
    }
}
