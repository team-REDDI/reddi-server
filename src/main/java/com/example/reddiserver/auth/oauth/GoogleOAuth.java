package com.example.reddiserver.auth.oauth;

import com.example.reddiserver.dto.auth.response.GoogleTokenResponseDto;
import com.example.reddiserver.dto.auth.response.GoogleUserResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Getter
public class GoogleOAuth {

	private final String GOOGLE_LOGIN_URL = "https://accounts.google.com/o/oauth2/v2/auth";
	private final String GOOGLE_TOKEN_REQUEST_URL = "https://oauth2.googleapis.com/token";
	private final String GOOGLE_USERINFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

//	@Value("${spring.security.oauth2.client.registration.google.redirectUri}")
//	private String REDIRECT_URI;

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

		return webClient.post()
				.uri(GOOGLE_TOKEN_REQUEST_URL)
				.bodyValue(params)
				.retrieve()
				.bodyToMono(GoogleTokenResponseDto.class)
				.block();
	}


	public GoogleUserResponseDto requestUserInfo(GoogleTokenResponseDto googleToken) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + googleToken.getAccess_token());

		GoogleUserResponseDto googleUserResponse = webClient.get()
				.uri(GOOGLE_USERINFO_REQUEST_URL)
				.headers(httpHeaders -> httpHeaders.setBearerAuth(googleToken.getAccess_token()))
				.retrieve()
				.bodyToMono(GoogleUserResponseDto.class)
				.block();

		System.out.println("requestUserInfo response = " + googleUserResponse);

		return googleUserResponse;
	}
}
