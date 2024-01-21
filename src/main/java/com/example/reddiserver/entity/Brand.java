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
@Table(name = "brands")
public class Brand extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "brand")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "brand")
    private List<BrandTag> brandTags = new ArrayList<>();

    @Column
    private String name;

    @Column
    private String image_url;

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


    @Builder
    public Brand(String name, String image_url, String content, String notion_page_id, String notion_page_url, String notion_page_created_time, String notion_page_last_edited_time) {
        this.name = name;
        this.image_url = image_url;
        this.content = content;
        this.notion_page_id = notion_page_id;
        this.notion_page_url = notion_page_url;
        this.notion_page_created_time = notion_page_created_time;
        this.notion_page_last_edited_time = notion_page_last_edited_time;
    }
}
