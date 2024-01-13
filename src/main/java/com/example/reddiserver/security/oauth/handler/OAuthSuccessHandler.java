package com.example.reddiserver.security.oauth.handler;

import com.example.reddiserver.security.jwt.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Access, Refresh Token Body 저장
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        String tokenDto = mapper.writeValueAsString(tokenProvider.createAccessToken(authentication));
        response.getWriter().write(tokenDto);

        response.getWriter().flush();
    }
}
