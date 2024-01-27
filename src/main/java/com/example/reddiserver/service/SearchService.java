package com.example.reddiserver.service;

import com.example.reddiserver.dto.search.response.SearchBrands;
import com.example.reddiserver.dto.search.response.SearchPosts;
import com.example.reddiserver.dto.search.response.SearchResponseDto;
import com.example.reddiserver.repository.BrandRepository;
import com.example.reddiserver.repository.PostRepository;
import lombok.RequiredArgsConstructor;
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
}
