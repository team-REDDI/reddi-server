package com.example.reddiserver.service;

import com.example.reddiserver.dto.auth.request.ReissueRequestDto;
import com.example.reddiserver.dto.security.response.TokenDto;
import com.example.reddiserver.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final TokenProvider tokenProvider;

    @Transactional
    public TokenDto reissue(ReissueRequestDto reissueRequestDto) {
        if (!tokenProvider.refreshTokenValidation(reissueRequestDto.getRefreshToken())) {
            throw new RuntimeException("유효하지 않은 Refresh Token 입니다.");
        }

        Authentication authentication = tokenProvider.getAuthentication(reissueRequestDto.getAccessToken());

        return tokenProvider.createAccessToken(authentication);
    }
}
