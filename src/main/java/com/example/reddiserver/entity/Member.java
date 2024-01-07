package com.example.reddiserver.entity;

import com.example.reddiserver.entity.base.BaseTimeEntity;
import com.example.reddiserver.entity.enums.Authority;
import com.example.reddiserver.entity.enums.ProviderType;
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
@Table(name = "members")
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "member")
    private List<Bookmark> bookmarks = new ArrayList<>();

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column
    private String imageUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Builder
    public Member(String email, String name, String imageUrl, ProviderType providerType, Authority authority) {
        this.email = email;
        this.name = name;
        this.imageUrl = imageUrl;
        this.providerType = providerType;
        this.authority = authority;
    }
}
