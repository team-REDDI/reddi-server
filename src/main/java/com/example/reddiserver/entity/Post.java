package com.example.reddiserver.entity;

import com.example.reddiserver.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "posts")
@DynamicInsert
@DynamicUpdate
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<BookmarkPost> bookmarkPosts = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostTag> postTags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY) // 연관관계 주인
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
    @ColumnDefault("0")
    private Long view_count;

    @Column
    private String notion_page_id;

    @Column
    private String notion_page_url;

    @Column
    private String notion_page_created_time;

    @Column
    private String notion_page_last_edited_time;

    public void increaseViewCount() {
        this.view_count++;
    }
}
