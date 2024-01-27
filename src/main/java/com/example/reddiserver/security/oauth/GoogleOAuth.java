package com.example.reddiserver.security.oauth;

import com.example.reddiserver.dto.auth.response.GoogleTokenResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Getter
public class GoogleOAuth {

	private final String GOOGLE_LOGIN_URL = "https://accounts.google.com/o/oauth2/v2/auth";
	private final String GOOGLE_TOKEN_REQUEST_URL = "https://oauth2.googleapis.com/token";
	private final String GOOGLE_USERINFO_REQUEST_Url = "https://www.googleapis.com/oauth2/v3/userinfo";

	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String CLIENT_ID;

	@Value("${spring.security.oauth2.client.registration.google.client-secret}")
	private String CLIENT_SECRET;

	private final WebClient webClient;

	public GoogleTokenResponseDto requestToken(String code, String redirectUri) {
		Map<String, Object> params = Map.of(
				"code", code,
				"client_id", CLIENT_ID,
				"client_secret", CLIENT_SECRET,
				"redirect_uri", redirectUri,
				"grant_type", "authorization_code"
		);

		GoogleTokenResponseDto response = webClient.post()
				.uri(GOOGLE_TOKEN_REQUEST_URL)
				.bodyValue(params)
				.retrieve()
				.bodyToMono(GoogleTokenResponseDto.class)
				.block();

		System.out.println("response = " + response);

		return response;
	}
}
