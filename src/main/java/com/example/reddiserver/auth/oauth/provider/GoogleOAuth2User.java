package com.example.reddiserver.auth.oauth.provider;

import com.example.reddiserver.entity.enums.ProviderType;

import java.util.Map;

public class GoogleOAuth2User extends OAuth2UserInfo{

    public GoogleOAuth2User(Map<String, Object> attributes) {
        super(attributes);
    }

    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    public String getName() {
        return (String) attributes.get("name");
    }

    public ProviderType getProviderType() {
        return ProviderType.GOOGLE;
    }

    public String getEmail() {
        return (String) attributes.get("email");
    }

    public String getProfileImage() {
        return (String) attributes.get("picture");
    }
}
