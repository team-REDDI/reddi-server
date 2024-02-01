package com.example.reddiserver.auth.service;

import com.example.reddiserver.auth.filter.CustomAuthenticationDetails;
import com.example.reddiserver.auth.oauth.GoogleOAuth;
import com.example.reddiserver.dto.auth.response.GoogleTokenResponseDto;
import com.example.reddiserver.dto.auth.response.GoogleUserResponseDto;
import com.example.reddiserver.dto.auth.response.LoginResponseDto;
import com.example.reddiserver.entity.Member;
import com.example.reddiserver.entity.RefreshToken;
import com.example.reddiserver.repository.MemberRepository;
import com.example.reddiserver.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuthService {

	private final GoogleOAuth googleOAuth;
	private final MemberRepository memberRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtTokenProvider jwtTokenProvider;

	@Transactional
	public LoginResponseDto login(String code, String redirectUri) {
		GoogleTokenResponseDto googleToken = googleOAuth.requestToken(code, redirectUri);
		System.out.println("googleToken = " + googleToken);

		// 구글토큰으로 구글 유저 정보 조회

		GoogleUserResponseDto googleUserResponse = googleOAuth.requestUserInfo(googleToken);

		String providerId = googleUserResponse.getSub();
		String name = googleUserResponse.getName();
		String email = googleUserResponse.getEmail();
		String profileImageUrl = googleUserResponse.getPicture();

		// providerId로 DB 조회
		// DB에 없으면 회원가입
		Member member = memberRepository.findByProviderId(providerId)
				.orElse(null);

		if (member == null) {
			member = Member.builder()
					.providerId(providerId)
					.name(name)
					.email(email)
					.profileImageUrl(profileImageUrl)
					.build();
			memberRepository.save(member);
		}

		// accessToken, refreshToken 발급

		Map<String, String> payload = new HashMap<>();
		payload.put("userId", String.valueOf(member.getId()));

		String accessToken = jwtTokenProvider.createAccessToken(payload);
		String refreshToken = jwtTokenProvider.createRefreshToken(payload);

		// redis에 저장
		RefreshToken refreshTokenEntity = new RefreshToken();
		refreshTokenEntity.setUserId(member.getId());
		refreshTokenEntity.setRefreshToken(refreshToken);

		refreshTokenRepository.save(refreshTokenEntity);

		return LoginResponseDto.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

	public Long getUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null) {
			System.out.println(authentication);
			System.out.println(authentication.getDetails());
			Object details = authentication.getDetails();
			if (details instanceof CustomAuthenticationDetails) {
				CustomAuthenticationDetails customDetails = (CustomAuthenticationDetails) details;
				return customDetails.getUserId();
			}
		}

		return null; // 인증 정보가 없거나 CustomAuthenticationDetails를 사용하지 않는 경우
	}
}
