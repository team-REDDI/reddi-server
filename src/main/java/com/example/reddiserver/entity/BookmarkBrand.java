package com.example.reddiserver.entity;

import com.example.reddiserver.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bookmark_brands")
@DynamicInsert
@DynamicUpdate
public class BookmarkBrand extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 연관관계 주인
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY) // 연관관계 주인
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Builder
    public BookmarkBrand(Member member, Brand brand) {
        this.member = member;
        this.brand = brand;
    }
}
