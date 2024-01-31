package com.example.reddiserver.entity;

import com.example.reddiserver.entity.base.BaseTimeEntity;
import com.example.reddiserver.entity.enums.Authority;
import com.example.reddiserver.entity.enums.ProviderType;
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
@Table(name = "members")
@DynamicInsert
@DynamicUpdate
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<BookmarkBrand> bookmarkBrands = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<BookmarkPost> bookmarkPosts = new ArrayList<>();

    @Column(nullable = false, unique = true)
    private String providerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column
    private String profileImageUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Builder
    public Member(String providerId, String name, String email, String profileImageUrl, ProviderType providerType, Authority authority) {
        this.providerId = providerId;
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.providerType = providerType;
        this.authority = authority;
    }
}
