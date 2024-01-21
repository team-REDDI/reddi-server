package com.example.reddiserver.entity;

import com.example.reddiserver.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "posts")
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "post")
    private List<BookmarkPost> bookmarkPosts = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostTag> postTags = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(nullable = false)
    private String title;

    @Column
    private String subtitle;

    @Column
    private String description;

    @Column(columnDefinition = "LONGTEXT")
    private String cover_url;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column
    private String notion_page_id;

    @Column
    private String notion_page_url;

    @Column
    private String notion_page_created_time;

    @Column
    private String notion_page_last_edited_time;
}
