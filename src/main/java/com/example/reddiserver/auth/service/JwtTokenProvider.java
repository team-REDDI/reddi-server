package com.example.reddiserver.auth.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider implements InitializingBean {

	@Value("${spring.jwt.secret}")
	private String JWT_SECRET;

	private Key key;

	private final Long accessTokenExpiredTime = 1000 * 60L * 60L * 24L; // 유효시간 24시간 (임시 변경)
	private final Long refreshTokenExpiredTime = 1000 * 60L * 60L * 24L * 14L; // 유효시간 14일

	private final Long shortAccessTokenExpiredTime = 1000 * 60L * 5;  // 유효시간 5분

	@Override
	public void afterPropertiesSet() throws Exception {
		Base64.Decoder decoders = Base64.getDecoder();
		byte[] keyBytes = decoders.decode(JWT_SECRET);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public String createAccessToken(Map<String, String> payload) {
		return createJwtToken(payload, accessTokenExpiredTime);
	}

	public String createRefreshToken(Map<String, String> payload) {
		return createJwtToken(payload, refreshTokenExpiredTime);
	}

	public String createJwtToken(Map<String, String> payload, long expireLength) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + expireLength);

		try {
			return Jwts.builder()
					.setClaims(payload)
					.setIssuedAt(now)
					.setExpiration(validity)
					.signWith(key, SignatureAlgorithm.HS512)
					.compact();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public Map<String, String> getPayload(String token) {
		try {
			Claims claims = Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token)
					.getBody();

			// 원하는 클레임 값 추출하여 Map으로 저장
			Map<String, String> payload = new HashMap<>();
			payload.put("userId", claims.get("userId", String.class));

			return payload;

		} catch (JwtException e) {
			System.err.println("Error Type: " + e.getClass().getName());
			System.err.println("Error Message: " + e.getMessage());
			throw new JwtException("유효하지 않은 토큰 입니다");
		}
	}

	public void validateToken(String token) throws JwtException {
		try {
			Jws<Claims> claimsJws = Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token);

			Date expiration = claimsJws.getBody().getExpiration();
			Date now = new Date();

			if (expiration.before(now)) {
				throw new ExpiredJwtException(null, null, "Token expired");
			}
		} catch (ExpiredJwtException e) {
			// 토큰이 만료된 경우 처리
			log.error("===Token expired: {}===", e.getMessage());
			throw e;
		} catch (MalformedJwtException e) {
			// 토큰 형식이 잘못된 경우 처리
			log.error("====Malformed token: {}===", e.getMessage());
			throw e;
		} catch (JwtException | IllegalArgumentException e) {
			// 기타 예외 처리
			log.error("===Invalid token: {}===", e.getMessage());
			throw e;
		}
	}



}
