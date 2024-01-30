package com.example.reddiserver.service;

import com.example.reddiserver.dto.search.response.*;
import com.example.reddiserver.entity.Keyword;
import com.example.reddiserver.repository.BrandRepository;
import com.example.reddiserver.repository.KeywordRepository;
import com.example.reddiserver.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {
    private final BrandRepository brandRepository;
    private final PostRepository postRepository;
    private final KeywordRepository keywordRepository;

    @Transactional
    public SearchResponseDto getSearchList(String keyword, Pageable pageable) {
        List<SearchPosts> posts = postRepository.findByTitleContainingOrContentContaining("%"+keyword+"%", pageable).getContent().stream()
                .map(SearchPosts::convertPostToSearchPosts)
                .collect(Collectors.toList());
        List<SearchBrands> brands = brandRepository.findByNameContainingOrContentContaining("%"+keyword+"%", pageable).getContent().stream()
                .map(SearchBrands::convertBrandToSearchBrands)
                .collect(Collectors.toList());

        Optional<Keyword> keywordOptional = keywordRepository.findByKeyword(keyword);
        if (keywordOptional.isEmpty()) {
            keywordRepository.save(Keyword.builder()
                            .keyword(keyword)
                            .count(1L)
                            .build());
        } else {
            keywordRepository.save(keywordOptional.get().updateCount());
        }

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

    public HotKeywordResponseDto getHotKeywordList() {
        List<HotKeywordInfo> topNKeywords = keywordRepository.findTopNByOrderByCountDesc(PageRequest.of(0,20)).stream()
                .map(HotKeywordInfo::from)
                .collect(Collectors.toList());

        return HotKeywordResponseDto.builder()
                .keywords(topNKeywords)
                .build();
    }
}
