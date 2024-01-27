package com.example.reddiserver.security.oauth;

import com.example.reddiserver.entity.Member;
import com.example.reddiserver.entity.enums.Authority;
import com.example.reddiserver.repository.MemberRepository;
import com.example.reddiserver.security.oauth.provider.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PrincipalOAuth2DetailsService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        return processOAuth2User(userRequest, super.loadUser(userRequest));
    }

    protected OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        System.out.println("oAuth2User.getAttributes() = " + oAuth2User.getAttributes());

        Member member = memberRepository.findByProviderId(oAuth2UserInfo.getProviderId()).orElse(null);
        if (member == null) {
            member = signup(oAuth2UserInfo);
        }

        return new PrincipalOAuth2Details(member, oAuth2UserInfo.getAttributes());
    }

    private Member signup(OAuth2UserInfo oAuth2UserInfo) {
        Member member = Member.builder()
                .providerId(oAuth2UserInfo.getProviderId())
                .name(oAuth2UserInfo.getName())
                .email(oAuth2UserInfo.getEmail())
                .profileImageUrl(oAuth2UserInfo.getProfileImage())
                .providerType(oAuth2UserInfo.getProviderType())
                .authority(Authority.ROLE_USER)
                .build();

        return memberRepository.save(member);
    }
}
