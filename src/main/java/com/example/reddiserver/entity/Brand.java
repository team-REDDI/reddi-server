package com.example.reddiserver.entity;

import com.example.reddiserver.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "brands")
public class Brand extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "brand")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "brand")
    private List<BrandCategory> brandCategories = new ArrayList<>();

    @Column(nullable = false)
    private String name;

    @Column
    private String image_url;

    @Column(nullable = false)
    private String content;

    @Builder
    public Brand(String name, String image_url, String content) {
        this.name = name;
        this.image_url = image_url;
        this.content = content;
    }
}
