package com.example.reddiserver.dto.search.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchResponseDto {
    private List<SearchBrands> brands;
    private List<SearchPosts> posts;
}
