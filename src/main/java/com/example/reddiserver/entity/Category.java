package com.example.reddiserver.entity;

import com.example.reddiserver.entity.base.BaseTimeEntity;
import com.example.reddiserver.entity.enums.CategoryType;
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
@Table(name = "categories")
public class Category extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "category")
    private List<BrandCategory> brandCategories = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private List<PostCategory> postCategories = new ArrayList<>();

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;

    @Builder
    public Category(String name, CategoryType categoryType) {
        this.name = name;
        this.categoryType = categoryType;
    }
}
