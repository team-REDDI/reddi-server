package com.example.reddiserver.dto.search.response;

import com.example.reddiserver.entity.Post;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class HotPostInfo {
    private Long id;
    private String title;
    private String subtitle;
    private String coverUrl;
    private String notionPageCreatedTime;
    private String notionPageLastEditedTime;

    public static HotPostInfo from(Post post) {
        return HotPostInfo.builder()
                .id(post.getId())
                .title(post.getTitle())
                .subtitle(post.getSubtitle())
                .coverUrl(post.getCover_url())
                .notionPageCreatedTime(post.getNotion_page_created_time())
                .notionPageLastEditedTime(post.getNotion_page_last_edited_time())
                .build();
    }
}
