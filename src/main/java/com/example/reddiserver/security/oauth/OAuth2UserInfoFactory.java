package com.example.reddiserver.security.oauth;

import com.example.reddiserver.security.oauth.provider.GoogleOAuth2User;
import com.example.reddiserver.security.oauth.provider.OAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        switch (registrationId) {
            case "GOOGLE": return new GoogleOAuth2User(attributes);

            default: throw new IllegalArgumentException("지원하지 않는 OAuth 플랫폼 입니다. \n지원하는 플랫폼 : Google");
        }
    }
}
