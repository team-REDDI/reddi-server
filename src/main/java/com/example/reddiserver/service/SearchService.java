package com.example.reddiserver.service;

import com.example.reddiserver.dto.search.response.*;
import com.example.reddiserver.repository.BrandRepository;
import com.example.reddiserver.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {
    private final BrandRepository brandRepository;
    private final PostRepository postRepository;

    public SearchResponseDto getSearchList(String keyword, Pageable pageable) {
        List<SearchPosts> posts = postRepository.findByTitleContainingOrContentContaining("%"+keyword+"%", pageable).getContent().stream()
                .map(SearchPosts::convertPostToSearchPosts)
                .collect(Collectors.toList());
        List<SearchBrands> brands = brandRepository.findByNameContainingOrContentContaining("%"+keyword+"%", pageable).getContent().stream()
                .map(SearchBrands::convertBrandToSearchBrands)
                .collect(Collectors.toList());

        return SearchResponseDto.builder()
                .posts(posts)
                .brands(brands)
                .build();
    }

    public HotPostResponseDto getHotPostList() {
        List<HotPostInfo> topNPosts = postRepository.findTopNByOrderByViewCountDesc(PageRequest.of(0, 2)).stream()
                .map(HotPostInfo::from)
                .collect(Collectors.toList());

        return HotPostResponseDto.builder()
                .posts(topNPosts)
                .build();
    }
}
