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
@Table(name = "brands")
@DynamicInsert
@DynamicUpdate
public class Brand extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL)
    private List<BrandTag> brandTags = new ArrayList<>();

    @Column
    private String name;

    @Column(columnDefinition = "LONGTEXT")
    private String cover_url;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(nullable = false)
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

}
