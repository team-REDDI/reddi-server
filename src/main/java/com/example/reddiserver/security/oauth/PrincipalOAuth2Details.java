package com.example.reddiserver.security.oauth;

import com.example.reddiserver.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Getter
public class PrincipalOAuth2Details implements OAuth2User {

    private Member member;
    private Map<String, Object> attributes;

    public PrincipalOAuth2Details(Member member) {
        this.member = member;
    }

    public PrincipalOAuth2Details(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return member.getAuthority().toString();
            }
        });

        return collect;
    }

    @Override
    public String getName() {
        return member.getProviderId();
    }
}
