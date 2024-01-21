package com.example.reddiserver.entity;

import com.example.reddiserver.entity.base.BaseTimeEntity;
import com.example.reddiserver.entity.enums.BrandTagType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "brand_tags")
public class BrandTag extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 연관관계 주인
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Enumerated(EnumType.STRING)
    private BrandTagType brandTagType;
    // 브랜드_분위기, 브랜드_색감, MKT_종류, MKT_타겟층, 산업군

    @Column
    private String tag;

    @Builder
    public BrandTag(Brand brand, BrandTagType brandTagType, String tag) {
        this.brand = brand;
        this.brandTagType = brandTagType;
        this.tag = tag;
    }
}
