package com.example.reddiserver.entity;

import com.example.reddiserver.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bookmark_posts")
public class BookmarkPost extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bookmark_id")
    private Bookmark bookmark;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public BookmarkPost(Bookmark bookmark, Post post) {
        this.bookmark = bookmark;
        this.post = post;
    }
}