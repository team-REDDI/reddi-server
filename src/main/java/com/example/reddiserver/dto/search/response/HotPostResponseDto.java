package com.example.reddiserver.dto.search.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class HotPostResponseDto {
    private List<HotPostInfo> posts;
}
