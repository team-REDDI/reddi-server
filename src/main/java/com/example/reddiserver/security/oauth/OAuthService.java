package com.example.reddiserver.security.oauth;

import com.example.reddiserver.dto.auth.response.GoogleTokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuthService {

	private final GoogleOAuth googleOAuth;

	@Transactional
	public void login(String code, String redirectUri) {
		GoogleTokenResponseDto googleToken = googleOAuth.requestToken(code, redirectUri);
		System.out.println("googleToken = " + googleToken);


	}
}
