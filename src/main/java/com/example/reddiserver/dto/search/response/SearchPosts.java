package com.example.reddiserver.dto.search.response;

import com.example.reddiserver.dto.post.response.PostTagDto;
import com.example.reddiserver.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static com.example.reddiserver.dto.post.response.PostTagDto.convertToDtoList;

@Getter
@Builder
public class SearchPosts {
    private Long id;
    private String title;
    private String subtitle;
    private List<PostTagDto> postTags;
    private String coverUrl;
    private String notionPageCreatedTime;
    private String notionPageLastEditedTime;

    public static SearchPosts convertPostToSearchPosts(Post post) {
        return SearchPosts.builder()
                .id(post.getId())
                .title(post.getTitle())
                .subtitle(post.getSubtitle())
                .postTags(convertToDtoList(post.getPostTags()))
                .coverUrl(post.getCover_url())
                .notionPageCreatedTime(post.getNotion_page_created_time())
                .notionPageLastEditedTime(post.getNotion_page_last_edited_time())
                .build();
    }
}
