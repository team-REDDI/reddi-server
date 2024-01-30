package com.example.reddiserver.controller;

import com.example.reddiserver.common.ApiResponse;
import com.example.reddiserver.dto.search.response.HotKeywordResponseDto;
import com.example.reddiserver.dto.search.response.HotPostResponseDto;
import com.example.reddiserver.dto.search.response.SearchResponseDto;
import com.example.reddiserver.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
@Slf4j
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "검색")
    @GetMapping("/")
    public ApiResponse<SearchResponseDto> getSearchList(@RequestParam String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        SearchResponseDto searchResponseDto = searchService.getSearchList(keyword, PageRequest.of(page, size));
        return ApiResponse.successResponse(searchResponseDto, "검색");
    }

    @Operation(summary = "인기 검색어")
    @GetMapping("/keyword")
    public ApiResponse<HotKeywordResponseDto> getKeywordList() {
        HotKeywordResponseDto keywordResponseDto = searchService.getHotKeywordList();
        return ApiResponse.successResponse(keywordResponseDto, "인기 검색어 조회");
    }

    @Operation(summary = "인기 마케팅 레퍼런스")
    @GetMapping("/hot-post")
    public ApiResponse<HotPostResponseDto> getHotPostList() {
        HotPostResponseDto hotPostResponseDto = searchService.getHotPostList();
        return ApiResponse.successResponse(hotPostResponseDto, "인기 마케팅 레퍼런스 조회");
    }
}
