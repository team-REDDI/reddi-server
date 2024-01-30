package com.example.reddiserver.auth.filter;

import com.example.reddiserver.auth.service.JwtTokenProvider;
import com.example.reddiserver.entity.enums.Authority;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.engine.Role;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String accessTokenHeader = request.getHeader("Authorization");
		String refreshTokenHeader = request.getHeader("RefreshToken");

		// 헤더에 토큰 안 담기는 경우는 인가 안하고 그대로 필터 종료
		if (accessTokenHeader == null) {
			filterChain.doFilter(request, response);
			return;
		}

		// 헤더에 토큰 담기는 경우
		if (accessTokenHeader.startsWith("Bearer ")) {
			String token = accessTokenHeader.substring(7);

			try {
				// 토큰이 유효한 경우에 수행할 작업
				jwtTokenProvider.validateToken(token);

				Map<String, String> payload = jwtTokenProvider.getPayload(token);

				Long userId = Long.valueOf(payload.get("userId"));

				System.out.println("필터 userId = " + userId);


				// 권한 부여
				log.info("=====권한 부여=====");
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority(Authority.ROLE_USER.name())));


				// Detail을 넣어줌
				// userId와 uuid를 추가로 넣어줌
				authenticationToken.setDetails(new CustomAuthenticationDetails(userId, request));

				SecurityContextHolder.getContext().setAuthentication(authenticationToken);

				log.info("[+] Token in SecurityContextHolder");
				filterChain.doFilter(request, response);

			} catch (ExpiredJwtException e) {
				// 토큰이 만료된 경우에 수행할 작업
				// refresh 토큰이 있으면 검증하고 access 토큰 재발급
				log.error("=====만료된 토큰입니다=====", e);
				throw e;

//				errorResponse(response, "jwt필터 - 만료된 토큰입니다", HttpServletResponse.SC_UNAUTHORIZED);

			} catch (MalformedJwtException e) {
				// 토큰 형식이 잘못된 경우에 수행할 작업
				log.error("=====잘못된 형식의 토큰=====", e);
				throw e;
//				errorResponse(response, "jwt필터 - 잘못된 형식의 토큰입니다", HttpServletResponse.SC_BAD_REQUEST);

			} catch (JwtException e) {
				// 기타 예외 처리
				log.error("=====유효하지 않은 토큰입니다=====", e);
				throw e;
//				errorResponse(response, "jwt필터 - 유효하지 않은 토큰입니다", HttpServletResponse.SC_FORBIDDEN);
			}


		}

	}


	//	private boolean userHasPermission(String userId) {
	//		// 여기에서 해당 사용자의 권한을 확인하는 로직 구현
	//		// userId를 기반으로 데이터베이스 등에서 해당 사용자의 권한 정보를 가져와서 확인
	//		// 권한이 있다면 true를 반환, 권한이 없다면 false를 반환
	//		return true; // 또는 false
	//	}
	//}


	private void errorResponse(HttpServletResponse response, String errMsg, int httpStatus) throws IOException {
		response.setStatus(httpStatus);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("{\"error\": \"" + errMsg + "\"}");
	}
}
