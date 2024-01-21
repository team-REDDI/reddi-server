package com.example.reddiserver.entity;

import com.example.reddiserver.entity.base.BaseTimeEntity;
import com.example.reddiserver.entity.enums.PostTagType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post_tags")
public class PostTag extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Enumerated(EnumType.STRING)
    private PostTagType postTagType;
    // 기업, 마케팅_종류, 산업, 타겟층

    @Column
    private String tag;

    @Builder
    public PostTag(Post post, PostTagType postTagType, String tag) {
        this.post = post;
        this.postTagType = postTagType;
        this.tag = tag;
    }
}
