package com.example.reddiserver.dto.search.response;

import com.example.reddiserver.entity.Keyword;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class HotKeywordInfo {
    private String keyword;
    private Long count;

    public static HotKeywordInfo from(Keyword keyword) {
        return HotKeywordInfo.builder()
                .keyword(keyword.getKeyword())
                .count(keyword.getCount())
                .build();
    }
}
