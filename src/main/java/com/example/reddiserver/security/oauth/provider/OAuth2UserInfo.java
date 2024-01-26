package com.example.reddiserver.security.oauth.provider;

import com.example.reddiserver.entity.enums.ProviderType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public abstract String getProviderId();
    public abstract String getName();
    public abstract ProviderType getProviderType();
    public abstract String getEmail();
    public abstract String getProfileImage();
}
